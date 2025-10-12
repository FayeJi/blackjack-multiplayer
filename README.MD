# Online Multiplayer Blackjack

A blackjack web online game supporting multiplayer in real time, players are able to join game room and start play using virtual currency with dealers and other players in the same room.

The project is set up using Java Spring Boot, Spring Security, RESTful API, Spring WebSocket, Spring Data JPA (Hibernate), MySQL, Gradle and Angular.

# Getting Started

Download:

Java JDK 17 or higher

Node.js and npm (for the Angular frontend)

MySQL Server

IDE: IntelliJ IDEA or VS Code are recommended

Backend Setup (Spring Boot)

   1. Clone the repository:


    git clone https://github.com/your-username/your-repo-name.git
    cd your-repo-name



   2. Configure Database Connection:

   Open the backend project.

   Navigate to src/main/resources/application.properties.

   Update the spring.datasource properties with your local MySQL username and password. Ensure the database name is set (it will be created automatically if it doesn't exist).

    spring.datasource.url=jdbc:mysql://localhost:3306/blackjack_db?createDatabaseIfNotExist=true
    spring.datasource.username=your_mysql_username
    spring.datasource.password=your_mysql_password



   3. Build and Run the Application:
   Using IntelliJ IDEA: Open the project, add gradle file and refresh dependency, run the main method in BlackjackApplication.java, no arguments needed.
   
   The backend will start on http://localhost:8080. On the first run, it will automatically create the necessary database tables.

Frontend Setup (Angular)
   1. Navigate to the frontend directory:

    cd ../blackjack-frontend

   2. Install Dependencies:

    npm install

   3. Run the Development Server:

    ng serve

The frontend will be available at http://localhost:4200.
