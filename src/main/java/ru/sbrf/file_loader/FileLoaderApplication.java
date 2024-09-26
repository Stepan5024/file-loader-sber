package ru.sbrf.file_loader;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class FileLoaderApplication {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (AdminClient adminClient = AdminClient.create(properties)) {
            Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(Collections.singletonList("file_upload_topic"))
                    .all().get();

            TopicDescription topicDescription = topicDescriptionMap.get("file_upload_topic");
            int partitionCount = topicDescription.partitions().size();

            System.out.println("Количество партиций в теме file_upload_topic: " + partitionCount);

            SpringApplication.run(FileLoaderApplication.class, args);
        }
    }

}
