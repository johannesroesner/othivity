package de.oth.othivity.api.controller;

import de.oth.othivity.api.dto.ClubApiDto;
import de.oth.othivity.api.service.EntityConverter;
import de.oth.othivity.dto.ClubDto;
import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ClubService;
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

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/clubs")
@Tag(name = "Clubs", description = "Club Management API")
@SecurityRequirement(name = "Bearer Authentication")
public class ClubRestController {

    private final ClubService clubService;
    private final EntityConverter entityConverter;
    private final ProfileService profileService;

    @Operation(summary = "Get all clubs", description = "Returns a list of all clubs in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of clubs",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClubApiDto.class)))
    })
    @GetMapping
    public List<ClubApiDto> getAllClubs() {
        return clubService.getAllClubs().stream().map(entityConverter::ClubToApiDto).toList();
    }

    @Operation(summary = "Create a new club", description = "Creates a new club with the provided details. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Club successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClubApiDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    @PostMapping
    public ResponseEntity<Object> createClub(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ClubApiDto apiDto) {
        String email = userDetails.getUsername();
        Profile profile = profileService.getProfileByEmail(email);
        if (profile == null) {
            return ResponseEntity
                    .status(401)
                    .body("error: unauthorized");
        }

        ClubDto clubDto;
        try {
            clubDto = entityConverter.ApiDtoToClubDto(apiDto);
        } catch (Exception error) {
            return ResponseEntity
                    .status(400)
                    .body("error: " + error.getMessage());
        }

        Club createdClub = clubService.createClubForUser(clubDto, profile, null);

        Club club = clubService.getClubById(createdClub.getId());
        if (club == null) return ResponseEntity.internalServerError().body("error: internal server error");

        if (apiDto.getMembers() != null) {
            for (String s : apiDto.getMembers()) {
                if (s.equals(profile.getId().toString())) continue;
                UUID memberId = UUID.fromString(s);
                Profile member = profileService.getProfileById(memberId);
                if (member != null) {
                    clubService.joinClubForProfile(null, club);
                }
            }
        }

        if (apiDto.getAdmins() != null) {
            for (String s : apiDto.getAdmins()) {
                if (s.equals(profile.getId().toString())) continue;
                UUID adminId = UUID.fromString(s);
                Profile admin = profileService.getProfileById(adminId);
                if (admin != null && club.getMembers().contains(admin)) {
                    clubService.makeProfileAdminOfClub(admin, club, null);
                }
            }
        }

        return ResponseEntity.status(201).body(entityConverter.ClubToApiDto(clubService.getClubById(club.getId())));
    }

    @Operation(summary = "Update a club", description = "Updates an existing club. Only club admins can update the club.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Club successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClubApiDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not an admin of this club"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateClub(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Club ID", required = true) @PathVariable UUID id,
            @RequestBody ClubApiDto apiDto) {
        Club club = clubService.getClubById(id);
        if(club == null) {
            return ResponseEntity
                    .status(404)
                    .body("error: club not found");
        }
        String email = userDetails.getUsername();
        Profile profile = profileService.getProfileByEmail(email);
        if (profile == null || (!club.getAdmins().contains(profile) && !profile.getRole().equals(Role.MODERATOR))) {
            return ResponseEntity
                    .status(403)
                    .body("error: unauthorized");
        }

        ClubDto clubDto;
        try {
            clubDto = entityConverter.ApiDtoToClubDto(apiDto);
        } catch (Exception error) {
            return ResponseEntity
                    .status(400)
                    .body("error: " + error.getMessage());
        }

        club = clubService.updateClub(club, clubDto, null, null);
        if (club == null) return ResponseEntity.internalServerError().body("error: internal server error");

        return ResponseEntity.status(200).body(entityConverter.ClubToApiDto(clubService.getClubById(club.getId())));
    }

    @Operation(summary = "Delete a club", description = "Deletes an existing club. Only club admins can delete the club.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Club successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not an admin of this club"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClub(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Club ID", required = true) @PathVariable UUID id) {
        Club club = clubService.getClubById(id);
        if(club == null) {
            return ResponseEntity
                    .status(404)
                    .body("error: club not found");
        }

        String email = userDetails.getUsername();
        Profile profile = profileService.getProfileByEmail(email);
        if (profile == null || (!club.getAdmins().contains(profile) && !profile.getRole().equals(Role.MODERATOR))) {
            return ResponseEntity
                    .status(403)
                    .body("error: forbidden");
        }

        clubService.deleteClub(club, profile);
        return ResponseEntity.status(204).body("success");
    }

    @Operation(summary = "Get club by ID", description = "Returns details of a specific club by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved club",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClubApiDto.class))),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getClubById(@Parameter(description = "Club ID", required = true) @PathVariable UUID id) {
        Club club = clubService.getClubById(id);
        if(club == null) {
            return ResponseEntity
                    .status(404)
                    .body("error: club not found");
        }
        ClubApiDto response = entityConverter.ClubToApiDto(club);
        return ResponseEntity.status(200).body(response);
    }
}
