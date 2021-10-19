# Create container
./docker-build.sh

# Run container

docker run --name kira_bot -e TELEBOT_BOT_TOKEN="${TELEBOT_BOT_TOKEN}" -e PASSWORD="${PASSWORD}" -e MARKS_CHAT_ID="${MARKS_CHAT_ID}"  -e USERNAME="${USERNAME}" --net=host -p 8080:8080  kira_bot
