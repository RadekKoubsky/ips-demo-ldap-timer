#!/bin/sh

read -p "Type your username please: " username
read -p "Type your password please: " password 

mvn -q exec:java -Dexec.mainClass=com.redhat.xpaas.qe.CompleteTask -Dexec.args="${username} ${password}"


