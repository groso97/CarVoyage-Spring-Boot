CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email_verified BOOLEAN DEFAULT FALSE,
    confirmation_token VARCHAR(255) DEFAULT NULL
);

CREATE TABLE locations(
    location_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    address VARCHAR(50),
    email VARCHAR(50),
    phone_number VARCHAR(50),
    image VARCHAR(255) NOT NULL
);

CREATE TABLE cars (
    car_id INT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    color VARCHAR(20),
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    mileage INT,
    daily_rate DECIMAL(10, 2) NOT NULL,
    location_id INT,
    image VARCHAR(255) NOT NULL,
    fuel_type VARCHAR(20),
    transmission_type VARCHAR(20),
    seats INT,
    doors INT,
    air_conditioning VARCHAR(20),
    tank_size INT,
    FOREIGN KEY(location_id) REFERENCES locations(location_id)
);

CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    car_id INT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('pending', 'confirmed') DEFAULT 'confirmed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (car_id) REFERENCES cars(car_id)
);
