# Запуск проекта

Конфигурация kafka и zookeeper docker-compose.yml
1. Поднять кафку и зукипер на порту 9092
```bash
docker-compose up -d
```
2. По умолчанию количество партиций = 1, можно увеличить после запуска контейнера командой ниже 
Увеличить количество партиций в кафка:
```bash
docker exec -it kafka kafka-topics --alter --topic file_upload_topic --partitions 20 --bootstrap-server localhost:9092
```


Проверить количество партиций в докер контейнере кафки
```bash
docker exec -it kafka kafka-topics --describe --topic file_upload_topic --bootstrap-server localhost:9092
```

3. Зпустить проект в idea FileLoaderApplication

4. Отправлять запросы POST http://localhost:8080/api/v1/fileloader/file/send


```json
{
  "requestId": "154d68301e1342f3bd91a0c143efdb3a",
  "consumer": "StreamShark",
  "fileLinks": [
    {
      "fileLink": "A123D0E5-B96A-4204-9BE6-3744369019C1"
    },
    {
      "fileLink": "B747D0E5-C96A-4304-8BE6-3744369029D2"
    },
    {
      "fileLink": "C847D0E5-D96B-4404-7BE6-3744369039E3"
    }
  ]
}

```


5. Проверить статусы 
GET http://localhost:8080/api/v1/fileloader/file/get

Ответ в форме:

```bash
{
    "requestId": "154d68301e1342f3bd91a0c143efdb3a",
    "fileLinks": [
        {
            "fileLink": "A123D0E5-B96A-4204-9BE6-3744369019C1",
            "status": "PENDING"
        },
        {
            "fileLink": "B747D0E5-C96A-4304-8BE6-3744369029D2",
            "status": "IN_PROGRESS"
        },
        {
            "fileLink": "C847D0E5-D96B-4404-7BE6-3744369039E3",
            "status": "PENDING"
        }
    ]
}


```

6. Посмотреть БД H2
   http://localhost:8080/h2-console/

URL jdbc:h2:mem:file-loader

User: sa
Password: password


