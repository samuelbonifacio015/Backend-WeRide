package org.example.backendweride.platform.user.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class VerificationStatus {
    private String status;

    protected VerificationStatus() {}

    public VerificationStatus(String status) {
        this.status = status;
    }
}