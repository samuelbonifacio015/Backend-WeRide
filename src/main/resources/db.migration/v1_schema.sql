CREATE TABLE vehicle_types (
                               id BIGINT PRIMARY KEY,
                               code VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE plans (
                       id VARCHAR(50) PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       price DECIMAL(10,2) NOT NULL,
                       price_per_minute DECIMAL(10,2) NOT NULL,
                       duration_days INT NOT NULL,
                       is_active BOOLEAN NOT NULL
);

CREATE TABLE locations (
                           id BIGINT PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           address VARCHAR(255),
                           lat DOUBLE NOT NULL,
                           lng DOUBLE NOT NULL,
                           capacity INT NOT NULL,
                           is_active BOOLEAN NOT NULL
);

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       membership_plan_id VARCHAR(50),
                       is_active BOOLEAN NOT NULL,
                       verification_status VARCHAR(50),
                       registration_date TIMESTAMP,
                       CONSTRAINT fk_users_plan
                           FOREIGN KEY (membership_plan_id)
                               REFERENCES plans(id)
);

CREATE TABLE vehicles (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          brand VARCHAR(100),
                          model VARCHAR(100),
                          vehicle_type_id BIGINT,
                          location_id BIGINT,
                          battery INT,
                          status VARCHAR(50),
                          price_per_minute DECIMAL(10,2),
                          CONSTRAINT fk_vehicle_type
                              FOREIGN KEY (vehicle_type_id)
                                  REFERENCES vehicle_types(id),
                          CONSTRAINT fk_vehicle_location
                              FOREIGN KEY (location_id)
                                  REFERENCES locations(id)
);

CREATE TABLE trips (
                       id VARCHAR(50) PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       vehicle_id BIGINT NOT NULL,
                       start_location_id BIGINT,
                       end_location_id BIGINT,
                       start_date TIMESTAMP,
                       end_date TIMESTAMP,
                       duration INT,
                       distance DECIMAL(6,2),
                       total_cost DECIMAL(10,2),
                       status VARCHAR(50),

                       CONSTRAINT fk_trip_user FOREIGN KEY (user_id) REFERENCES users(id),
                       CONSTRAINT fk_trip_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
                       CONSTRAINT fk_trip_start_location FOREIGN KEY (start_location_id) REFERENCES locations(id),
                       CONSTRAINT fk_trip_end_location FOREIGN KEY (end_location_id) REFERENCES locations(id)
);