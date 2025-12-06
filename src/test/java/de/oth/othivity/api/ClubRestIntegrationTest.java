package de.oth.othivity.api;

import de.oth.othivity.TestUtil;
import de.oth.othivity.api.dto.ClubApiDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.impl.ApiTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ClubRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ApiTokenService apiTokenService;

    @Autowired
    private ProfileRepository profileRepository;

    private Profile profile;
    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        profile = testUtil.registerTestUser(mockMvc);
        jwtToken = apiTokenService.createToken(profile, "testToken", 12);
    }

    @Test
    void getAllClubs_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(200))
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void createClub_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(testUtil.createValidClubApiDto())))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.name").value("Test Club"))
                .andExpect(jsonPath("$.description").value("Test Club Description"));
    }

    @Test
    void createClub_withInvalidData_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(new ClubApiDto())))
                .andExpect(status().is(400))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }

    @Test
    void getClubById_shouldReturn200() throws Exception {
        String response = mockMvc.perform(post("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(testUtil.createValidClubApiDto())))
                .andReturn().getResponse().getContentAsString();

        String clubId = testUtil.extractIdFromResponse(response);

        mockMvc.perform(get("/api/clubs/" + clubId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(clubId));
    }

    @Test
    void getClubById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/clubs/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(404))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }

    @Test
    void updateClub_asAdmin_shouldReturn200() throws Exception {
        ClubApiDto apiDto = testUtil.createValidClubApiDto();
        String response = mockMvc.perform(post("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String clubId = testUtil.extractIdFromResponse(response);
        apiDto.setName("Updated Club Name");

        mockMvc.perform(put("/api/clubs/" + clubId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value("Updated Club Name"));
    }

    @Test
    void updateClub_unauthorized_shouldReturn401() throws Exception {
        ClubApiDto apiDto = testUtil.createValidClubApiDto();
        String response = mockMvc.perform(post("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String clubId = testUtil.extractIdFromResponse(response);
        apiDto.setName("Updated Club Name");

        Profile otherProfile = testUtil.registerUser(mockMvc, "otherUser", "otherUser@example.com", "password");
        String otherToken = apiTokenService.createToken(otherProfile, "testToken", 12);

        mockMvc.perform(put("/api/clubs/" + clubId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andExpect(status().is(401))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }

    @Test
    void updateClub_asModerator_shouldReturn200() throws Exception {
        ClubApiDto apiDto = testUtil.createValidClubApiDto();
        String response = mockMvc.perform(post("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String clubId = testUtil.extractIdFromResponse(response);
        apiDto.setName("Updated Club Name");

        Profile otherProfile = testUtil.registerUser(mockMvc, "otherUser", "otherUser@example.com", "password");
        testUtil.makeModerator(otherProfile);
        String otherToken = apiTokenService.createToken(otherProfile, "testToken", 12);

        mockMvc.perform(put("/api/clubs/" + clubId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value("Updated Club Name"));
    }

    @Test
    void deleteClub_asAdmin_shouldReturn204() throws Exception {
        ClubApiDto apiDto = testUtil.createValidClubApiDto();
        String response = mockMvc.perform(post("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String clubId = testUtil.extractIdFromResponse(response);

        mockMvc.perform(delete("/api/clubs/" + clubId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(204));
    }

    @Test
    void deleteClub_unauthorized_shouldReturn401() throws Exception {
        ClubApiDto apiDto = testUtil.createValidClubApiDto();
        String response = mockMvc.perform(post("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String clubId = testUtil.extractIdFromResponse(response);

        Profile otherProfile = testUtil.registerUser(mockMvc, "otherUser", "otherUser@example.com", "password");
        String otherToken = apiTokenService.createToken(otherProfile, "testToken", 12);

        mockMvc.perform(delete("/api/clubs/" + clubId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().is(401))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }

    @Test
    void deleteClub_asModerator_shouldReturn204() throws Exception {
        ClubApiDto apiDto = testUtil.createValidClubApiDto();
        String response = mockMvc.perform(post("/api/clubs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String clubId = testUtil.extractIdFromResponse(response);

        Profile otherProfile = testUtil.registerUser(mockMvc, "otherUser", "otherUser@example.com", "password");
        testUtil.makeModerator(otherProfile);
        String otherToken = apiTokenService.createToken(otherProfile, "testToken", 12);

        mockMvc.perform(delete("/api/clubs/" + clubId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().is(204));
    }
}
