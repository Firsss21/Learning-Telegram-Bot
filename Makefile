#!/usr/bin/env bash
build:
	mvn clean --quiet package --quiet spring-boot:repackage --quiet -Dmaven.test.skip=true -Dspring.profiles.active=prod
	cp "target/app.jar" "app.jar";
	rm -rf target
