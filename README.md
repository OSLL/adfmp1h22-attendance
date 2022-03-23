# adfmp1h22-attendance

## Deploy

### Linux

1. Run `docker-compose up -d`
2. Open `app` folder from Android studio and `Run`
3. To stop server run script `stop.sh`

### Windows

1. Run `docker-compose up -d`
2. Go to `Docker Desktop` and remove container
3. Open `Terminal` and run `docker run -p 3001:3001 android_backend_image`
4. Stop server from `Docker Desktop` via deleting container

### Without Docker

1. Go to `./backend` folder
2. Install `npm` (Node Package Manager)
3. Run `npm install`
4. Run `npm run dev`

## Testing

1. To get `Teacher` privellegies `Register` with email `ks@mail.ru`
2. To get `Student` privellegies `Register` with any other email

## Explanations
Now due to some problems with Virtual device I could not test how app gets location info, but I did it as in many tutorials it was done.
