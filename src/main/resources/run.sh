docker run -d -it \
 -e MYSQL_ROOT_PASSWORD=1234 \
 -e MYSQL_DATABASE=booking \
 -v ~/IdeaProjects/mySql-db:/var/lib/mysql \
 -p 3306:3306 \
 --name mysql_booking1 mysql:5.7