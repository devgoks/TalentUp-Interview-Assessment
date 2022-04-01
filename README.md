# TalentUp-Interview-Assessment
 A micro service that exposes the following 2 APIs
 1. Inbound SMS API (POST) http://localhost:8080/inbound/sms/
 2. Outbound SMS API (POST) http://localhost:8080/outbound/sms/
   
 #### Requirements
 1. Install Redis
 2. Install Postgres
 3. Install Java 8
 4. Fetch postgres data dump from ​SQL dump schema.sql
 
 ## Configuration
  1. Edit application properties file with PostgresDB, database name, Username and Password
 
 ##Steps to run this project:
 1. Clone this Git repository
 2. Navigate to the folder `solution`
 3. Run the application with `mvn spring-boot:run`
 4. You can access the Inbound SMS API
   - POST http://localhost:8080/inbound/sms/
 5. You can access the Outbound SMS API
   - POST http://localhost:8080/outbound/sms/
 
 ##Path To Exercises 
 1. Exercise can be located at `TalentUp Interview Assessment solution\src\main\java\com\talentup\interview\assessment\solution`
 
 2. Exercise tests can be located at `TalentUp Interview Assessment solution\src\test\java\com\talentup\interview\assessment\solution`


