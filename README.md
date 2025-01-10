# Hotel Management System

A comprehensive hotel management system built with Java and JavaFX that enables efficient management of hotel operations through a user-friendly interface.

### Owner Panel
- Create and manage manager accounts
- Add and remove rooms
- Monitor hotel statistics
- View client information and ratings
- Track additional services usage

### Manager Panel
- Create and manage receptionist accounts
- View notifications and alerts
- Monitor hotel operations
- Access reservation reports

### Receptionist Panel
- Manage room reservations
- Handle client check-in/check-out
- Process additional services
- Manage client ratings and feedback

## 🛠 Technologies Used

- **Java** - Core programming language
- **JavaFX** - GUI framework
- **MySQL** - Database management
- **Maven** - Dependency management
- **JUnit & TestNG** - Testing frameworks
- **Scene Builder** - UI design tool

## 📋 System Requirements

- Java 11 or higher
- MySQL Database
- Maven

## 🚀 Installation

1. Clone the repository
```bash
git clone https://github.com/yourusername/Hotel-Management.git
```

2. Navigate to project directory
```bash
cd Hotel-Management
```

3. Configure Database
- Create a MySQL database
- Update database connection settings in `DatabaseConnection.java`

4. Build the project
```bash
mvn clean install
```

5. Run the application
```bash
mvn javafx:run
```

## 💻 Usage

1. **Login System**
   - Different access levels for owners, managers, and receptionists
   - Secure authentication system

2. **Room Management**
   - Multiple room categories (Standard, Deluxe, Suite, etc.)
   - Real-time availability tracking
   - Room status monitoring

3. **Reservation System**
   - Create and manage bookings
   - Handle check-in/check-out
   - Process cancellations

4. **Additional Services**
   - Manage extra services
   - Track service usage
   - Seasonal service offerings

5. **Client Management**
   - Store client information
   - Handle client feedback
   - Rating system

##  Security Features

- Role-based access control
- Secure password handling
- Session management

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📧 Contact

For any questions or suggestions, please open an issue in the GitHub repository.

## 🙏 Acknowledgments

- Thanks to all contributors who have helped with the project
- Special thanks to the JavaFX and MySQL communities for their excellent documentation 
