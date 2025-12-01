package de.oth.othivity;

import de.oth.othivity.service.IPushNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OthivityApplicationTests {
    @MockBean
    private IPushNotificationService pushNotificationService;

    @Test
    void contextLoads() {
    }

}