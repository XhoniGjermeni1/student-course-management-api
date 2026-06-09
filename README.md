# Student-Course-Management-API

## Run Project

Start the project with Docker:

```powershell
docker compose up --build
```

After the first build, start it with:

```powershell
docker compose up
```

Run in background:

```powershell
docker compose up -d
```

Stop containers:

```powershell
docker compose down
```

Stop containers and delete the database volume:

```powershell
docker compose down -v
```

## URLs

```text
API:        http://localhost:8081
Swagger:    http://localhost:8081/swagger-ui.html
OpenAPI:    http://localhost:8081/v3/api-docs
MySQL:      localhost:3307
```
