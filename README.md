
## Deploy

1. Clone this repo, cd to the directory.
2. In `data/secrets`, copy `sample-secrets.json` to `secrets.json`
3. Edit `secrets.json`
4. In `docker-compose.yml`, change `build: .` to `image: ghcr.io/one-among-us/backend:main` if you don't want to build locally.
4. `docker-compose up -d`

### Update Existing Deploy

1. `git pull`
2. `docker-compose pull`
3. `docker-compose up -d`
