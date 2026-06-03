# Deploy Furnituree to Railway

## 1. Push this project to GitHub

```powershell
git add .
git commit -m "Final Railway deploy config"
git push origin main
```

## 2. Railway services

Railway project must contain exactly these two services:

- E-commerce-web-page-for-Furniture-Sale = Spring Boot app
- MySQL = Railway MySQL database

Do not edit MySQL service variables.

## 3. App Variables

Open the Spring Boot app service, not the MySQL service:

`E-commerce-web-page-for-Furniture-Sale -> Variables -> Raw Editor`

Paste the contents of `RAILWAY_APP_VARIABLES_COPY_ME.env`.

Then click `Deploy changes`.

## 4. Public domain

Open the Spring Boot app service:

`Settings -> Networking -> Public Networking`

Generate a domain and make sure the target port is `8080`.

## 5. Expected success log

The deploy is successful when logs contain:

```text
Tomcat started on port 8080
Started FurnitureeApplication
```
