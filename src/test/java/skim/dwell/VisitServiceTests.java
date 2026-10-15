package skim.dwell;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import skim.dwell.model.*;
import skim.dwell.repository.SupervisorRepository;
import skim.dwell.service.NotificationService;
import skim.dwell.service.VisitService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VisitServiceTests {

    @Autowired VisitService visitService;
    @Autowired SupervisorRepository supervisors;
    @MockitoBean NotificationService notifications;

    @Test
    void checkInThenCheckOut() {
        Long supId = supervisors.findAll().get(0).getId();

        Visit visit = visitService.checkIn("Test Student", " Test@Example.com", supId);
        assertTrue(visit.isActive());

        assertThrows(IllegalStateException.class,
                () -> visitService.checkIn("Test Student", "test@example.com", supId));

        Visit done = visitService.checkOut("test@example.com", CheckoutMethod.QR);
        assertFalse(done.isActive());
        assertEquals(CheckoutMethod.QR, done.getCheckoutMethod());
    }
}