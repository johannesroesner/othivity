package de.oth.othivity.api.controller;

import de.oth.othivity.api.dto.ClubApiDto;
import de.oth.othivity.api.service.EntityConverter;
import de.oth.othivity.dto.ClubDto;
import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.ProfileService;
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
public class ClubRestController {

    private final ClubService clubService;
    private final EntityConverter entityConverter;
    private final ProfileService profileService;

    @GetMapping
    public List<ClubApiDto> getAllClubs() {
        return clubService.getAllClubs().stream().map(entityConverter::ClubToApiDto).toList();
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateClub(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id, @RequestBody ClubApiDto apiDto) {
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

        club = clubService.updateClub(club, clubDto, null, null);
        if (club == null) return ResponseEntity.internalServerError().body("error: internal server error");

        return ResponseEntity.status(200).body(entityConverter.ClubToApiDto(clubService.getClubById(club.getId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClub(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id) {
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
                    .status(401)
                    .body("error: unauthorized");
        }

        clubService.deleteClub(club, profile);
        return ResponseEntity.status(204).body("success");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getClubById(@PathVariable UUID id) {
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
