#!/bin/bash

DIR_MM="MessageManagerMicroservice"
DIR_TEACHER="TeacherMicroservice"
DIR_STUDENT="StudentMicroservice"
REGISTRY="localhost:5000"

echo "=== 1. Build & Push: Message Manager ==="
cd $DIR_MM || { echo "Nu gasesc folderul $DIR_MM!"; exit 1; }
docker build -t $REGISTRY/message_manager_microservice:v1 .
docker push $REGISTRY/message_manager_microservice:v1
cd ..

echo "=== 2. Build & Push: Teacher ==="
cd $DIR_TEACHER || { echo "Nu gasesc folderul $DIR_TEACHER!"; exit 1; }
echo "-> Pregatesc Teacher.."
cat <<EOF > questions_database.txt
Bem o bere?
Muuuuuuuuuu
Va plac femeile?
Ohhh da
EOF
docker build -t $REGISTRY/teacher_microservice:v1 .
docker push $REGISTRY/teacher_microservice:v1
cd ..

echo "=== 3. Build & Push: Studenti (Tip 1, 2, 3) ==="
cd $DIR_STUDENT || { echo "Nu gasesc folderul $DIR_STUDENT!"; exit 1; }

# Generam Studentul TIP 1
echo "-> Pregatesc Student Tip 1..."
cat <<EOF > questions_database.txt
De unde vin copiii?
De la barza
Cati neuroni are un om?
Multi
EOF
docker build -t $REGISTRY/student_microservice:tip1 .
docker push $REGISTRY/student_microservice:tip1

# Generam Studentul TIP 2
echo "-> Pregatesc Student Tip 2..."
cat <<EOF > questions_database.txt
Unde se da al 3-lea razboi mondial?
Pe Facebook
Tineti post?
Jean Calvin si trixitus inversus
EOF
docker build -t $REGISTRY/student_microservice:tip2 .
docker push $REGISTRY/student_microservice:tip2

# Generam Studentul TIP 3
echo "-> Pregatesc Student Tip 3..."
cat <<EOF > questions_database.txt
Care e sensul vietii?
42
Cat e ceasul?
Cat ti-e nasul
De ce a trecut gaina strada?
Ca sa faca un ou
EOF
docker build -t $REGISTRY/student_microservice:tip3 .
docker push $REGISTRY/student_microservice:tip3
cd ..

echo ""
echo "=== Toate imaginile au fost construite si incarcate in registru cu succes! ==="
docker image ls | grep localhost:5000
