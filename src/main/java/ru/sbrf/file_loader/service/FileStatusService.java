package ru.sbrf.file_loader.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FileStatusService {

    @KafkaListener(topics = "file_upload_topic", groupId = "file_upload_group")
    public void listen(String message) {
        // Обработка сообщения
        System.out.println("Received message: " + message);
    }
}