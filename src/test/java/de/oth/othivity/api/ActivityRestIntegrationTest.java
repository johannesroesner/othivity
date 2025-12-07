package de.oth.othivity.api;

import de.oth.othivity.TestUtil;
import de.oth.othivity.api.dto.ActivityApiDto;
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
class ActivityRestIntegrationTest {

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
    void  setUp() throws Exception {
        profile =  testUtil.registerTestUser(mockMvc);
        jwtToken = apiTokenService.createToken(profile,"testToken",12);
    }

    @Test
    void getAllActivities_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(200))
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void createActivity_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(testUtil.createValidActivityApiDto())))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.title").value("Test Activity"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void createActivity_withInvalidData_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(new ActivityApiDto())))
                .andExpect(status().is(400))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }

    @Test
    void getActivityById_shouldReturn200() throws Exception {
        String response = mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(testUtil.createValidActivityApiDto())))
                .andReturn().getResponse().getContentAsString();

        String activityId = testUtil.extractIdFromResponse(response);

        mockMvc.perform(get("/api/activities/" + activityId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(activityId));
    }

    @Test
    void getActivityById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/activities/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(404))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }


    @Test
    void updateActivity_asOwner_shouldReturn200() throws Exception {
        ActivityApiDto apiDto = testUtil.createValidActivityApiDto();
        String response = mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String activityId = testUtil.extractIdFromResponse(response);
        apiDto.setTitle("Updated Title");
        apiDto.setStartedBy(profile.getId().toString());
        apiDto.setTakePart(new String[] {profile.getId().toString()});

        mockMvc.perform(put("/api/activities/" + activityId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }


    @Test
    void updateActivity_forbidden_shouldReturn403() throws Exception {
        ActivityApiDto apiDto = testUtil.createValidActivityApiDto();
        String response = mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String activityId = testUtil.extractIdFromResponse(response);
        apiDto.setTitle("Updated Title");
        apiDto.setStartedBy(profile.getId().toString());
        apiDto.setTakePart(new String[] {profile.getId().toString()});

        Profile otherProfile = testUtil.registerUser(mockMvc, "otherUser","otherUser@example.com", "password");
        String otherToken = apiTokenService.createToken(otherProfile,"testToken",12);

        mockMvc.perform(put("/api/activities/" + activityId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andExpect(status().is(403))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }

    @Test
    void updateActivity_asModerator_shouldReturn200() throws Exception {
        ActivityApiDto apiDto = testUtil.createValidActivityApiDto();
        String response = mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String activityId = testUtil.extractIdFromResponse(response);
        apiDto.setTitle("Updated Title");
        apiDto.setStartedBy(profile.getId().toString());
        apiDto.setTakePart(new String[] {profile.getId().toString()});

        Profile otherProfile = testUtil.registerUser(mockMvc, "otherUser","otherUser@example.com", "password");
        testUtil.makeModerator(otherProfile);
        String otherToken = apiTokenService.createToken(otherProfile,"testToken",12);

        mockMvc.perform(put("/api/activities/" + activityId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void deleteActivity_asOwner_shouldReturn204() throws Exception {
        ActivityApiDto apiDto = testUtil.createValidActivityApiDto();
        String response = mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String activityId = testUtil.extractIdFromResponse(response);

        mockMvc.perform(delete("/api/activities/" + activityId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(204));
    }


    @Test
    void deleteActivity_forbidden_shouldReturn403() throws Exception {
        ActivityApiDto apiDto = testUtil.createValidActivityApiDto();
        String response = mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String activityId = testUtil.extractIdFromResponse(response);

        Profile otherProfile = testUtil.registerUser(mockMvc, "otherUser","otherUser@example.com", "password");
        String otherToken = apiTokenService.createToken(otherProfile,"testToken",12);

        mockMvc.perform(delete("/api/activities/" + activityId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().is(403))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }

    @Test
    void deleteActivity_asModerator_shouldReturn204() throws Exception {
        ActivityApiDto apiDto = testUtil.createValidActivityApiDto();
        String response = mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(apiDto)))
                .andReturn().getResponse().getContentAsString();

        String activityId = testUtil.extractIdFromResponse(response);

        Profile otherProfile = testUtil.registerUser(mockMvc, "otherUser","otherUser@example.com", "password");
        testUtil.makeModerator(otherProfile);
        String otherToken = apiTokenService.createToken(otherProfile,"testToken",12);

        mockMvc.perform(delete("/api/activities/" + activityId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().is(204));
    }
}
