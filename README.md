# pet_booking

This service is aimed to book room and check booking status.

The API is organized around REST. 
API has endpoint - 'http://localhost:8080/room/{name}/status' and accepts form-encoded request bodies, returns JSON - responses
                 - 'http://localhost:8080/room/book' and accepts JSON -request, returns JSON - responses

Users can get information about rooms statuses sending GET request to endpoint
Users can book room via POST request to endpoint
the form of request body:
  {
    "roomName": "some string value",
    "minutes": some int value
  }'
  
  Tech stack:
  - java version 11.0.5
  - gradle version 6.9.1
  - junit version 5.3.1
  - springframework boot version 2.5.2
  - image of mySQL database in docker
  - slf4j-log4j12 version 1.7.29

To create databese:
- pull image mySQL
- upd line 4 in run.sh to select where you will store db
- lanch cpmmand n console:
  docker run -d -it  -e MYSQL_ROOT_PASSWORD=1234  -e MYSQL_DATABASE=booking  -v /home/vbulatova/IdeaProjects/mySql-db:/var/lib/mysql  -p 3306:3306  --name mysql_booking mysql:5.7
- check status via command: docker ps
- use query from schema-mysql.sql to create table
- use query from data-mysql.sql to create data
