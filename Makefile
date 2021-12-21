#!/usr/bin/env bash
build:
#	sudo ./mvnw clean --quiet package --quiet spring-boot:repackage --quiet -Dmaven.test.skip=true spring-boot:build-info spring-boot:build-image -Dmaven.test.skip=true --quiet
	sudo mvn clean --quiet package --quiet spring-boot:repackage --quiet -Dmaven.test.skip=true
	sudo cp "target/app.jar" "docker/images/java/app.jar";
	sudo rm -rf target
	cd docker/images/java && sudo docker buildx -t firsss21/tg_taxi:arm .
	sudo docker push firsss21/tg_taxi:arm



	#sudo ./mvnw clean --quiet package --quiet spring-boot:repackage --quiet -Dmaven.test.skip=true spring-boot:build-info spring-boot:build-image -Dmaven.test.skip=true --quiet

	# docker run --restart always --net="host" --name tg_taxi  tg_taxi:2.0