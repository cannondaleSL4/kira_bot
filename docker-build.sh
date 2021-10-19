#!/bin/sh

mvn clean install

docker build -t kira_bot .
