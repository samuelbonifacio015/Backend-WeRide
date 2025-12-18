package org.example.backendweride.platform.user.interfaces.rest.transform;

import org.example.backendweride.platform.user.domain.model.commands.CreateUserCommand;
import org.example.backendweride.platform.user.interfaces.rest.resources.CreateUserResource;

public class CreateUserCommandFromResourceAssembler {
    public static CreateUserCommand toCommandFromResource(CreateUserResource resource) {
        return new CreateUserCommand(
                resource.name(),
                resource.email(),
                resource.password(),
                resource.phone(),
                resource.membershipPlanId(),
                resource.profilePicture(),
                resource.dateOfBirth(),
                resource.address(),
                resource.emergencyContact()
        );
    }
}
