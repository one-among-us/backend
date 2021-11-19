
## Deploy

1. Create a mysql server.
2. Create file `./secrets/github.txt`  
   The first line is a github token of someone with access to the repo.  
   The second line is the name of the repo (E.g. hykilpikonna/our-data).  
   The third line is the RecaptchaV2 server secret.
3. `docker run -d --restart unless-stopped -v /root/secrets:/app/secrets -p 43482:43482 hykilpikonna/one-among-us-back:1.0.0`
