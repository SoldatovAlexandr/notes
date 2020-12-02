package net.thumbtack.school.notes.endpoint;

import net.thumbtack.school.notes.dto.request.CreateCommentDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateCommentDtoRequest;
import net.thumbtack.school.notes.dto.response.CommentInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.service.CommentService;
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
@RequestMapping("/api")
public class CommentEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentEndpoint.class);
    private final static String COOKIE_NAME = "JAVASESSIONID";
    private final CommentService commentService;

    @Autowired
    public CommentEndpoint(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(value = "/comments", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommentInfoDtoResponse addComment(@Valid @RequestBody CreateCommentDtoRequest createCommentDtoRequest,
                                             @CookieValue(value = COOKIE_NAME) String token) throws ServerException {
        LOGGER.info("CommentEndpoint create comment");
        return commentService.createComment(createCommentDtoRequest, token);
    }

    @GetMapping(value = "/notes/{noteId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommentInfoDtoResponse> getComments(@CookieValue(value = COOKIE_NAME) String token,
                                                    @PathVariable("noteId") int noteId)
            throws ServerException {
        LOGGER.info("CommentEndpoint get comments");
        return commentService.getComments(noteId, token);
    }

    @PutMapping(value = "/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommentInfoDtoResponse updateNote(@Valid @RequestBody UpdateCommentDtoRequest updateCommentDtoRequest,
                                             @CookieValue(value = COOKIE_NAME) String token,
                                             @PathVariable("commentId") int commentId) throws ServerException {
        LOGGER.info("CommentEndpoint update comments");
        return commentService.updateComment(updateCommentDtoRequest, commentId, token);
    }

    @DeleteMapping(value = "comments/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse deleteComment(@CookieValue(value = COOKIE_NAME) String token,
                                          @PathVariable("commentId") int commentId) throws ServerException {
        LOGGER.info("CommentEndpoint delete comment");
        return commentService.deleteComment(commentId, token);
    }

    @DeleteMapping(value = "notes/{noteId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse deleteComments(@CookieValue(value = COOKIE_NAME) String token,
                                           @PathVariable("noteId") int noteId) throws ServerException {
        LOGGER.info("CommentEndpoint delete comments");
        return commentService.deleteComments(noteId, token);
    }


}
