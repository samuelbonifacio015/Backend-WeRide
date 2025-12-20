package org.example.backendweride.platform.shared.bootstrap;

import lombok.RequiredArgsConstructor;
import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.garage.domain.model.commands.CreateVehicleCommand;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.example.backendweride.platform.location.domain.model.aggregates.Location;
import org.example.backendweride.platform.location.domain.commands.CreateLocationCommand;
import org.example.backendweride.platform.location.infrastructure.persistence.jpa.LocationRepository;
import org.example.backendweride.platform.plan.domain.commands.CreatePlanCommand;
import org.example.backendweride.platform.plan.domain.model.aggregates.Plan;
import org.example.backendweride.platform.plan.infrastructure.persistence.jpa.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;
    private final LocationRepository locationRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) {
        seedPlans();
        seedLocations();
        seedVehicles();
    }

    private void seedPlans() {
        if (planRepository.count() > 0) return;

        planRepository.saveAll(List.of(
                new Plan(new CreatePlanCommand("Basic Plan", "Plan básico", 9.99f, "USD", 0.5f, "30 days", 30, 10, 60, 100, 0, new ArrayList<>(), "#FF5733", false, true)),
                new Plan(new CreatePlanCommand("Premium Plan", "Plan premium", 19.99f, "USD", 0.3f, "30 days", 30, 20, 120, 200, 10, new ArrayList<>(), "#33FF57", true, true)),
                new Plan(new CreatePlanCommand("Business Plan", "Plan empresarial", 49.99f, "USD", 0.1f, "30 days", 30, 100, 480, 1000, 25, new ArrayList<>(), "#3357FF", false, true))
        ));
    }

    private void seedLocations() {
        if (locationRepository.count() > 0) return;

        locationRepository.save(
                new Location(new CreateLocationCommand("Plaza Mayor", "Calle Principal 123", null, "public", 50, 50, true, null, new ArrayList<>(), "Centro", "Plaza central de la ciudad", null))
        );
    }

    private void seedVehicles() {
        if (vehicleRepository.count() > 0) return;

        vehicleRepository.save(
                new Vehicle(new CreateVehicleCommand("Xiaomi", "Mi Electric Scooter", 2024, 100, 55, 100, 12.5, "Black", "XM001", "Plaza Mayor", "available", "scooter", "COMPANY001", 0.25, null, new ArrayList<>(), "good", new Date(), new Date(), 0, 4.5))
        );
    }
}
