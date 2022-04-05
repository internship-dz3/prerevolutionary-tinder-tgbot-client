#!/bin/bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/dz3-bot.pem \
    target/prerevolutionary-tinder-tgbot-client-0.0.1-SNAPSHOT.jar \
    ubuntu@ec2-3-86-187-19.compute-1.amazonaws.com:/home/ubuntu/

echo 'Restart server...'

ssh -i ssh -i "~/.ssh/dz3-bot.pem" ubuntu@ec2-3-86-187-19.compute-1.amazonaws.com << EOF

pgrep java | xargs kill -9
nohup java -jar prerevolutionary-tinder-tgbot-client-0.0.1-SNAPSHOT.jar > log.txt &
EOF

echo 'Bye'

