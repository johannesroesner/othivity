package de.oth.othivity.controller;

import de.oth.othivity.TestUtil;
import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ClubRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ClubIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ClubRepository clubRepository;

    private MockHttpSession session;
    private Profile profile;

    @BeforeEach
    void setUp() throws Exception {
        clubRepository.deleteAll();
        profile = testUtil.registerTestUser(mockMvc);
        session = testUtil.loginUser(mockMvc, "test@example.com", "password");
    }

    private void createTestClub() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/clubs/create")
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("name", "test club")
                        .param("description", "test description")
                        .param("accessLevel", "OPEN")
                        .param("address.street", "test street")
                        .param("address.houseNumber", "123")
                        .param("address.city", "test city")
                        .param("address.postalCode", "12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs"));
    }

    @Test
    void testGetClubsPage_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs")
                        .session(session))
                .andExpect(status().is(200))
                .andExpect(view().name("club-overview"))
                .andExpect(model().attributeExists("joinedClubs"))
                .andExpect(model().attributeExists("allClubs"))
                .andExpect(model().attributeExists("managedClubs"))
                .andExpect(model().attributeExists("activeTab"))
                .andExpect(model().attributeExists("size"))
                .andExpect(model().attributeExists("sortBy"))
                .andExpect(model().attributeExists("direction"))
                .andExpect(model().attributeExists("allAccessLevels"));
    }

    @Test
    void testGetClubsPage_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs"))
                .andExpect(status().is(302));
    }

    @Test
    void testGetCreateClubForm_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/create")
                        .session(session))
                .andExpect(status().is(200))
                .andExpect(view().name("club-edit"))
                .andExpect(model().attributeExists("clubDto"))
                .andExpect(model().attributeExists("accessLevels"))
                .andExpect(model().attributeExists("returnUrl"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    @Test
    void testCreateClubViaForm_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();
        assertEquals("test club", club.getName());
        assertEquals("test description", club.getDescription());
        assertEquals(AccessLevel.OPEN, club.getAccessLevel());
    }

    @Test
    void testCreateClubViaForm_badInput() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/clubs/create")
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("name", "bad club"))
                .andExpect(status().is(200))
                .andExpect(view().name("club-edit"));
    }

    @Test
    void testCreateClubViaForm_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/clubs/create"))
                .andExpect(status().is(403));
    }

    @Test
    void testGetClubDetail_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("club-detail"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("joinAble"))
                .andExpect(model().attributeExists("editMode"))
                .andExpect(model().attributeExists("leaveAble"))
                .andExpect(model().attributeExists("joinAbleOnInvite"))
                .andExpect(model().attributeExists("inviteOnly"))
                .andExpect(model().attributeExists("clubMembers"))
                .andExpect(model().attributeExists("clubAdmins"))
                .andExpect(model().attributeExists("memberCount"))
                .andExpect(model().attributeExists("clubActivities"))
                .andExpect(model().attributeExists("activitiesCount"))
                .andExpect(model().attributeExists("isReportable"));
    }

    @Test
    void testGetClubDetail_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + "123"))
                .andExpect(status().is(302));
    }

    @Test
    @Transactional
    void testJoinClub_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/join/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs/" + club.getId()));

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(2, club.getMembers().size());
    }

    @Test
    void testJoinClub_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/join/" + "123"))
                .andExpect(status().is(403));
    }

    @Test
    @Transactional
    void testLeaveClub_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/join/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(2, club.getMembers().size());

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/leave/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs/" + club.getId()));

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(1, club.getMembers().size());
    }

    @Test
    void testLeaveClub_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/leave/" + "123"))
                .andExpect(status().is(403));
    }

    @Test
    void testGetEditClubForm_asAdmin_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/edit/" + club.getId())
                        .session(session))
                .andExpect(status().is(200))
                .andExpect(view().name("club-edit"))
                .andExpect(model().attributeExists("clubDto"))
                .andExpect(model().attributeExists("accessLevels"))
                .andExpect(model().attributeExists("returnUrl"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    @Test
    void testGetEditClubForm_unauthorized_redirects() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/edit/" + club.getId())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs/" + club.getId()));
    }

    @Test
    void testEditClub_asAdmin_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/clubs/edit/" + club.getId())
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("name", "updated club")
                        .param("description", "updated description")
                        .param("accessLevel", "ON_INVITE")
                        .param("address.street", "updated street")
                        .param("address.houseNumber", "456")
                        .param("address.city", "updated city")
                        .param("address.postalCode", "54321"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs/" + club.getId()));

        Club updated = clubRepository.findAll().getFirst();
        assertEquals("updated club", updated.getName());
        assertEquals("updated description", updated.getDescription());
        assertEquals(AccessLevel.ON_INVITE, updated.getAccessLevel());
    }

    @Test
    void testEditClub_badInput_returnsForm() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/clubs/edit/" + club.getId())
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("name", "updated club"))
                .andExpect(status().is(200))
                .andExpect(view().name("club-edit"));

        Club updated = clubRepository.findAll().getFirst();
        assertEquals("test club", updated.getName());
    }

    @Test
    void testEditClub_unauthorized_redirects() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/clubs/edit/" + club.getId())
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("name", "updated club")
                        .param("description", "updated description")
                        .param("accessLevel", "ON_INVITE")
                        .param("address.street", "updated street")
                        .param("address.houseNumber", "456")
                        .param("address.city", "updated city")
                        .param("address.postalCode", "54321"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs/" + club.getId()));

        Club updated = clubRepository.findAll().getFirst();
        assertEquals("test club", updated.getName());
    }

    @Test
    void testEditClub_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/clubs/edit/" + "123"))
                .andExpect(status().is(403));
    }

    @Test
    void testDeleteClub_asAdmin_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/delete/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs"));

        Club deletedClub = clubRepository.findAll().stream().findFirst().orElse(null);
        assertNull(deletedClub);
    }

    @Test
    void testDeleteClub_unauthorized_fail() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/delete/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Club notDeleted = clubRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(notDeleted);
    }

    @Test
    void testDeleteClub_asModerator_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        Profile profile2 = testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        testUtil.makeModerator(profile2);
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/delete/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs"));

        Club deletedClub = clubRepository.findAll().stream().findFirst().orElse(null);
        assertNull(deletedClub);
    }

    @Test
    void testDeleteClub_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/delete/" + "123"))
                .andExpect(status().is(403));
    }

    @Test
    @Transactional
    void testRemoveMember_asAdmin_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        Profile joiner = testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/join/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(2, club.getMembers().size());

        session = testUtil.loginUser(mockMvc, "test@example.com", "password");
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/removeMember/" + club.getId() + "/" + joiner.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs/" + club.getId()));

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(1, club.getMembers().size());
    }

    @Test
    @Transactional
    void testRemoveMember_unauthorized_fail() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/join/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(2, club.getMembers().size());

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/removeMember/" + club.getId() + "/" + profile.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(2, club.getMembers().size());
    }

    @Test
    @Transactional
    void testRemoveMember_asModerator_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        Profile joiner = testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        testUtil.makeModerator(joiner);
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/join/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(2, club.getMembers().size());

        session = testUtil.loginUser(mockMvc, "test@example.com", "password");
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/removeMember/" + club.getId() + "/" + profile.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(1, club.getMembers().size());
    }

    @Test
    void testRemoveMember_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/removeMember/" + "123" + "/" + "123"))
                .andExpect(status().is(403));
    }

    @Test
    void testGetSelectAdmin_asLastAdmin_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        Profile joiner = testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/join/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        session = testUtil.loginUser(mockMvc, "test@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + club.getId() + "/select-admin")
                        .session(session)
                        .param("leaving", "true"))
                .andExpect(status().is(200))
                .andExpect(view().name("club-select-admin"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("clubMembers"))
                .andExpect(model().attributeExists("isLeaving"));
    }

    @Test
    @Transactional
    void testMakeAdmin_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        Profile joiner = testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/join/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        session = testUtil.loginUser(mockMvc, "test@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/makeAdmin/" + club.getId() + "/" + joiner.getId())
                        .session(session)
                        .with(csrf())
                        .param("leaving", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs/" + club.getId()));

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getAdmins());
        assertEquals(2, club.getAdmins().size());
    }

    @Test
    @Transactional
    void testMakeAdmin_withLeaving_success() throws Exception {
        createTestClub();
        Club club = clubRepository.findAll().getFirst();

        Profile joiner = testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/join/" + club.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        assertEquals(2, club.getMembers().size());

        session = testUtil.loginUser(mockMvc, "test@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/makeAdmin/" + club.getId() + "/" + joiner.getId())
                        .session(session)
                        .with(csrf())
                        .param("leaving", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clubs"));

        club = clubRepository.findAll().getFirst();
        Hibernate.initialize(club.getMembers());
        Hibernate.initialize(club.getAdmins());
        assertEquals(1, club.getMembers().size());
        assertEquals(1, club.getAdmins().size());
        assertTrue(club.getAdmins().contains(joiner));
    }

    @Test
    void testMakeAdmin_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/clubs/makeAdmin/" + "123" + "/" + "123"))
                .andExpect(status().is(403));
    }
}
