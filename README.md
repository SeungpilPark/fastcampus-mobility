# fastcampus-mobility

### Resource Install
* docker-compose 실행 
```
docker-compose -f docker-compose-resources-local.yml up
```

Mysql 접속 정보
* host / port: `localhost:3306`
* username: `root`
* password: `root`
* database: mobility


### Data Migration (flyway)

```
./gradlew :migration:flywayClean :migration:flywayBaseline :migration:flywayMigrate
```

