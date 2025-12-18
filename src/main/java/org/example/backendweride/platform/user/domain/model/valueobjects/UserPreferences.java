package org.example.backendweride.platform.user.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class UserPreferences {
    private String language;
    private Boolean notifications;
    private String theme;

    protected UserPreferences() {}

    public UserPreferences(String language, Boolean notifications, String theme) {
        this.language = language;
        this.notifications = notifications;
        this.theme = theme;
    }
}