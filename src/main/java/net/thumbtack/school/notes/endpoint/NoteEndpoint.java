package net.thumbtack.school.notes.endpoint;

import net.thumbtack.school.notes.dto.request.AddRatingDtoRequest;
import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateNoteDtoRequest;
import net.thumbtack.school.notes.dto.request.params.IncludeRequestType;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.NoteInfoDtoResponse;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.service.NoteService;
import net.thumbtack.school.notes.views.NoteView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/notes")
public class NoteEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoteEndpoint.class);
    private final static String COOKIE_NAME = "JAVASESSIONID";
    private final NoteService noteService;

    @Autowired
    public NoteEndpoint(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public NoteInfoDtoResponse createNote(@Valid @RequestBody CreateNoteDtoRequest createNoteDtoRequest,
                                          @CookieValue(value = COOKIE_NAME) String token
    ) throws ServerException {
        LOGGER.info("NoteEndpoint create note");
        return noteService.createNote(createNoteDtoRequest, token);
    }

    @GetMapping(value = "/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NoteInfoDtoResponse getProfileInfo(@CookieValue(value = COOKIE_NAME) String token,
                                              @PathVariable("noteId") int noteId)
            throws ServerException {
        LOGGER.info("NoteEndpoint get note info");
        return noteService.getNoteInfo(noteId, token);
    }

    @PutMapping(value = "/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public NoteInfoDtoResponse updateNote(@Valid @RequestBody UpdateNoteDtoRequest updateNoteDtoRequest,
                                          @CookieValue(value = COOKIE_NAME) String token,
                                          @PathVariable("noteId") int noteId) throws ServerException {
        LOGGER.info("NoteEndpoint update note");
        return noteService.updateNote(updateNoteDtoRequest, noteId, token);
    }


    @DeleteMapping(value = "/{noteId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse deleteNote(@CookieValue(value = COOKIE_NAME) String token,
                                       @PathVariable("noteId") int noteId) throws ServerException {
        LOGGER.info("NoteEndpoint delete note");
        return noteService.deleteNote(noteId, token);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NoteView> getNotes(@CookieValue(value = COOKIE_NAME) String token,
                                   @RequestParam(required = false) Integer sectionId,
                                   @RequestParam(required = false) SortRequestType sortByRating,
                                   @RequestParam(required = false) List<String> tags,
                                   @RequestParam(required = false) boolean allTags,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timeFrom,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timeTo,
                                   @RequestParam(required = false, value = "user") Integer userId,
                                   @RequestParam(required = false) IncludeRequestType include,
                                   @RequestParam(required = false) boolean comment,
                                   @RequestParam(required = false) boolean allVersion,
                                   @RequestParam(required = false) boolean commentVersion,
                                   @RequestParam(required = false) Integer from,
                                   @RequestParam(required = false) Integer count
    ) throws ServerException {
        LOGGER.info("NoteEndpoint get notes");
        return noteService.getNotes(sectionId, sortByRating, tags, allTags, timeFrom, timeTo, userId,
                include, comment, allVersion, commentVersion, from, count, token);
    }

    @PostMapping(value = "/{noteId}/rating", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse addRating(@Valid @RequestBody AddRatingDtoRequest addRatingDtoRequest,
                                      @CookieValue(value = COOKIE_NAME) String token,
                                      @PathVariable("noteId") int noteId) throws ServerException {
        LOGGER.info("NoteEndpoint add rating");
        return noteService.addRating(addRatingDtoRequest, noteId, token);
    }
}
