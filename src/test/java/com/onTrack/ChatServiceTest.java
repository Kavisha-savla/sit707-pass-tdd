package com.onTrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatServiceTest {

    private ChatService service;

    @BeforeEach
    void setUp() {
        service = new ChatService();
    }

    @Test
    @DisplayName("R: post then list contains the posted message")
    void R_postThenListContainsMessage() {
        SubmissionId id = SubmissionId.of(1L);

        ChatMessage posted = service.post(id, "kavisha", "What does criterion 3 mean?");
        List<ChatMessage> messages = service.list(id);

        assertEquals(1, messages.size(), "list must contain exactly one message");
        assertEquals(posted, messages.get(0), "list entry must equal the posted message");
    }

    @Test
    @DisplayName("B: list of a submission with no chat returns an empty list")
    void B_listOfSubmissionWithNoChatIsEmpty() {
        List<ChatMessage> messages = service.list(SubmissionId.of(123L));

        assertNotNull(messages, "list must never return null");
        assertTrue(messages.isEmpty(), "submission with no chat must have empty list");
    }

    @Test
    @DisplayName("I: posted message is present in list (inverse round-trip)")
    void I_postThenListIsInverse() {
        SubmissionId id = SubmissionId.of(2L);

        ChatMessage posted = service.post(id, "tutor", "Looks good. Resubmit with section 4 expanded.");

        assertTrue(service.list(id).contains(posted), "list must contain the just-posted message");
    }

    @Test
    @DisplayName("C: list size matches an explicit post counter (cross-check)")
    void C_listSizeMatchesExplicitPostCounter() {
        SubmissionId id = SubmissionId.of(3L);
        int posted = 0;
        for (int i = 0; i < 17; i++) {
            service.post(id, "kavisha", "msg " + i);
            posted++;
        }

        assertEquals(posted, service.list(id).size(),
                "list size must equal the number of posts made");
    }

    @Test
    @DisplayName("E: post with a null sender throws NullPointerException")
    void E_postWithNullSenderThrows() {
        assertThrows(NullPointerException.class,
                () -> service.post(SubmissionId.of(4L), null, "body"));
    }

    @Test
    @DisplayName("P: post 1000 messages then list completes under 200ms")
    void P_post1kMessagesAndListUnder200ms() {
        SubmissionId id = SubmissionId.of(5L);
        for (int i = 0; i < 1000; i++) {
            service.post(id, "kavisha", "m" + i);
        }

        assertTimeoutPreemptively(Duration.ofMillis(200), () -> {
            List<ChatMessage> messages = service.list(id);
            assertEquals(1000, messages.size(), "all 1000 messages must be returned");
        });
    }
}
