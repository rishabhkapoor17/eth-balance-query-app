# Ethereum Balance Query Application

## Description
This application runs continuously based on a configured schedule, and queries ETH balances for the specified
wallet address. The configured schedule is default set to run every 60000ms (1 minute), but can be configured
to run every second up until every day. The specified wallet address must start with "0x" and must be 42 characters long 
(including "0x"). The Web3 client is used to query the ETH balance, using the ethereum rpc url defined in the 
application.properties file. By default, it is defined to be on the Base network (https://mainnet.base.org).

The app keeps track of a current address using a boolean value, and stores addresses (along with the last updated time 
and isCurrent boolean) in an address repository (address_config table). The app also keeps track of balance records (which are 
populated with the wallet address, timestamp, and balance) in a balance repository (balance_records_table). These tables 
are stored in a database which is defined in the application.properties file in the spring.datasource.url attribute. 
This attribute is by default set to a local MySQL datasource instance, for database "balance_records_db", which must be created 
before app run. A dependency in the build.gradle file (runtimeOnly 'com.mysql:mysql-connector-j') is defined to allow connections to MySQL. 
The app can be configured to use a different datasource if so desired. The MySQL instance is defined to have a username/
password of eth-app-user/eth-app-password-111, which are also defined in the application.properties file. 

The Gradle as the build tool system, and all Gradle dependencies are defined in the build.gradle file. 
The app uses SpringBoot as the Java framework, to make use of the Spring Jpa repository.

The client facing APIs are defined as follows:
- POST (/api/address/current) setCurrentAddress: Takes a String address in the request body as plaintext (Content type text/plain, raw), returns once the current address is updated
- GET (/api/address/current) getCurrentAddress: Returns the current address
- GET (/api/address/exists/{address}) addressExists: Takes a String address in the request path, returns a boolean for if the address exists in the repository.
- GET (/api/balances) getBalances: Takes a String address, LocalDateTime start (ex: 2023-01-01T00:00:00), LocalDateTime end as request query params, returns a list of balance records for the specified address and timeframe
- POST (/api/update-interval): Takes a String intervalMs as a request query param, returns once the query interval for balances is updated

## Installation and running the backend service
1. Clone the repository:
   1. git clone https://github.com/rishabhkapoor17/eth-balance-query-app
   2. cd eth-balance-query-app
2. Configure and run your MySQL instance:
   1. brew services start mysql
   2. Create a MySQL user to use with the app and input the login values in the application.properties file:
   3. mysql -u root -p
   4. You will be prompted for a password.
   5. CREATE USER 'eth-app-user'@'localhost' IDENTIFIED BY 'eth-app-password-111';
   1. Create a MySQL database
      1. Create a database under this user:
      2. mysql -u eth-app-user -p
      3. You will be prompted for a password.
      4. CREATE DATABASE balance_records_db;
   2. Add it to the application.properties file like so:
      1. spring.datasource.url=jdbc:mysql://localhost:3306/balance_records_db
      2. This assumes your MySQL instance is running on localhost with port 3306. 
   3. The Spring JPA Repository will take care of creating the necessary tables on app run
3. Build this project with Gradle:
   1. cd backend
   2. ./gradlew build
4. Run the service:
   1. ./gradlew bootRun

Troubleshooting:
- Check the system logs on application start
- Ensure your MySQL instance is running on the correct port, which is correctly specified in your spring.datasource.url attribute in the application.properties file
- Ensure your application.properties file contains your correct MySQL username and password
- Ensure your MySQL user has a database created which matches the one provided in your spring.datasource.url attribute in the application.properties file. You can double check by running SHOW DATABASES; on your MySQL user through CLI

## Running the frontend app
1. Install necessary dependencies
   1. cd frontend
   2. npm install
2. Run the app
   1. npm start
