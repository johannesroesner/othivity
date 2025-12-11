package de.oth.othivity.controller;

import de.oth.othivity.TestUtil;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Theme;
import de.oth.othivity.model.helper.VerificationToken;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.ApiToken;
import de.oth.othivity.repository.helper.VerificationTokenRepository;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.security.ApiTokenRepository;
import de.oth.othivity.service.IPushNotificationService;
import de.oth.othivity.service.IVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private ApiTokenRepository apiTokenRepository;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private IVerificationService IVerificationService;

    @MockBean
    private IPushNotificationService pushNotificationService;

    private MockHttpSession session;
    private Profile mainUser;

    @BeforeEach
    void setUp() throws Exception {
        // Standard User für die meisten Tests registrieren und einloggen
        mainUser = testUtil.registerUser(mockMvc, "user1", "user1@example.com", "password");
        session = testUtil.loginUser(mockMvc, "user1@example.com", "password");
    }

    // --- SEARCH / OVERVIEW TESTS ---

    @Test
    void testSearchProfiles_success() throws Exception {
        mockMvc.perform(get("/profiles")
                        .session(session)
                        .param("search", "user1"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-overview"))
                .andExpect(model().attributeExists("profiles"))
                .andExpect(model().attribute("search", "user1"));
    }

    @Test
    void testSearchProfiles_emptySearch_returnsEmpty() throws Exception {
        mockMvc.perform(get("/profiles")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-overview"));
    }

    @Test
    void testSearchProfiles_withoutSession_redirects() throws Exception {
        mockMvc.perform(get("/profiles"))
                .andExpect(status().is3xxRedirection());
    }

    // --- GET PROFILE DETAIL TESTS ---

    @Test
    void testUserCanViewOwnProfile() throws Exception {
        mockMvc.perform(get("/profile/user1").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("isOwnProfile", true))
                .andExpect(model().attribute("canUpdate", true));
    }

    @Test
    void testUserCanViewOtherProfile() throws Exception {
        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");

        mockMvc.perform(get("/profile/user2").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("isOwnProfile", false))
                .andExpect(model().attribute("canUpdate", false));
    }

    @Test
    void testGetProfile_notFound_redirects() throws Exception {
        mockMvc.perform(get("/profile/gibtesnicht").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    // --- EDIT PROFILE (GET FORM) TESTS ---

    @Test
    void testShowEditForm_owner_success() throws Exception {
        mockMvc.perform(get("/profile/edit/user1").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("profileDto"));
    }

    @Test
    void testShowEditForm_otherUser_redirects() throws Exception {
        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");

        mockMvc.perform(get("/profile/edit/user2").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/user2"));
    }

    @Test
    void testShowEditForm_moderator_success() throws Exception {
        Profile mod = testUtil.registerUser(mockMvc, "mod1", "mod1@example.com", "password");
        testUtil.makeModerator(mod);
        MockHttpSession modSession = testUtil.loginUser(mockMvc, "mod1@example.com", "password");

        mockMvc.perform(get("/profile/edit/user1").session(modSession))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"));
    }

    // --- UPDATE PROFILE (POST) TESTS ---

    @Test
    void testUpdateProfile_owner_success() throws Exception {
        // WICHTIG: email ist ein Objekt (Email), daher muss der Parameter "email.address" heißen!
        mockMvc.perform(multipart("/profile/edit/user1")
                        .session(session)
                        .with(csrf())
                        .param("firstName", "UpdatedFirst")
                        .param("lastName", "UpdatedLast")
                        .param("username", "user1")
                        .param("email.address", "user1@example.com")
                        .param("aboutMe", "New Bio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/user1"));

        Profile updated = profileRepository.findByUsername("user1");
        assertEquals("New Bio", updated.getAboutMe());
        assertEquals("UpdatedFirst", updated.getFirstName());
    }

    @Test
    void testUpdateProfile_withImage_success() throws Exception {
        // WICHTIG: email.address statt email
        mockMvc.perform(multipart("/profile/edit/user1")
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("username", "user1")
                        .param("email.address", "user1@example.com"))
                .andExpect(status().is3xxRedirection());

        Profile updated = profileRepository.findByUsername("user1");
        assertNotNull(updated.getImage());
    }

    @Test
    void testUpdateProfile_badInput_fails() throws Exception {
        // Beispiel: Leere Pflichtfelder (falls Validierung existiert)
        // Hier simulieren wir einen Fehler, indem wir ungültige Daten senden
        // Wir nehmen an, dass firstName nicht leer sein darf, wenn entsprechende Annotationen da wären.
        // Da wir das DTO nicht vollständig kennen, verlassen wir uns darauf, 
        // dass der Controller Fehler im BindingResult abfängt und die View zurückgibt.
        
        // Senden eines ungültigen Formats für ein Feld, z.B. ungültiges Telefonformat via Validator
        mockMvc.perform(multipart("/profile/edit/user1")
                        .session(session)
                        .with(csrf())
                        .param("firstName", "First")
                        .param("lastName", "Last")
                        .param("username", "user1")
                        .param("email.address", "user1@example.com")
                        .param("phone.number", "invalid-phone")) // Fehler im Validator
                .andExpect(status().isOk()) // Kein Redirect, sondern View Reload
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeHasFieldErrors("profileDto", "phone.number"));
    }

    @Test
    void testUpdateProfile_unauthorized_redirects() throws Exception {
        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");

        mockMvc.perform(multipart("/profile/edit/user2")
                        .session(session)
                        .with(csrf())
                        .param("aboutMe", "Hacked"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/user2"));
    }

    // --- DELETE PROFILE IMAGE ---

    @Test
    void testDeleteProfileImage_owner_success() throws Exception {
        mainUser.setImage(null);
        profileRepository.save(mainUser);

        mockMvc.perform(post("/profile/deleteImage/user1")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/edit/user1"));
    }

    @Test
    void testDeleteProfileImage_unauthorized_redirects() throws Exception {
        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");

        mockMvc.perform(post("/profile/deleteImage/user2")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/user2"));
    }

    // --- DELETE PROFILE TESTS ---

    @Test
    void testDeleteOwnProfile_success() throws Exception {
        mockMvc.perform(post("/profile/delete/user1")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertNull(profileRepository.findByUsername("user1"));
    }

    @Test
    void testDeleteOtherProfile_asModerator_success() throws Exception {
        Profile mod = testUtil.registerUser(mockMvc, "mod1", "mod1@example.com", "password");
        testUtil.makeModerator(mod);
        MockHttpSession modSession = testUtil.loginUser(mockMvc, "mod1@example.com", "password");

        testUtil.registerUser(mockMvc, "userToDelete", "del@example.com", "password");

        mockMvc.perform(post("/profile/delete/userToDelete")
                        .session(modSession)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertNull(profileRepository.findByUsername("userToDelete"));
    }

    @Test
    void testDeleteOtherProfile_unauthorized_redirects() throws Exception {
        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");

        mockMvc.perform(post("/profile/delete/user2")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertNotNull(profileRepository.findByUsername("user2"));
    }

    // --- SETTINGS TESTS ---

    @Test
    void testGetSettings_success() throws Exception {
        mockMvc.perform(get("/settings").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"));
    }

    @Test
    void testGetSettings_withoutSession_redirects() throws Exception {
        mockMvc.perform(get("/settings"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testChangeLanguage() throws Exception {
        mockMvc.perform(post("/change-language")
                        .session(session)
                        .with(csrf())
                        .param("language", "GERMAN"))
                .andExpect(status().is3xxRedirection());

        Profile p = profileRepository.findByUsername("user1");
        assertEquals(Language.GERMAN, p.getLanguage());
    }

    @Test
    void testChangeTheme() throws Exception {
        mockMvc.perform(post("/change-theme")
                        .session(session)
                        .with(csrf())
                        .param("theme", "DARK"))
                .andExpect(status().is3xxRedirection());

        Profile p = profileRepository.findByUsername("user1");
        assertEquals(Theme.DARK, p.getTheme());
    }

    // --- API TOKEN TESTS ---

    @Test
    void testCreateApiToken_success() throws Exception {
        mockMvc.perform(post("/tokens")
                        .session(session)
                        .with(csrf())
                        .param("name", "TestToken")
                        .param("duration", "30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("createdToken"));

        List<ApiToken> tokens = apiTokenRepository.findAll();
        assertFalse(tokens.isEmpty());
        assertEquals("TestToken", tokens.get(0).getName());
    }

    @Test
    void testDeleteApiToken_success() throws Exception {
        ApiToken token = new ApiToken();
        token.setName("ToDelete");
        token.setProfile(mainUser);
        token.setTokenIdentifier(UUID.randomUUID().toString());
        token.setExpiresAt(java.time.LocalDateTime.now().plusMonths(1));
        apiTokenRepository.save(token);

        mockMvc.perform(post("/tokens/delete")
                        .session(session)
                        .with(csrf())
                        .param("id", token.getId().toString()))
                .andExpect(status().is3xxRedirection());

        assertTrue(apiTokenRepository.findById(token.getId()).isEmpty());
    }

    // --- USERNAME UPDATE / SETUP TESTS ---

    @Test
    void testGetSetup_success() throws Exception {
        mockMvc.perform(get("/setup").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("setup"));
    }

    @Test
    void testUpdateUsername_success() throws Exception {
        mockMvc.perform(post("/profile/username/update")
                        .session(session)
                        .with(csrf())
                        .param("username", "NewUsername123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        Profile p = profileRepository.findById(mainUser.getId()).orElseThrow();
        assertEquals("NewUsername123", p.getUsername());
    }

    @Test
    void testUpdateUsername_validationFail() throws Exception {
        // "invalid user" enthält ein Leerzeichen, was das Regex ^[A-Za-z0-9]+$ verletzt.
        // Dadurch sollte der Validator einen Fehler werfen und der Controller die "setup" View zurückgeben (200 OK).
        mockMvc.perform(post("/profile/username/update")
                        .session(session)
                        .with(csrf())
                        .param("username", "invalid user"))
                .andExpect(status().isOk())
                .andExpect(view().name("setup"))
                .andExpect(model().attributeHasFieldErrors("usernameDto", "username"));
    }

    // --- EMAIL VERIFICATION TESTS ---

    @Test
    void testGetVerifyEmailPage_success() throws Exception {
        mockMvc.perform(get("/verify-email").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("verify-email"));
    }

    @Test
    void testVerifyEmail_validToken_success() throws Exception {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, mainUser);
        verificationTokenRepository.save(verificationToken);

        mockMvc.perform(post("/profile/email/verify")
                        .session(session)
                        .with(csrf())
                        .param("token", token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        Profile p = profileRepository.findById(mainUser.getId()).orElseThrow();
        assertTrue(p.getEmail().isVerified());
        assertNull(verificationTokenRepository.findByToken(token));
    }

    @Test
    void testVerifyEmail_invalidToken_fails() throws Exception {
        mockMvc.perform(post("/profile/email/verify")
                        .session(session)
                        .with(csrf())
                        .param("token", "invalid-token-123"))
                .andExpect(status().isOk())
                .andExpect(view().name("verify-email"))
                .andExpect(model().attribute("message", "Ungültiger Token."));
    }

    @Test
    void testVerifyEmail_expiredToken_fails() throws Exception {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, mainUser);
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -24);
        verificationToken.setExpiryDate(cal.getTime());
        verificationTokenRepository.save(verificationToken);

        mockMvc.perform(post("/profile/email/verify")
                        .session(session)
                        .with(csrf())
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name("verify-email"))
                .andExpect(model().attribute("message", "Token ist abgelaufen."));
    }
}