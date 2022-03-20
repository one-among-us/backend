
## Deploy

1. Create a mysql server.
2. Create file `./secrets/secrets.json` (sample below)
3. `docker run -d --restart unless-stopped -v /root/secrets:/app/secrets -p 43482:43482 hykilpikonna/one-among-us-back:1.0.3`

```secrets.json
{
    "githubToken": "Github token of the bot account that has access to the repo",
    "githubRepo": "Name of the repo (E.g. hykilpikonna/our-data)",
    "recaptchaSecret": "RecaptchaV2 server secret",
    "telegramBotToken": "Telegram bot token",
    "telegramChatID": Admin group chat ID
}
```

## Build

1. `docker-compose build`
2. `docker-compose push`
