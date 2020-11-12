package net.thumbtack.school.notes.endpoint;

import net.thumbtack.school.notes.dto.request.AddRatingDtoRequest;
import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateNoteDtoRequest;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.NoteInfoDtoResponse;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/notes")
public class NoteEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoteEndpoint.class);
    private final NoteService noteService;
    private final String cookieName = "JAVASESSIONID";

    @Autowired
    public NoteEndpoint(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public NoteInfoDtoResponse createNote(@Valid @RequestBody CreateNoteDtoRequest createNoteDtoRequest,
                                          @CookieValue(value = cookieName) String token
    ) throws ServerException {
        LOGGER.info("NoteEndpoint create note");
        return noteService.createNote(createNoteDtoRequest, token);
    }

    @GetMapping(value = "/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NoteInfoDtoResponse getProfileInfo(@CookieValue(value = cookieName) String token,
                                              @PathVariable("noteId") int noteId)
            throws ServerException {
        LOGGER.info("NoteEndpoint get note info");
        return noteService.getNoteInfo(noteId, token);
    }

    @PutMapping(value = "/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public NoteInfoDtoResponse updateNote(@Valid @RequestBody UpdateNoteDtoRequest updateNoteDtoRequest,
                                          @CookieValue(value = cookieName) String token,
                                          @PathVariable("noteId") int noteId) throws ServerException {
        LOGGER.info("NoteEndpoint update note");
        return noteService.updateNote(updateNoteDtoRequest, noteId, token);
    }


    @DeleteMapping(value = "/{noteId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse deleteNote(@CookieValue(value = cookieName) String token,
                                       @PathVariable("noteId") int noteId) throws ServerException {
        LOGGER.info("NoteEndpoint delete note");
        return noteService.deleteNote(noteId, token);
    }

    //TODO: сделать этот метод
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNotes(@CookieValue(value = cookieName) String token) throws ServerException {
        LOGGER.info("NoteEndpoint get notes");
        return null;
    }

    @PostMapping(value = "/{noteId}/rating", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse addRating(@Valid @RequestBody AddRatingDtoRequest addRatingDtoRequest,
                                      @CookieValue(value = cookieName) String token,
                                      @PathVariable("noteId") int noteId) throws ServerException {
        LOGGER.info("NoteEndpoint add rating");
        return noteService.addRating(addRatingDtoRequest, noteId, token);
    }
}
