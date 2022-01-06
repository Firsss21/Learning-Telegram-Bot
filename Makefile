#!/usr/bin/env bash
build:
	mvn clean --quiet package --quiet spring-boot:repackage --quiet -Dmaven.test.skip=true
	cp "target/app.jar" "app.jar";
	rm -rf target
