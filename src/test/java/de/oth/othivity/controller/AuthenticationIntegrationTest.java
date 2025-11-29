package de.oth.othivity.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private IPushNotificationService pushNotificationService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testRegistrationSuccess() throws Exception {
        mockMvc.perform(post("/process-register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .param("matchingPassword", "password123")
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("username", "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        assertNotNull(userRepository.findByEmail("test@example.com"));
    }

    @Test
    void testRegistrationValidationFailure() throws Exception {
        mockMvc.perform(post("/process-register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "invalid-email")
                        .param("password", "123") // Too short
                        .param("matchingPassword", "123")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("username", ""))
                .andExpect(status().isOk()) // Should return to the form with errors
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasErrors("registerDto"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        // First register a user
        mockMvc.perform(post("/process-register")
                .with(csrf())
                .param("email", "login@example.com")
                .param("password", "password123")
                .param("matchingPassword", "password123")
                .param("firstName", "Login")
                .param("lastName", "User")
                .param("username", "loginuser"));

        // Then try to login
        mockMvc.perform(formLogin("/process-login")
                        .user("email", "login@example.com")
                        .password("password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(result -> {
                    MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
                    assertNotNull(session);
                    assertNotNull(session.getAttribute("profileId"));
                });
    }

    @Test
    void testLoginFailure() throws Exception {
        mockMvc.perform(formLogin("/process-login")
                        .user("email", "wrong@example.com")
                        .password("wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void testAccessProtectedResourceUnauthenticated() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testLogout() throws Exception {
        mockMvc.perform(logout())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    @Test
    void testRegistrationDuplicateEmail() throws Exception {
        // 1. Register first user
        mockMvc.perform(post("/process-register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "duplicate@example.com")
                        .param("password", "password123")
                        .param("matchingPassword", "password123")
                        .param("firstName", "First")
                        .param("lastName", "User")
                        .param("username", "firstuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        // 2. Try to register same email again
        mockMvc.perform(post("/process-register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "duplicate@example.com")
                        .param("password", "password123")
                        .param("matchingPassword", "password123")
                        .param("firstName", "Second")
                        .param("lastName", "User")
                        .param("username", "seconduser"))
                .andExpect(status().isOk()) // Should stay on page
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasErrors("registerDto"));
    }

    @Test
    void testRegistrationDuplicateUsername() throws Exception {
        // 1. Register first user
        mockMvc.perform(post("/process-register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "user1@example.com")
                        .param("password", "password123")
                        .param("matchingPassword", "password123")
                        .param("firstName", "First")
                        .param("lastName", "User")
                        .param("username", "uniqueuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        // 2. Try to register same username again (but different email)
        mockMvc.perform(post("/process-register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "user2@example.com") // Different email
                        .param("password", "password123")
                        .param("matchingPassword", "password123")
                        .param("firstName", "Second")
                        .param("lastName", "User")
                        .param("username", "uniqueuser")) // SAME username
                .andExpect(status().isOk()) // Should stay on page (validation error)
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registerDto", "username"));
    }
}