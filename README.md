# 🌸 Sakura Saki

Sakura Saki is a comprehensive and elegantly designed Salon & Spa Management System built with Spring Boot. It provides an intuitive platform for customers to book appointments, and for administrators and staff to manage salon operations efficiently.

## 🌟 Features

### 👤 Role-Based Access Control
- **Admin**: Complete system control, staff management, service/package management, review moderation, and comprehensive reporting.
- **Customer**: Browse services, book appointments, view booking history, manage profile, and submit reviews.
- **Staff (Stylists & Therapists)**: View assigned appointments, manage availability, and access personalized dashboards.

### 📅 Core Functionalities
- **Appointment Management**: Seamless booking system with scheduling and availability checks.
- **Service & Package Management**: Define and categorize salon services and promotional packages.
- **Staff Management**: Track staff schedules, availability, and roles.
- **Review System**: Customers can leave feedback and ratings, which admins can moderate.
- **Dashboards & Reporting**: Insights into daily operations, appointments, and overall business performance.
- **File Uploads**: Support for images and document uploads (configured up to 10MB).

## 🛠️ Tech Stack

- **Backend Framework**: Java 21, Spring Boot 4
- **Web Layer**: Spring Web MVC
- **Security**: Spring Security
- **Templating**: Thymeleaf (with Spring Security integration)
- **Database Layer**: Spring Data JPA, Hibernate
- **Database**: MySQL (Aiven Cloud) for production, H2 for testing/dev
- **Build Tool**: Maven
- **Containerization**: Docker

## 🚀 Getting Started

### Prerequisites
- [Java 21](https://jdk.java.net/21/) or higher
- [Maven 3.8+](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/) (Optional, for containerized deployment)
- MySQL Server (if running locally instead of Aiven Cloud)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/Sakura_Saki.git
   cd Sakura_Saki
   ```

2. **Configure the Database**
   The application is configured to connect to an Aiven Cloud MySQL database via environment variables by default. 
   If you wish to use a local MySQL database, update `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/sakurasaki?createDatabaseIfNotExist=true
       username: root
       password: yourpassword
   ```

3. **Build the Application**
   ```bash
   mvn clean package -DskipTests
   ```

4. **Run the Application**
   Using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
   Or using the generated JAR:
   ```bash
   java -jar target/SakuraSaki-0.0.1-SNAPSHOT.jar
   ```

5. **Access the Application**
   Open your browser and navigate to: `http://localhost:8080`

## 🐳 Docker Deployment

A `Dockerfile` is provided for easy containerization.

1. **Build the Docker Image**
   ```bash
   docker build -t sakurasaki .
   ```

2. **Run the Container**
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATASOURCE_URL="your_db_url" \
     -e SPRING_DATASOURCE_USERNAME="your_db_user" \
     -e SPRING_DATASOURCE_PASSWORD="your_db_password" \
     sakurasaki
   ```

## 🧪 API & Testing

- Postman collections are included in the repository for API testing:
  - `docs/postman/Sakura_Saki_Postman_Collection.json`
- Import these into Postman to explore and test the available REST endpoints.

## 🤝 Contributing
Contributions are welcome! Please fork the repository and create a pull request with your changes.

## 📄 License
This project is licensed under a Custom Proprietary License. See the [LICENSE](LICENSE) file for more details.
