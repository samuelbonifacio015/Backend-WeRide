INSERT INTO users (
    name, email, password, phone,
    membership_plan_id, is_active,
    verification_status, registration_date
) VALUES (
             'Laura Pérez',
             'laura.perez@example.com',
             '{noop}12345',
             '+51987654321',
             'premium-plan-002',
             TRUE,
             'verified',
             '2024-01-15 10:00:00'
         );

INSERT INTO vehicles (
    brand, model, vehicle_type_id,
    location_id, battery, status, price_per_minute
) VALUES (
             'Xiaomi',
             'M365',
             1,
             1,
             82,
             'available',
             0.50
         );

INSERT INTO trips (
    id, user_id, vehicle_id,
    start_location_id, end_location_id,
    start_date, end_date,
    duration, distance, total_cost, status
) VALUES (
             'trip-001',
             1,
             1,
             1,
             4,
             '2025-10-07 10:02:00',
             '2025-10-07 11:28:00',
             86,
             12.5,
             24.75,
             'completed'
         );
