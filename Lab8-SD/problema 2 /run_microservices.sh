#!/bin/bash

echo "=== Curatare mediu (oprire/stergere containere vechi) ==="
docker stop message_manager teacher_microservice student_microservice_1 student_microservice_2 student_microservice_3 2>/dev/null
docker rm message_manager teacher_microservice student_microservice_1 student_microservice_2 student_microservice_3 2>/dev/null

echo "=== Creare retea virtuala ms-net ==="
docker network create ms-net 2>/dev/null

echo "=== 1. Pornire Message Manager (Port 1500) ==="
docker run -d -p 1500:1500 --name message_manager --network=ms-net localhost:5000/message_manager_microservice:v1
sleep 2

echo "=== 2. Pornire Teacher Microservice (Port 1600) ==="
docker run -d -p 1600:1600 -e MESSAGE_MANAGER_HOST='message_manager' --name teacher_microservice --network=ms-net localhost:5000/teacher_microservice:v1

echo "=== 3. Pornire Student 1 (Tip 1 - Port 1701) ==="
docker run -d -p 1701:1701 -e MESSAGE_MANAGER_HOST='message_manager' -e STUDENT_PORT='1701' --name student_microservice_1 --network=ms-net localhost:5000/student_microservice:tip1

echo "=== 4. Pornire Student 2 (Tip 2 - Port 1702) ==="
docker run -d -p 1702:1702 -e MESSAGE_MANAGER_HOST='message_manager' -e STUDENT_PORT='1702' --name student_microservice_2 --network=ms-net localhost:5000/student_microservice:tip2

echo "=== 5. Pornire Student 3 (Tip 3 - Port 1703) ==="
docker run -d -p 1703:1703 -e MESSAGE_MANAGER_HOST='message_manager' -e STUDENT_PORT='1703' --name student_microservice_3 --network=ms-net localhost:5000/student_microservice:tip3

echo ""
echo "=== Toate microserviciile au fost pornite cu succes! ==="
docker ps
