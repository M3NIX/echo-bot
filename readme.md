Wire bot API is currently in alpha.

1. Clone https://github/wireapp/hello-bot

2. Run make

3. Go to https://wire.com/b/don (not supported on mobile yet)

4. Register
  - Email
  - Website
  - Developer description (e.g. “Yahoo”)
  - Verification email
  - Account review by Wire
  - Account approved email

5. Create new bot (with Don)
  - Name - name of the bot, will also be used as the URL for the bot
  - Base URL
  - Description
  - RSA key (from ./hello-bot/certs/pubkey.pem)

6. Update config file (with auth_token from Don)

7. Deploy service to cloud

8. Enable bot (with Don)
