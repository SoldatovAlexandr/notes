package net.thumbtack.school.notes.endpoint;

import net.thumbtack.school.notes.dto.request.SectionDtoRequest;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.SectionDtoResponse;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.service.SectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/sections")
public class SectionEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(SectionEndpoint.class);
    private final SectionService sectionService;
    private final String cookieName = "JAVASESSIONID";

    @Autowired
    public SectionEndpoint(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public SectionDtoResponse createSection(@Valid @RequestBody SectionDtoRequest sectionDtoRequest,
                                            @CookieValue(value = cookieName) String token
    ) throws ServerException {
        LOGGER.info("SectionEndpoint create section");
        return sectionService.createSection(sectionDtoRequest, token);
    }

    @PutMapping(value = "/{sectionId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public SectionDtoResponse renameSection(@Valid @RequestBody SectionDtoRequest sectionDtoRequest,
                                            @CookieValue(value = cookieName) String token,
                                            @PathVariable("sectionId") int sectionId) throws ServerException {
        LOGGER.info("SectionEndpoint rename section");
        return sectionService.renameSection(sectionDtoRequest, sectionId, token);
    }

    @DeleteMapping(value = "/{sectionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse removeSection(@CookieValue(value = cookieName) String token,
                                          @PathVariable("sectionId") int sectionId) throws ServerException {
        LOGGER.info("SectionEndpoint remove section");
        return sectionService.removeSection(sectionId, token);
    }

    @GetMapping(value = "/{sectionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SectionDtoResponse getSection(@CookieValue(value = cookieName) String token,
                                         @PathVariable("sectionId") int sectionId)
            throws ServerException {
        LOGGER.info("SectionEndpoint get section");
        return sectionService.getSection(sectionId, token);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SectionDtoResponse> getSections(@CookieValue(value = cookieName) String token) throws ServerException {
        LOGGER.info("SectionEndpoint get sections");
        return sectionService.getSections(token);
    }
}
