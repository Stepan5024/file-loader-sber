# Запуск проекта

Конфигурация kafka и zookeeper docker-compose.yml

```bash
docker-compose up -d
```
Увеличить количество партиций в кафка
```bash
docker exec -it kafka kafka-topics --alter --topic file_upload_topic --partitions 20 --bootstrap-server localhost:9092
```
Проверить количество партиций в докер контейнере кафки
```bash
docker exec -it kafka kafka-topics --describe --topic file_upload_topic --bootstrap-server localhost:9092
```