package de.oth.othivity.api;

import de.oth.othivity.TestUtil;
import de.oth.othivity.api.dto.ProfileApiDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ProfileRestIntegrationTest {

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

    private ProfileApiDto createValidProfileApiDto(String username, String email) {
        ProfileApiDto dto = new ProfileApiDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("password123");
        dto.setAboutMe("Hello World");
        dto.setLanguage("ENGLISH");
        dto.setTheme("LIGHT");
        return dto;
    }

    @Test
    void getAllProfiles_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/profiles/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(200))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getProfileMe_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/profiles/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    void getProfileByUsername_asNormalUser_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/profiles/test")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(403));
    }

    @Test
    void getProfileByUsername_asModerator_shouldReturn200() throws Exception {
        Profile modProfile = testUtil.registerUser(mockMvc, "modUser", "mod@example.com", "password");
        testUtil.makeModerator(modProfile);
        String modToken = apiTokenService.createToken(modProfile, "modToken", 12);

        mockMvc.perform(get("/api/profiles/test")
                        .header("Authorization", "Bearer " + modToken))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    void getProfileByUsername_notFound_shouldReturn404() throws Exception {
        Profile modProfile = testUtil.registerUser(mockMvc, "modUser", "mod@example.com", "password");
        testUtil.makeModerator(modProfile);
        String modToken = apiTokenService.createToken(modProfile, "modToken", 12);

        mockMvc.perform(get("/api/profiles/gibtsnicht")
                        .header("Authorization", "Bearer " + modToken))
                .andExpect(status().is(404));
    }

    @Test
    void createProfile_asModerator_shouldReturn201() throws Exception {
        Profile modProfile = testUtil.registerUser(mockMvc, "modUser", "mod@example.com", "password");
        testUtil.makeModerator(modProfile);
        String modToken = apiTokenService.createToken(modProfile, "modToken", 12);

        ProfileApiDto newProfile = createValidProfileApiDto("newUser", "new@example.com");

        mockMvc.perform(post("/api/profiles")
                        .header("Authorization", "Bearer " + modToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(newProfile)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void createProfile_asNormalUser_shouldReturn401() throws Exception {
        ProfileApiDto newProfile = createValidProfileApiDto("hacker", "hacker@example.com");

        mockMvc.perform(post("/api/profiles")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(newProfile)))
                .andExpect(status().is(401));
    }

    @Test
    void createProfile_duplicateEmail_shouldReturn409() throws Exception {
        Profile modProfile = testUtil.registerUser(mockMvc, "modUser", "mod@example.com", "password");
        testUtil.makeModerator(modProfile);
        String modToken = apiTokenService.createToken(modProfile, "modToken", 12);

        ProfileApiDto duplicateDto = createValidProfileApiDto("dupUser", "test@example.com");

        mockMvc.perform(post("/api/profiles")
                        .header("Authorization", "Bearer " + modToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(duplicateDto)))
                .andExpect(status().is(409));
    }

    @Test
    void updateProfile_asOwner_shouldReturn200() throws Exception {
        ProfileApiDto updateDto = new ProfileApiDto();
        updateDto.setFirstName(profile.getFirstName());
        updateDto.setLastName(profile.getLastName());
        updateDto.setEmail(profile.getEmail().getAddress());
        updateDto.setAboutMe("Updated Bio via API");
        updateDto.setTheme("DARK");

        mockMvc.perform(put("/api/profiles/test")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(updateDto)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.aboutMe").value("Updated Bio via API"));

        Profile updated = profileRepository.findByUsername("test");
        assertEquals("DARK", updated.getTheme().name());
    }

    @Test
    void updateProfile_changeImmutableFields_shouldReturn400() throws Exception {
        ProfileApiDto updateDto = new ProfileApiDto();
        updateDto.setFirstName("ChangedName");

        mockMvc.perform(put("/api/profiles/test")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(updateDto)))
                .andExpect(status().is(400))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("first name cannot be changed")));
    }

    @Test
    void updateProfile_asModerator_shouldReturn200() throws Exception {
        Profile modProfile = testUtil.registerUser(mockMvc, "modUser", "mod@example.com", "password");
        testUtil.makeModerator(modProfile);
        String modToken = apiTokenService.createToken(modProfile, "modToken", 12);

        ProfileApiDto updateDto = new ProfileApiDto();
        updateDto.setFirstName(profile.getFirstName());
        updateDto.setLastName(profile.getLastName());
        updateDto.setEmail(profile.getEmail().getAddress());
        updateDto.setAboutMe("Moderated Content");

        mockMvc.perform(put("/api/profiles/test")
                        .header("Authorization", "Bearer " + modToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(updateDto)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.aboutMe").value("Moderated Content"));
    }

    @Test
    void updateProfile_unauthorized_shouldReturn403() throws Exception {
        Profile otherProfile = testUtil.registerUser(mockMvc, "other", "other@example.com", "password");
        String otherToken = apiTokenService.createToken(otherProfile, "otherToken", 12);

        ProfileApiDto updateDto = new ProfileApiDto();
        updateDto.setAboutMe("Hacked");

        mockMvc.perform(put("/api/profiles/test")
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType("application/json")
                        .content(testUtil.asJsonString(updateDto)))
                .andExpect(status().is(403));
    }

    @Test
    void deleteProfile_asOwner_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/profiles/test")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is(204));
    }

    @Test
    void deleteProfile_asModerator_shouldReturn204() throws Exception {
        Profile modProfile = testUtil.registerUser(mockMvc, "modUser", "mod@example.com", "password");
        testUtil.makeModerator(modProfile);
        String modToken = apiTokenService.createToken(modProfile, "modToken", 12);

        mockMvc.perform(delete("/api/profiles/test")
                        .header("Authorization", "Bearer " + modToken))
                .andExpect(status().is(204));
    }

    @Test
    void deleteProfile_unauthorized_shouldReturn403() throws Exception {
        Profile otherProfile = testUtil.registerUser(mockMvc, "other", "other@example.com", "password");
        String otherToken = apiTokenService.createToken(otherProfile, "otherToken", 12);

        mockMvc.perform(delete("/api/profiles/test")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().is(403));
    }
}