INSERT INTO vehicle_types (id, code) VALUES
                                         (1, 'SCOOTER'),
                                         (2, 'BIKE'),
                                         (3, 'E-BIKE');

INSERT INTO plans (id, name, price, price_per_minute, duration_days, is_active) VALUES
                                                                                    ('basic-plan-001', 'Plan Normal', 3.99, 0.60, 30, TRUE),
                                                                                    ('premium-plan-002', 'Plan Premium', 49.90, 0.20, 30, TRUE);

INSERT INTO locations (id, name, address, lat, lng, capacity, is_active) VALUES
                                                                             (1, 'Plaza Mayor', 'Centro Histórico', -12.046374, -77.042793, 20, TRUE),
                                                                             (2, 'Miraflores', 'Av. Larco', -12.121467, -77.030367, 15, TRUE),
                                                                             (3, 'Av. Abancay', 'Cercado de Lima', -12.052891, -77.041234, 10, TRUE),
                                                                             (4, 'Jockey Plaza', 'Surco', -12.084334, -76.977074, 25, TRUE);
