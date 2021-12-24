#!/usr/bin/env bash
build:
	sudo mvn clean --quiet package --quiet spring-boot:repackage --quiet -Dmaven.test.skip=true
	sudo cp "target/app.jar" "app.jar";
	sudo rm -rf target
	sudo docker build -t firsss21/learning-tg-bot:arm .
	sudo docker push firsss21/learning-tg-bot:arm



	#sudo ./mvnw clean --quiet package --quiet spring-boot:repackage --quiet -Dmaven.test.skip=true spring-boot:build-info spring-boot:build-image -Dmaven.test.skip=true --quiet

	# docker run --restart always --net="host" --name tg_taxi  tg_taxi:2.0