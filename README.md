# TalentUp-Interview-Assessment
 A micro service that exposes the following 2 APIs
 1. Inbound SMS API (POST) http://localhost:8080/inbound/sms/
 2. Outbound SMS API (POST) http://localhost:8080/outbound/sms/
   
 #### Requirements
 1. Install Redis
 2. Install Postgres
 3. Install Java 8
 4. Install Maven (for running Java Springboot Apps)
 5. Fetch postgres data dump from ​SQL dump schema.sql
 
 #### Configuration
  1. Edit application properties file with PostgresDB, database name, Username and Password
  2. The application.properties to edit is located at solution\src\main\resources\application.properties
 
 #### Steps to run this project:
 1. Clone this Git repository
 2. Navigate to the folder `solution`
 3. Run the application with `mvn spring-boot:run`
 4. You can access the Inbound SMS API
   - POST http://localhost:8080/inbound/sms/
 5. You can access the Outbound SMS API
   - POST http://localhost:8080/outbound/sms/
 
 ####  Hosted Service
 This service is hosted on Linode and can be accessed at IP - 139.162.239.14
 1. You can access the Inbound SMS API
    - POST http://139.162.239.14:8080/inbound/sms/
 2. You can access the Outbound SMS API
    - POST http://139.162.239.14:8080/outbound/sms/
 3. I created a postman collection you can import to test it with postman. 
 4. This is the link to the postman collection https://www.getpostman.com/collections/6b65e8162448f268506a
 
 ##Path To Exercise
 1. Exercise can be located at `TalentUp Interview Assessment solution\src\main\java\com\talentup\interview\assessment\solution`
 
 2. Exercise tests can be located at `TalentUp Interview Assessment solution\src\test\java\com\talentup\interview\assessment\solution`


