package de.oth.othivity.dto;

public record PushSubscriptionDto(String endpoint, Keys keys) {
        public record Keys(String p256dh, String auth) {}
    }