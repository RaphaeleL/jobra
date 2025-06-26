.PHONY: build alias clean test help prep

build:
	mvn clean package

alias: 
	alias jobra="java -jar target/cobra-1.0.0.jar"

clean:
	mvn clean

prep: clean build alias
