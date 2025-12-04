package de.oth.othivity.controller;

import de.oth.othivity.TestUtil;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ActivityRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ActivityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ActivityRepository activityRepository;

    private MockHttpSession session;
    private Profile profile;

    @BeforeEach
    void setUp() throws Exception {
        activityRepository.deleteAll();
        profile = testUtil.registerTestUser(mockMvc);
        session = testUtil.loginUser(mockMvc, "test@example.com", "password");
    }

    private void createTestActivity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/activities/create")
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("title", "test activity")
                        .param("description", "test description")
                        .param("date", "3000-12-03T12:00:00")
                        .param("language", "ENGLISH")
                        .param("groupSize", "5")
                        .param("address.street", "test street")
                        .param("address.houseNumber", "123")
                        .param("address.city", "test city")
                        .param("address.postalCode", "123")
                        .param("tag", "OUTDOOR")

                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/activities"));
    }

    @Test
    void testGetActivitiesPage_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/activities")
                        .session(session))
                .andExpect(status().is(200))
                .andExpect(view().name("activity-overview"))
                .andExpect(model().attributeExists("profileActivities"))
                .andExpect(model().attributeExists("createdActivities"))
                .andExpect(model().attributeExists("allActivities"))
                .andExpect(model().attributeExists("daysToMark"))
                .andExpect(model().attributeExists("activeTab"))
                .andExpect(model().attributeExists("size"))
                .andExpect(model().attributeExists("sortBy"))
                .andExpect(model().attributeExists("direction"))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeExists("calendarTheme"));
    }

    @Test
    void testGetActivitiesPage_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/activities"))
                .andExpect(status().is(302));
    }


    @Test
    void testCreateActivityViaForm_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();
        assertEquals("test activity", activity.getTitle());
    }

    @Test
    void testCreateActivityViaForm_badInput() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/activities/create")
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("title", "bad activity")
                )
                .andExpect(status().is(400));
    }

    @Test
    void testCreateActivityViaForm_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/activities/create"))
                .andExpect(status().is(403));
    }


    @Test
    void testGetActivityDetail_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.get("/activities/" + activity.getId())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("activity-detail"))
                .andExpect(model().attributeExists("activity"))
                .andExpect(model().attributeExists("joinAble"))
                .andExpect(model().attributeExists("leaveAble"))
                .andExpect(model().attributeExists("updateAble"))
                .andExpect(model().attributeExists("deleteAble"))
                .andExpect(model().attributeExists("isReportable"));
    }

    @Test
    void testGetActivityDetail_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/activities/" + "123"))
                .andExpect(status().is(302));
    }

    @Test
    @Transactional
    void testJoinActivity_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/join/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(2, activity.getTakePart().size());

    }

    @Transactional
    @Test
    void testJoinActivity_joinAbleFalse_fail() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/join/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Hibernate.initialize(activity.getTakePart());
        assertEquals(1, activity.getTakePart().size());
    }

    @Test
    void testJoinActivity_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/activities/join/" + "123"))
                .andExpect(status().is(403));
    }

    @Test
    @Transactional
    void testLeaveActivity_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/join/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(2, activity.getTakePart().size());

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/leave/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(1, activity.getTakePart().size());
    }

    @Test
    @Transactional
    void testLeaveActivity_leaveAbleFalse_fail() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/leave/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(1, activity.getTakePart().size());
    }

    @Test
    void testLeaveActivity_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/activities/leave/" + "123"))
                .andExpect(status().is(403));
    }

    @Test
    void testDeleteActivity_asOwner_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/delete/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().stream().findFirst().orElse(null);
        assertNull(activity);
    }

    @Test
    void testDeleteActivity_unauthorized_fail() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/delete/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(activity);
    }

    @Test
    void testDeleteActivity_asModerator_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        Profile profile2 = testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        testUtil.makeModerator(profile2);
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/delete/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().stream().findFirst().orElse(null);
        assertNull(activity);
    }


    @Test
    void testDeleteActivity_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/activities/delete/" + "123"))
                .andExpect(status().is(403));
    }

    @Test
    @Transactional
    void kickParticipant_asOwner_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        Profile joiner = testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/join/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(2, activity.getTakePart().size());

        session = testUtil.loginUser(mockMvc, "test@example.com", "password");
        mockMvc.perform(MockMvcRequestBuilders.post("/activities/kick/" + activity.getId() + "/" + joiner.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(1, activity.getTakePart().size());
    }

    @Test
    @Transactional
    void kickParticipant_unauthorized_fail() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/join/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(2, activity.getTakePart().size());

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/kick/" + activity.getId() + "/" + profile.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(2, activity.getTakePart().size());
    }

    @Test
    @Transactional
    void kickParticipant_asModerator_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        Profile joiner = testUtil.registerUser(mockMvc, "joiner", "joiner@example.com", "password");
        testUtil.makeModerator(joiner);
        session = testUtil.loginUser(mockMvc, "joiner@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/activities/join/" + activity.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(2, activity.getTakePart().size());

        session = testUtil.loginUser(mockMvc, "test@example.com", "password");
        mockMvc.perform(MockMvcRequestBuilders.post("/activities/kick/" + activity.getId() + "/" + profile.getId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        activity = activityRepository.findAll().getFirst();
        Hibernate.initialize(activity.getTakePart());
        assertEquals(1, activity.getTakePart().size());
    }


    @Test
    void kickParticipant_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/activities/kick/" + "123" + "/" + "123"))
                .andExpect(status().is(403));
    }

    @Test
    void showUpdateForm_asOwner_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.get("/activities/update/" + activity.getId())
                        .session(session))
                .andExpect(status().is(200))
                .andExpect(view().name("activity-edit"))
                .andExpect(model().attributeExists("activityDto"))
                .andExpect(model().attributeExists("languages"))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeExists("tagAbleClubs"));
    }

    @Test
    void showUpdateForm_unauthorized_fail() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");


        mockMvc.perform(MockMvcRequestBuilders.get("/activities/update/" + activity.getId())
                        .session(session))
                .andExpect(status().is(302));

    }

    @Test
    void showUpdateForm_asModerator_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        Profile profile2 = testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        testUtil.makeModerator(profile2);
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");


        mockMvc.perform(MockMvcRequestBuilders.get("/activities/update/" + activity.getId())
                        .session(session))
                .andExpect(status().is(200))
                .andExpect(view().name("activity-edit"))
                .andExpect(model().attributeExists("activityDto"))
                .andExpect(model().attributeExists("languages"))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeExists("tagAbleClubs"));
    }

    @Test
    void showUpdateForm_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/activities/update/" + "123"))
                .andExpect(status().is(302));
    }

    @Test
    void updateActivity_asOwner_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/activities/update/" + activity.getId())
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("title", "updated title")
                        .param("description", "updated description")
                        .param("date", "3000-12-04T12:00:00")
                        .param("language", "GERMAN")
                        .param("groupSize", "10")
                        .param("address.street", "new street")
                        .param("address.houseNumber", "99")
                        .param("address.city", "new city")
                        .param("address.postalCode", "999")
                        .param("tag", "SPORT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/activities/" + activity.getId()));

        Activity updated = activityRepository.findAll().getFirst();
        assertEquals("updated title", updated.getTitle());
        assertEquals("updated description", updated.getDescription());
    }

    @Test
    void updateActivity_allowedButBadInput_fail() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/activities/update/" + activity.getId())
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("title", "updated title")
                        .param("description", "updated description")
                        .param("date", "3000-12-04T12:00:00")
                        .param("language", "GERMAN")
                        .param("groupSize", "0")
                        .param("address.street", "new street")
                        .param("address.houseNumber", "99")
                        .param("address.city", "new city")
                        .param("address.postalCode", "999")
                        .param("tag", "SPORT"))
                .andExpect(status().is(400));

        Activity updated = activityRepository.findAll().getFirst();
        assertEquals("test activity", updated.getTitle());
        assertEquals("test description", updated.getDescription());
    }

    @Test
    void updateActivity_unauthorized_fail() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/activities/update/" + activity.getId())
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("title", "updated title")
                        .param("description", "updated description")
                        .param("date", "3000-12-04T12:00:00")
                        .param("language", "GERMAN")
                        .param("groupSize", "10")
                        .param("address.street", "new street")
                        .param("address.houseNumber", "99")
                        .param("address.city", "new city")
                        .param("address.postalCode", "999")
                        .param("tag", "SPORT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/activities/" + activity.getId()));

        Activity updated = activityRepository.findAll().getFirst();
        assertEquals("test activity", updated.getTitle());
        assertEquals("test description", updated.getDescription());
    }

    @Test
    void updateActivity_asModerator_success() throws Exception {
        createTestActivity();
        Activity activity = activityRepository.findAll().getFirst();

        Profile profile2 = testUtil.registerUser(mockMvc, "user2", "user2@example.com", "password");
        testUtil.makeModerator(profile2);
        session = testUtil.loginUser(mockMvc, "user2@example.com", "password");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/activities/update/" + activity.getId())
                        .file(testUtil.setValidImageFile())
                        .session(session)
                        .with(csrf())
                        .param("title", "updated title")
                        .param("description", "updated description")
                        .param("date", "3000-12-04T12:00:00")
                        .param("language", "GERMAN")
                        .param("groupSize", "10")
                        .param("address.street", "new street")
                        .param("address.houseNumber", "99")
                        .param("address.city", "new city")
                        .param("address.postalCode", "999")
                        .param("tag", "SPORT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/activities/" + activity.getId()));

        Activity updated = activityRepository.findAll().getFirst();
        assertEquals("updated title", updated.getTitle());
        assertEquals("updated description", updated.getDescription());
    }

    @Test
    void updateActivity_withoutSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/activities/update/" + "123"))
                .andExpect(status().is(403));
    }

}
