package de.oth.othivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.oth.othivity.api.dto.ActivityApiDto;
import de.oth.othivity.api.dto.ClubApiDto;
import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
@AllArgsConstructor
@ActiveProfiles("test")
public class TestUtil {

    private final ProfileService profileService;

    private final ProfileRepository profileRepository;

    private final ObjectMapper objectMapper;

    // activity api dto helper method
    public ActivityApiDto createValidActivityApiDto() throws Exception {
        ActivityApiDto apiDto = new ActivityApiDto();
        apiDto.setTitle("Test Activity");
        apiDto.setDescription("Test Description");
        apiDto.setLanguage("ENGLISH");
        apiDto.setGroupSize(5);
        apiDto.setDate("2025-12-03T12:00:00");
        apiDto.setTags(new String[]{"OUTDOOR"});
        apiDto.setStreet("Main Street");
        apiDto.setHouseNumber("1");
        apiDto.setCity("Test City");
        apiDto.setPostalCode("12345");
        apiDto.setCountry("Testland");
        apiDto.setImageUrl("http://test.com/image.jpg");
        return apiDto;
    }

    // club api dto helper method
    public ClubApiDto createValidClubApiDto() throws Exception {
        ClubApiDto apiDto = new ClubApiDto();
        apiDto.setName("Test Club");
        apiDto.setDescription("Test Club Description");
        apiDto.setAccessLevel("OPEN");
        apiDto.setStreet("Main Street");
        apiDto.setHouseNumber("1");
        apiDto.setCity("Test City");
        apiDto.setPostalCode("12345");
        apiDto.setCountry("Testland");
        apiDto.setImageUrl("http://test.com/club-image.jpg");
        return apiDto;
    }

    // json helper methods
    public String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    public String extractIdFromResponse(String response) {
        try {
            JsonNode node = objectMapper.readTree(response);
            return node.get("id").asText();
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    // register a test user with fixed credentials
    public Profile registerTestUser(MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/process-register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("matchingPassword", "password")
                        .param("firstName", "test")
                        .param("lastName", "test")
                        .param("username", "test"))
                .andExpect(status().is3xxRedirection());
        return profileService.getProfileByEmail("test@example.com");
    }

    // register a user with given credentials
    public Profile registerUser(MockMvc mockMvc, String username, String email, String password) throws Exception {
        mockMvc.perform(post("/process-register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
                        .param("password", password)
                        .param("matchingPassword", password)
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("username", username))
                .andExpect(status().is3xxRedirection());
        return profileService.getProfileByEmail(email);
    }

    // login a user and return the session
    public MockHttpSession loginUser(MockMvc mockMvc, String email, String password) throws Exception {
        return (MockHttpSession) mockMvc.perform(formLogin("/process-login")
                        .user("email", email)
                        .password(password))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getRequest()
                .getSession();
    }

    // make a user a moderator
    public void makeModerator(Profile profile) throws Exception {
        profile.setRole(Role.MODERATOR);
        profileRepository.save(profile);
    }

    // test image
    public MockMultipartFile setValidImageFile() throws Exception {
        return new MockMultipartFile(
                "uploadedImage",
                "test-image.png",
                "image/png",
                Files.readAllBytes(Paths.get("src/test/resources/test-image.png"))
        );
    }
}
