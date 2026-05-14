package com.onTrack;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService {

    private final Clock clock;
    private final Map<Long, List<ChatMessage>> store = new ConcurrentHashMap<>();

    public ChatService() {
        this(Clock.systemUTC());
    }

    public ChatService(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public ChatMessage post(SubmissionId id, String sender, String body) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(sender, "sender must not be null");
        Objects.requireNonNull(body, "body must not be null");

        ChatMessage message = new ChatMessage(id, sender, body, Instant.now(clock));
        store.computeIfAbsent(id.value(), k -> Collections.synchronizedList(new ArrayList<>())).add(message);
        return message;
    }

    public List<ChatMessage> list(SubmissionId id) {
        if (id == null) {
            return List.of();
        }
        List<ChatMessage> messages = store.get(id.value());
        if (messages == null) {
            return List.of();
        }
        synchronized (messages) {
            return List.copyOf(messages);
        }
    }
}
