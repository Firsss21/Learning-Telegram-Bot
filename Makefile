#!/usr/bin/env bash
build:
	sudo mvn clean --quiet package --quiet spring-boot:repackage --quiet -Dmaven.test.skip=true
	sudo cp "target/app.jar" "app.jar";
	sudo rm -rf target
	#sudo docker build -t firsss21/learning-tg-bot:arm .
	#sudo docker push firsss21/learning-tg-bot:arm
