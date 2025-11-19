package de.oth.othivity.controller;

import de.oth.othivity.dto.RegisterRequest;
import de.oth.othivity.repository.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.test.context.TestConstructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@AllArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class AuthenticationIntegrationTest {

    private final MockMvc mockMvc;

    private final UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clean up before each test if necessary, though @Transactional handles rollback
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
                        .param("lastName", "User"))
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
                        .param("lastName", ""))
                .andExpect(status().isOk()) // Should return to the form with errors
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasErrors("registerRequest"));
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
                .param("lastName", "User"));

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
                        .param("lastName", "User"))
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
                        .param("lastName", "User"))
                .andExpect(status().isOk()) // Should stay on page
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasErrors("registerRequest"));
    }
}
