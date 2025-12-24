package org.example.backendweride.platform.shared.bootstrap;

import lombok.RequiredArgsConstructor;
import org.example.backendweride.platform.garage.domain.model.aggregates.Vehicle;
import org.example.backendweride.platform.garage.domain.model.commands.CreateVehicleCommand;
import org.example.backendweride.platform.garage.infrastructure.persistence.jpa.VehicleRepository;
import org.example.backendweride.platform.iam.domain.model.entities.Role;
import org.example.backendweride.platform.iam.domain.model.valueobjects.Roles;
import org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.example.backendweride.platform.location.domain.model.aggregates.Location;
import org.example.backendweride.platform.location.domain.commands.CreateLocationCommand;
import org.example.backendweride.platform.location.domain.valueobjects.Coordinates;
import org.example.backendweride.platform.location.domain.valueobjects.OperatingHours;
import org.example.backendweride.platform.location.infrastructure.persistence.jpa.LocationRepository;
import org.example.backendweride.platform.plan.domain.commands.CreatePlanCommand;
import org.example.backendweride.platform.plan.domain.model.aggregates.Plan;
import org.example.backendweride.platform.plan.infrastructure.persistence.jpa.PlanRepository;
import org.example.backendweride.platform.trip.infrastructure.persistence.jpa.TripRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;
    private final LocationRepository locationRepository;
    private final VehicleRepository vehicleRepository;
    private final RoleRepository roleRepository;
    private final TripRepository tripRepository;

    @Override
    @Transactional
    public void run(String... args) {

        try {
            System.out.println("🧹 Cleaning database...");
            tripRepository.deleteAll();
            vehicleRepository.deleteAll();
            locationRepository.deleteAll();
            planRepository.deleteAll();
        } catch (Exception e) {
            System.out.println("⚠ Warning during cleanup: " + e.getMessage());
        }

        seedRoles();
        seedPlans();
        seedLocations();
        seedVehicles();

        System.out.println(" Data Initialized Successfully");
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            Arrays.stream(Roles.values()).forEach(role -> {
                if (!roleRepository.existsByName(role)) {
                    roleRepository.save(new Role(role));
                    System.out.println("Role " + role + " initialized.");
                }
            });
        }
    }

    private void seedPlans() {
        if (planRepository.count() == 0) {
            planRepository.saveAll(List.of(
                    new Plan(new CreatePlanCommand(
                            "Plan Normal",
                            "Ideal para usuarios ocasionales que buscan una opción económica",
                            3.99f, "USD", 0.6f, "monthly", 30, 5, 60, 30, 10,
                            List.of("Acceso a scooters estándar", "10% de descuento en cada viaje", "Soporte al cliente básico", "30 minutos gratis al mes"),
                            "#3B82F6", false, true
                    )),
                    new Plan(new CreatePlanCommand(
                            "Plan Estudiantil",
                            "Perfecto para estudiantes que necesitan movilidad frecuente",
                            5.99f, "USD", 0.4f, "monthly", 30, 10, 90, 60, 20,
                            List.of("Acceso a scooters premium", "20% de descuento en cada viaje", "Soporte al cliente prioritario", "Viajes ilimitados los fines de semana", "60 minutos gratis al mes"),
                            "#10B981", true, true
                    )),
                    new Plan(new CreatePlanCommand(
                            "Plan Empresarial",
                            "Solución completa para profesionales y empresas",
                            9.99f, "USD", 0.3f, "monthly", 30, 20, 120, 120, 30,
                            List.of("Acceso a todos los vehículos", "30% de descuento en cada viaje", "Soporte prioritario 24/7", "Viajes ilimitados", "120 minutos gratis al mes", "Reportes mensuales", "Facturación centralizada"),
                            "#8B5CF6", false, true
                    ))
            ));
        }
    }

    private void seedLocations() {
        locationRepository.saveAll(List.of(
                new Location(new CreateLocationCommand(
                        "Plaza Mayor", "Plaza Mayor, Lima Centro", new Coordinates(-12.046374, -77.042793), "station", 15, 8, true, new OperatingHours("05:00", "23:00"), List.of("WiFi", "Security", "Shelter"), "Lima", "Estación principal en el corazón histórico de Lima", "assets/locations/plaza-mayor.jpg")),
                new Location(new CreateLocationCommand(
                        "Parque Kennedy", "Av. Larco, Miraflores", new Coordinates(-12.120500, -77.029833), "station", 20, 12, true, new OperatingHours("06:00", "22:00"), List.of("WiFi", "Security", "Charging", "Shelter"), "Miraflores", "Estación en el corazón de Miraflores", "assets/locations/parque-kennedy.jpg")),
                new Location(new CreateLocationCommand(
                        "Larcomar", "Malecón de la Reserva, Miraflores", new Coordinates(-12.131944, -77.030278), "station", 18, 10, true, new OperatingHours("07:00", "23:00"), List.of("WiFi", "Security", "Charging"), "Miraflores", "Estación con vista al mar", "assets/locations/larcomar.jpg")),
                new Location(new CreateLocationCommand(
                        "Jockey Plaza", "Av. Javier Prado Este, Surco", new Coordinates(-12.084334, -76.977074), "station", 25, 15, true, new OperatingHours("06:00", "23:30"), List.of("WiFi", "Security", "Charging", "Shelter", "Lockers"), "Surco", "Estación en centro comercial principal", "assets/locations/jockey-plaza.jpg")),
                new Location(new CreateLocationCommand(
                        "San Isidro Golf", "Av. Camino Real, San Isidro", new Coordinates(-12.097778, -77.037222), "station", 16, 9, true, new OperatingHours("06:00", "22:00"), List.of("WiFi", "Security", "Shelter"), "San Isidro", "Estación en zona empresarial", "assets/locations/san-isidro-golf.jpg")),
                new Location(new CreateLocationCommand(
                        "Real Plaza Salaverry", "Av. Salaverry, Jesús María", new Coordinates(-12.081111, -77.047222), "station", 22, 14, true, new OperatingHours("06:30", "23:00"), List.of("WiFi", "Security", "Charging", "Shelter"), "Jesús María", "Estación en centro comercial", "assets/locations/real-plaza-salaverry.jpg")),
                new Location(new CreateLocationCommand(
                        "Barranco Central", "Av. Grau, Barranco", new Coordinates(-12.146667, -77.016944), "station", 14, 7, true, new OperatingHours("07:00", "00:00"), List.of("WiFi", "Security"), "Barranco", "Estación en distrito bohemio", "assets/locations/barranco-central.jpg")),
                new Location(new CreateLocationCommand(
                        "Óvalo Gutiérrez", "Av. Santa Cruz, Miraflores", new Coordinates(-12.119444, -77.027778), "station", 20, 11, true, new OperatingHours("06:00", "22:30"), List.of("WiFi", "Security", "Charging", "Shelter"), "Miraflores", "Estación en zona comercial", "assets/locations/ovalo-gutierrez.jpg")),
                new Location(new CreateLocationCommand(
                        "Pueblo Libre Centro", "Av. Brasil, Pueblo Libre", new Coordinates(-12.075278, -77.063889), "station", 12, 6, true, new OperatingHours("06:30", "22:00"), List.of("WiFi", "Security", "Shelter"), "Pueblo Libre", "Estación en zona residencial", "assets/locations/pueblo-libre.jpg")),
                new Location(new CreateLocationCommand(
                        "Universidad del Pacífico", "Av. Salaverry, Jesús María", new Coordinates(-12.082222, -77.045278), "station", 18, 13, true, new OperatingHours("05:30", "22:30"), List.of("WiFi", "Security", "Charging", "Shelter", "Student Discount"), "Jesús María", "Estación universitaria", "assets/locations/universidad-pacifico.jpg"))
        ));
    }

    private void seedVehicles() {
        vehicleRepository.saveAll(List.of(
                new Vehicle(new CreateVehicleCommand(
                        "Xiaomi", "M365", 2023, 82, 25, 30, 12.5, "black", "XM001",
                        "Plaza Mayor", "available", "electric_scooter", "1", 0.5, "assets/vehicles/xiaomi-m365.jpg",
                        List.of("GPS", "Bluetooth", "Mobile App", "LED Lights"), "good",
                        Date.from(Instant.parse("2024-09-15T10:00:00Z")), Date.from(Instant.parse("2024-11-15T10:00:00Z")), 1250.5, 4.6)),

                new Vehicle(new CreateVehicleCommand(
                        "Trek", "FX 3", 2022, 0, 0, 0, 11.8, "blue", "TK002",
                        "Parque Kennedy", "reserved", "bike", "1", 0.3, "assets/vehicles/trek-fx3.jpg",
                        List.of("Lightweight", "21 Gears", "Quick Release"), "excellent",
                        Date.from(Instant.parse("2024-09-20T14:00:00Z")), Date.from(Instant.parse("2024-12-20T14:00:00Z")), 456.2, 4.8)),

                new Vehicle(new CreateVehicleCommand(
                        "Segway", "Ninebot Max", 2023, 65, 30, 40, 18.7, "gray", "SG003",
                        "Larcomar", "maintenance", "electric_scooter", "1", 0.6, "assets/vehicles/segway-max.jpg",
                        List.of("GPS", "Bluetooth", "Anti-theft", "Cruise Control"), "under_repair",
                        Date.from(Instant.parse("2025-10-05T08:00:00Z")), Date.from(Instant.parse("2025-12-05T08:00:00Z")), 892.3, 4.7)),

                new Vehicle(new CreateVehicleCommand(
                        "Giant", "Escape 3", 2023, 0, 0, 0, 12.9, "red", "GT004",
                        "Jockey Plaza", "available", "bike", "2", 0.35, "assets/vehicles/giant-escape3.jpg",
                        List.of("Aluminum Frame", "24 Gears", "Disc Brakes"), "good",
                        Date.from(Instant.parse("2025-09-10T10:00:00Z")), Date.from(Instant.parse("2025-12-10T10:00:00Z")), 678.5, 4.5)),

                new Vehicle(new CreateVehicleCommand(
                        "Ninebot", "ES2", 2022, 90, 25, 25, 12.5, "black", "NB005",
                        "San Isidro Golf", "available", "electric_scooter", "2", 0.45, "assets/vehicles/ninebot-es2.jpg",
                        List.of("LED Display", "App Control", "Shock Absorption"), "excellent",
                        Date.from(Instant.parse("2025-09-25T14:00:00Z")), Date.from(Instant.parse("2025-11-25T14:00:00Z")), 523.8, 4.9)),
                new Vehicle(new CreateVehicleCommand(
                        "Specialized", "Sirrus X", 2023, 0, 0, 0, 11.5, "green", "SP006",
                        "Real Plaza Salaverry", "reserved", "bike", "2", 0.4, "assets/vehicles/specialized-sirrus.jpg",
                        List.of("Carbon Fork", "Hydraulic Brakes", "Tubeless Ready"), "good",
                        Date.from(Instant.parse("2025-09-18T11:00:00Z")), Date.from(Instant.parse("2025-12-18T11:00:00Z")), 345.2, 4.8)),

                new Vehicle(new CreateVehicleCommand(
                        "Xiaomi", "Pro 2", 2023, 75, 25, 45, 14.2, "black", "XM007",
                        "Barranco Central", "available", "electric_scooter", "1", 0.55, "assets/vehicles/xiaomi-pro2.jpg",
                        List.of("GPS", "E-ABS", "Dual Brakes", "IPX4 Waterproof"), "good",
                        Date.from(Instant.parse("2025-09-22T09:00:00Z")), Date.from(Instant.parse("2025-11-22T09:00:00Z")), 1045.7, 4.7)),

                new Vehicle(new CreateVehicleCommand(
                        "Segway", "E22", 2023, 88, 20, 22, 13.5, "white", "SG008",
                        "Óvalo Gutiérrez", "available", "electric_scooter", "1", 0.5, "assets/vehicles/segway-e22.jpg",
                        List.of("Folding Design", "LED Lights", "App Control"), "excellent",
                        Date.from(Instant.parse("2025-09-28T10:00:00Z")), Date.from(Instant.parse("2025-11-28T10:00:00Z")), 234.9, 4.6)),

                new Vehicle(new CreateVehicleCommand(
                        "Razor", "E300", 2022, 70, 24, 20, 21.0, "blue", "RZ009",
                        "Pueblo Libre Centro", "available", "electric_scooter", "3", 0.4, "assets/vehicles/razor-e300.jpg",
                        List.of("Wide Deck", "Pneumatic Tires", "Rear Suspension"), "good",
                        Date.from(Instant.parse("2025-09-15T13:00:00Z")), Date.from(Instant.parse("2025-11-15T13:00:00Z")), 567.3, 4.4)),

                new Vehicle(new CreateVehicleCommand(
                        "Unagi", "Model One", 2023, 95, 32, 25, 10.9, "orange", "UN010",
                        "Universidad del Pacífico", "available", "electric_scooter", "3", 0.65, "assets/vehicles/unagi-one.jpg",
                        List.of("Dual Motor", "Lightweight", "Premium Design", "Smart Display"), "excellent",
                        Date.from(Instant.parse("2025-10-01T08:00:00Z")), Date.from(Instant.parse("2025-12-01T08:00:00Z")), 123.4, 4.9)),

                new Vehicle(new CreateVehicleCommand(
                        "Xiaomi", "Essential", 2024, 100, 20, 20, 12.0, "black", "XM011",
                        "Plaza Mayor", "available", "electric_scooter", "1", 0.45, "assets/vehicles/xiaomi-m365.jpg",
                        List.of("Lite", "Portable"), "excellent", Date.from(Instant.now()), Date.from(Instant.now()), 0.0, 5.0)),

                new Vehicle(new CreateVehicleCommand(
                        "Segway", "F40", 2024, 100, 30, 40, 15.0, "grey", "SG012",
                        "Parque Kennedy", "available", "electric_scooter", "1", 0.55, "assets/vehicles/segway-max.jpg",
                        List.of("Robust", "Long Range"), "excellent", Date.from(Instant.now()), Date.from(Instant.now()), 0.0, 5.0))
        ));
    }
}