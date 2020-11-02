package net.thumbtack.school.notes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotesServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotesServer.class);

    public static void main(final String[] args) {
        LOGGER.info("Start application");
        SpringApplication.run(NotesServer.class);
        LOGGER.info("Stop application");
    }
}
