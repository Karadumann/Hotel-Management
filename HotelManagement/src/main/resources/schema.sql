
CREATE TABLE Role (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(50) NOT NULL
);


INSERT INTO Role (name) VALUES ('Administrator'), ('Client');

CREATE TABLE Hotel (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       address VARCHAR(255) NOT NULL
);


CREATE TABLE "User" (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(255) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      role_id INT,
                      hotel_id INT,
                      FOREIGN KEY (role_id) REFERENCES Role(id),
                      FOREIGN KEY (hotel_id) REFERENCES Hotel(id)
);


CREATE TABLE "Reservation" (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               client_name VARCHAR(255) NOT NULL,
                               room_number INT NOT NULL,
                               check_in DATE NOT NULL,
                               check_out DATE NOT NULL,
                               status VARCHAR(50) NOT NULL,
                               user_id INT,
                               FOREIGN KEY (user_id) REFERENCES "User"(id)
);
