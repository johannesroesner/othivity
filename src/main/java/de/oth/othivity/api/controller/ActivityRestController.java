package de.oth.othivity.api.controller;

import de.oth.othivity.api.dto.ActivityApiDto;
import de.oth.othivity.api.service.EntityConverter;
import de.oth.othivity.dto.ActivityDto;
import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/activities")
@Tag(name = "Activities", description = "Activity Management API")
@SecurityRequirement(name = "Bearer Authentication")
public class ActivityRestController {

    private final ActivityService activityService;

    private final EntityConverter entityConverter;
    private final ProfileService profileService;

    @Operation(summary = "Get all activities", description = "Returns a list of all activities in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of activities",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityApiDto.class)))
    })
    @GetMapping
    public List<ActivityApiDto> getAllActivities() {
        return activityService.getAllActivities().stream().map(entityConverter::ActivityToApiDto).toList();
    }

    @Operation(summary = "Create a new activity", description = "Creates a new activity with the provided details. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Activity successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityApiDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @PostMapping
    public ResponseEntity<Object> createActivity(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ActivityApiDto apiDto) {
        String email = userDetails.getUsername();
        Profile profile = profileService.getProfileByEmail(email);
        if (profile == null) {
            return ResponseEntity
                    .status(403)
                    .body("error: forbidden");
        }

        ActivityDto activityDto;
        try {
            activityDto = entityConverter.ApiDtoToActivityDto(apiDto);
        } catch (Exception error) {
            return ResponseEntity
                    .status(400)
                    .body("error: " + error.getMessage());
        }

        Activity createdActivity = activityService.createActivity(activityDto, null, profile);

        Activity activity = activityService.getActivityById(createdActivity.getId());
        if (activity == null) return ResponseEntity.internalServerError().body("error: internal server error");

        if (apiDto.getTakePart() != null) {
            for (String s : apiDto.getTakePart()) {
                if (s.equals(profile.getId().toString())) continue;
                    UUID participantId = UUID.fromString(s);
                    Profile participant = profileService.getProfileById(participantId);
                    if (participant != null) {
                        activityService.joinActivity(activity, participant);
                    }
            }
        }

        return ResponseEntity.status(201).body(entityConverter.ActivityToApiDto(activityService.getActivityById(activity.getId())));
    }

    @Operation(summary = "Update an activity", description = "Updates an existing activity. Only activity participants can update the activity.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityApiDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not a participant of this activity"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Activity ID", required = true) @PathVariable UUID id,
            @RequestBody ActivityApiDto apiDto) {
        Activity activity = activityService.getActivityById(id);
        if(activity == null) {
            return ResponseEntity
                    .status(404)
                    .body("error: activity not found");
        }
        String email = userDetails.getUsername();
        Profile profile = profileService.getProfileByEmail(email);
        if (profile == null || (!activity.getStartedBy().getId().equals(profile.getId()) && !profile.getRole().equals(Role.MODERATOR))) {
            return ResponseEntity
                    .status(403)
                    .body("error: forbidden");
        }

        ActivityDto activityDto;
        try {
            activityDto = entityConverter.ApiDtoToActivityDto(apiDto);
            if(apiDto.getStartedBy() == null) throw new IllegalArgumentException("startedBy is null");
            if (apiDto.getTakePart() == null || apiDto.getTakePart().length == 0) throw new IllegalArgumentException("takePart array is null or empty.");
            if(Arrays.stream((String[]) apiDto.getTakePart()).noneMatch(takePartId -> takePartId.equals(apiDto.getStartedBy()))) throw  new IllegalArgumentException("startedBy has to be in takePart array.");
        } catch (Exception error) {
            return ResponseEntity
                    .status(400)
                    .body("error: " + error.getMessage());
        }

        activity = activityService.updateActivity(activity,activityDto,null,profile);
        activity = activityService.resetTakePart(activity);
        if (activity == null) return ResponseEntity.internalServerError().body("error: internal server error");
        for (String s : apiDto.getTakePart()) {
            UUID participantId = UUID.fromString(s);
            Profile participant = profileService.getProfileById(participantId);
            if (participant != null) {
                activityService.joinActivity(activity, participant);
            }
        }


        return ResponseEntity.status(200).body(entityConverter.ActivityToApiDto(activityService.getActivityById(activity.getId())));
    }

    @Operation(summary = "Delete an activity", description = "Deletes an existing activity. Only the activity creator or moderators can delete the activity.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activity successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not the creator or a moderator"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @DeleteMapping("/{id}")
    public  ResponseEntity<Object> deleteMapping(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Activity ID", required = true) @PathVariable UUID id) {
        Activity activity = activityService.getActivityById(id);
        if(activity == null) {
            return ResponseEntity
                    .status(404)
                    .body("error: activity not found");
        }

        String email = userDetails.getUsername();
        Profile profile = profileService.getProfileByEmail(email);
        if (profile == null || (!activity.getStartedBy().getId().equals(profile.getId()) && !profile.getRole().equals(Role.MODERATOR))) {
            return ResponseEntity
                    .status(403)
                    .body("error: forbidden");
        }

        activityService.deleteActivity(activity);
        return ResponseEntity.status(204).body("success");
    }

    @Operation(summary = "Get activity by ID", description = "Returns details of a specific activity by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved activity",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityApiDto.class))),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @GetMapping("/{id}")
    public  ResponseEntity<Object> getActivityById(@Parameter(description = "Activity ID", required = true) @PathVariable UUID id) {
        Activity activity = activityService.getActivityById(id);
        if(activity == null) {
            return ResponseEntity
                    .status(404)
                    .body("error: activity not found");
        }
        ActivityApiDto response = entityConverter.ActivityToApiDto(activity);
        return ResponseEntity.status(200).body(response);
    }

}
