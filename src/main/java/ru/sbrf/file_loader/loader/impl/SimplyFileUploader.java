package ru.sbrf.file_loader.loader.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.loader.FileUploader;
import ru.sbrf.file_loader.model.FileLink;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class SimplyFileUploader implements FileUploader {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    // Минимальное и максимальное время задержки
    @Value("${file.upload.min.delay}")
    private int minDelay;

    @Value("${file.upload.max.delay}")
    private int maxDelay;

    @Override
    public boolean uploadFile(FileLink fileLink) {
        log.info("Starting upload for file: {}", fileLink.getFileLink());

        try {
            int delay = random.nextInt(minDelay, maxDelay + 1);
            log.info("Simulating upload delay of {} milliseconds for file: {}", delay, fileLink.getFileLink());

            Thread.sleep(delay);

            log.info("Upload successful for file: {}", fileLink.getFileLink());
            return true;
        } catch (InterruptedException e) {
            log.error("Upload failed for file: {}", fileLink.getFileLink(), e);
            return false;
        }
    }
}