package de.oth.othivity.controller;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.service.IPushNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;
    @MockBean
    private IPushNotificationService pushNotificationService;

    @BeforeEach
    void setUp() {
        // Transactional handles rollback
    }

    private MockHttpSession loginUser(String email, String password) throws Exception {
        return (MockHttpSession) mockMvc.perform(formLogin("/process-login")
                        .user("email", email)
                        .password(password))
                .andExpect(status().is3xxRedirection())
                .andReturn().getRequest().getSession();
    }

    private void registerUser(String username, String email, String password) throws Exception {
        mockMvc.perform(post("/process-register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
                        .param("password", password)
                        .param("matchingPassword", password)
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("username", username))
                .andExpect(status().is3xxRedirection());
    }

    private void makeModerator(String username) {
        Profile profile = profileRepository.findByusername(username);
        profile.setRole(Role.MODERATOR);
        profileRepository.save(profile);
    }

    @Test
    void testUserCanViewAndEditOwnProfile() throws Exception {
        registerUser("user1", "user1@example.com", "password");
        MockHttpSession session = loginUser("user1@example.com", "password");

        // View Profile
        mockMvc.perform(get("/profile/user1").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isOwnProfile", true))
                .andExpect(model().attribute("canUpdate", true));

        // Edit Profile
        mockMvc.perform(post("/profile/edit/user1")
                        .session(session)
                        .with(csrf())
                        .param("aboutMe", "New Bio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/user1"));

        Profile updatedProfile = profileRepository.findByusername("user1");
        assertEquals("New Bio", updatedProfile.getAboutMe());
    }

    @Test
    void testUserCanViewButNotEditOtherProfile() throws Exception {
        registerUser("user1", "user1@example.com", "password");
        registerUser("user2", "user2@example.com", "password");
        
        MockHttpSession session = loginUser("user1@example.com", "password");

        // View Other Profile
        mockMvc.perform(get("/profile/user2").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isOwnProfile", false))
                .andExpect(model().attribute("canUpdate", false))
                .andExpect(model().attribute("canDelete", false));
    }

    @Test
    void testUserCanDeleteOwnProfileInSettings() throws Exception {
        registerUser("user1", "user1@example.com", "password");
        MockHttpSession session = loginUser("user1@example.com", "password");

        // Delete Profile
        mockMvc.perform(post("/profile/delete/user1")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertNull(profileRepository.findByusername("user1"));
    }

    @Test
    void testModeratorCanViewAndEditOwnProfile() throws Exception {
        registerUser("mod1", "mod1@example.com", "password");
        makeModerator("mod1");
        MockHttpSession session = loginUser("mod1@example.com", "password");

        // View Own Profile
        mockMvc.perform(get("/profile/mod1").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isOwnProfile", true))
                .andExpect(model().attribute("canUpdate", true));
        
        // Edit Own Profile
        mockMvc.perform(post("/profile/edit/mod1")
                        .session(session)
                        .with(csrf())
                        .param("aboutMe", "Mod Bio"))
                .andExpect(status().is3xxRedirection());
                
        Profile updatedProfile = profileRepository.findByusername("mod1");
        assertEquals("Mod Bio", updatedProfile.getAboutMe());
    }

    @Test
    void testModeratorCanEditAndDeleteOtherProfile() throws Exception {
        registerUser("mod1", "mod1@example.com", "password");
        makeModerator("mod1");
        registerUser("user1", "user1@example.com", "password");
        
        MockHttpSession session = loginUser("mod1@example.com", "password");

        // View Other Profile
        mockMvc.perform(get("/profile/user1").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isOwnProfile", false))
                .andExpect(model().attribute("canUpdate", true)) // Mod CAN edit others
                .andExpect(model().attribute("canDelete", true)); // Mod can delete others

        // Edit Other Profile
        mockMvc.perform(post("/profile/edit/user1")
                        .session(session)
                        .with(csrf())
                        .param("aboutMe", "Edited by Mod"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/user1"));

        Profile updatedProfile = profileRepository.findByusername("user1");
        assertEquals("Edited by Mod", updatedProfile.getAboutMe());

        // Delete Other Profile
        mockMvc.perform(post("/profile/delete/user1")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()); // Redirects to dashboard or referer

        assertNull(profileRepository.findByusername("user1"));
        assertNotNull(profileRepository.findByusername("mod1")); // Mod still exists
    }
}