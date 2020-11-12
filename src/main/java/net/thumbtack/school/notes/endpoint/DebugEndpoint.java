package net.thumbtack.school.notes.endpoint;

import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.SettingsDtoResponse;
import net.thumbtack.school.notes.service.DebugService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/debug")
public class DebugEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugEndpoint.class);
    private final DebugService debugService;

    @Autowired
    public DebugEndpoint(DebugService debugService) {
        this.debugService = debugService;
    }

    @GetMapping(value = "/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    public SettingsDtoResponse getComments() {
        LOGGER.info("DebugEndpoint get server settings");
        return debugService.getServerSettings();
    }

    @PostMapping(value = "/clear", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse addComment() {
        LOGGER.info("DebugEndpoint clear server");
        return debugService.clear();
    }

}
