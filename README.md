#Run
mvn spring-boot:run

#Requests
* curl -d '{"name":"Jim Smith", "email":"jim.smith@example.com"}' -H 'Content-Type: application/json' -X POST http://localhost:8080/demo/users

