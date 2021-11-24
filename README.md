# pet_booking

A solution, which allows to get statuses of meeting rooms and book, if required.

The API is organized around REST. 
API has endpoint - 'http://localhost:8080/valera' and accepts form-encoded request bodies, returns JSON - responses

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
  - h2 database
  - slf4j-log4j12 version 1.7.29
