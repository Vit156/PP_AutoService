CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         completed_repairs BIGINT NOT NULL DEFAULT 0 CHECK (completed_repairs >= 0)
);

CREATE TABLE cars (
                      vin_code VARCHAR(50) PRIMARY KEY,
                      car_type VARCHAR(31) NOT NULL,
                      model VARCHAR(100) NOT NULL,
                      brand VARCHAR(100) NOT NULL,
                      release_year INT NOT NULL CHECK (release_year >= 1886),
                      owner_id BIGINT NOT NULL REFERENCES clients(id),
                      company_name VARCHAR(255)
);

CREATE TABLE mechanics (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           is_available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE mechanic_specializations (
                                          mechanic_id BIGINT NOT NULL REFERENCES mechanics(id) ON DELETE CASCADE,
                                          specialization VARCHAR(50) NOT NULL,
                                          PRIMARY KEY (mechanic_id, specialization)
);

CREATE TABLE service_orders (
                                id BIGSERIAL PRIMARY KEY,
                                current_status VARCHAR(50) NOT NULL,
                                assigned_mechanic_id BIGINT REFERENCES mechanics(id),
                                final_price NUMERIC(19, 2) CHECK (final_price >= 0),
                                completed_at TIMESTAMP
);

CREATE TABLE service_order_cars (
                                    order_id BIGINT NOT NULL REFERENCES service_orders(id) ON DELETE CASCADE,
                                    car_vin_code VARCHAR(50) NOT NULL REFERENCES cars(vin_code),
                                    PRIMARY KEY (order_id, car_vin_code)
);

CREATE TABLE jobs (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      status VARCHAR(50) NOT NULL,
                      specialization VARCHAR(50) NOT NULL,
                      work_cost NUMERIC(19, 2) NOT NULL CHECK (work_cost >= 0),
                      parts_cost NUMERIC(19, 2) NOT NULL CHECK (parts_cost >= 0),
                      service_order_id BIGINT REFERENCES service_orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_cars_owner_id ON cars(owner_id);
CREATE INDEX idx_service_orders_mechanic_id ON service_orders(assigned_mechanic_id);
CREATE INDEX idx_service_orders_status ON service_orders(current_status);
CREATE INDEX idx_service_order_cars_vin ON service_order_cars(car_vin_code);
CREATE INDEX idx_jobs_service_order_id ON jobs(service_order_id);