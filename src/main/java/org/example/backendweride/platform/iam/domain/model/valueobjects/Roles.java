package org.example.backendweride.platform.iam.domain.model.valueobjects;

public enum Roles {
    ROLE_USER("user"),
    ROLE_ADMIN("admin");

    private final String displayName;

    Roles(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
