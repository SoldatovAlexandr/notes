package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.request.CreateCommentDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateCommentDtoRequest;
import net.thumbtack.school.notes.dto.response.CommentInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class TestCommentService {


    private static final int USER_ID = 10;
    private static final int ANOTHER_USER_ID = 100;
    private static final int NOTE_ID = 24;
    private static final int NEW_COMMENT_ID = 0;
    private static final int COMMENT_ID = 44;
    private static final int FIRST_NOTE_VERSION = 1;
    private static final int SECOND_NOTE_VERSION = 2;
    private static final int THIRD_NOTE_VERSION = 3;
    private static final int IDLE_TIMEOUT = 3600;

    private static final String TOKEN = "some-token";
    private static final String BODY = "body";
    private static final String NEW_BODY = "new body";
    @Captor
    ArgumentCaptor<Comment> commentCaptor;
    @MockBean
    private UserDao userDao;
    @MockBean
    private CommentDao commentDao;
    @MockBean
    private NoteDao noteDao;
    @MockBean
    private Config config;
    @MockBean
    private SectionDao sectionDao;

    @Test
    public void testCreateComment() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        Note note = Mockito.mock(Note.class);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(note.getCurrentVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(SECOND_NOTE_VERSION);

        when(note.getId()).thenReturn(NOTE_ID);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        CreateCommentDtoRequest request = new CreateCommentDtoRequest(BODY, NOTE_ID);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(NEW_COMMENT_ID, BODY, note, user, SECOND_NOTE_VERSION, created);

        CommentInfoDtoResponse expectedResponse = new CommentInfoDtoResponse(NEW_COMMENT_ID, BODY, NOTE_ID,
                USER_ID, SECOND_NOTE_VERSION, created.toString());

        CommentInfoDtoResponse response = commentService.createComment(request, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(commentDao).insertComment(comment)
        );
    }

    @Test
    public void testCreateCommentFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        CreateCommentDtoRequest request = new CreateCommentDtoRequest(BODY, NOTE_ID);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.createComment(request, TOKEN)
        );
    }

    @Test
    public void testCreateCommentFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        CreateCommentDtoRequest request = new CreateCommentDtoRequest(BODY, NOTE_ID);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.createComment(request, TOKEN)
        );
    }

    @Test
    public void testGetComments() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Note note = Mockito.mock(Note.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        List<Comment> comments = new ArrayList<>();

        User author11 = Mockito.mock(User.class);

        User author14 = Mockito.mock(User.class);

        int userId11 = 11;

        int userId14 = 14;

        when(author11.getId()).thenReturn(userId11);

        when(author14.getId()).thenReturn(userId14);

        comments.add(new Comment(1, BODY, note, user, SECOND_NOTE_VERSION, created));
        comments.add(new Comment(2, BODY, note, author11, THIRD_NOTE_VERSION, created));
        comments.add(new Comment(3, BODY, note, author14, FIRST_NOTE_VERSION, created));

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(note.getId()).thenReturn(NOTE_ID);

        when(note.getComments()).thenReturn(comments);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        List<CommentInfoDtoResponse> expectedResponse = new ArrayList<>();

        expectedResponse.add(new CommentInfoDtoResponse(1, BODY, NOTE_ID, USER_ID, SECOND_NOTE_VERSION,
                created.toString()));
        expectedResponse.add(new CommentInfoDtoResponse(2, BODY, NOTE_ID, userId11, THIRD_NOTE_VERSION,
                created.toString()));
        expectedResponse.add(new CommentInfoDtoResponse(3, BODY, NOTE_ID, userId14, FIRST_NOTE_VERSION,
                created.toString()));

        List<CommentInfoDtoResponse> response = commentService.getComments(NOTE_ID, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).getNoteById(NOTE_ID)
        );
    }

    @Test
    public void testGetCommentsFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.getComments(NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testGetCommentsFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> commentService.getComments(NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testUpdateComment() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Note note = Mockito.mock(Note.class);

        User user = Mockito.mock(User.class);

        Comment comment = new Comment(COMMENT_ID, BODY, note, user, SECOND_NOTE_VERSION, created);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(commentDao.getCommentById(COMMENT_ID)).thenReturn(comment);

        when(note.getCurrentVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(SECOND_NOTE_VERSION);

        when(note.getId()).thenReturn(NOTE_ID);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        CommentInfoDtoResponse expectedResponse = new CommentInfoDtoResponse(COMMENT_ID, NEW_BODY, NOTE_ID, USER_ID,
                SECOND_NOTE_VERSION, created.toString());

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest(NEW_BODY);

        CommentInfoDtoResponse response = commentService.updateComment(request, COMMENT_ID, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(commentDao).updateComment(commentCaptor.capture()),
                () -> Assertions.assertEquals(NEW_BODY, commentCaptor.getValue().getBody())
        );
    }

    @Test
    public void testUpdateCommentFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest(NEW_BODY);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.updateComment(request, COMMENT_ID, TOKEN)
        );
    }

    @Test
    public void testUpdateCommentFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest(NEW_BODY);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> commentService.updateComment(request, COMMENT_ID, TOKEN)
        );
    }

    @Test
    public void testUpdateCommentFail3() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest(NEW_BODY);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Note note = Mockito.mock(Note.class);

        User user = Mockito.mock(User.class);

        Comment comment = new Comment(COMMENT_ID, BODY, note, user, SECOND_NOTE_VERSION, created);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(note.getId()).thenReturn(NOTE_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(commentDao.getCommentById(COMMENT_ID)).thenReturn(comment);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> commentService.updateComment(request, COMMENT_ID, TOKEN)
        );
    }

    @Test
    public void testUpdateCommentFail4() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        User author = Mockito.mock(User.class);

        Note note = Mockito.mock(Note.class);

        Comment comment = new Comment(COMMENT_ID, BODY, note, author, SECOND_NOTE_VERSION, created);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(author.getId()).thenReturn(ANOTHER_USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(note.getId()).thenReturn(NOTE_ID);

        when(commentDao.getCommentById(COMMENT_ID)).thenReturn(comment);

        when(note.getCurrentVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(SECOND_NOTE_VERSION);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest(NEW_BODY);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.updateComment(request, COMMENT_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteComment1() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        User user = Mockito.mock(User.class);

        Note note = Mockito.mock(Note.class);

        Comment comment = new Comment(COMMENT_ID, BODY, note, user, SECOND_NOTE_VERSION, created);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(note.getAuthor()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(note.getId()).thenReturn(NOTE_ID);

        when(commentDao.getCommentById(COMMENT_ID)).thenReturn(comment);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = commentService.deleteComment(COMMENT_ID, TOKEN);

        Assertions.assertAll(
                () -> verify(commentDao).deleteComment(comment)
        );
    }

    @Test
    public void testDeleteComment2() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Note note = Mockito.mock(Note.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        User author = Mockito.mock(User.class);

        Comment comment = new Comment(COMMENT_ID, BODY, note, author, SECOND_NOTE_VERSION, created);

        when(session.getUser()).thenReturn(user);

        when(author.getId()).thenReturn(USER_ID);

        when(user.getId()).thenReturn(ANOTHER_USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(note.getId()).thenReturn(NOTE_ID);

        when(commentDao.getCommentById(COMMENT_ID)).thenReturn(comment);

        when(note.getAuthor()).thenReturn(user);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = commentService.deleteComment(COMMENT_ID, TOKEN);

        Assertions.assertAll(
                () -> verify(commentDao).deleteComment(comment)
        );
    }

    @Test
    public void testDeleteComment3() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Note note = Mockito.mock(Note.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        User author = Mockito.mock(User.class);

        Comment comment = new Comment(COMMENT_ID, BODY, note, author, SECOND_NOTE_VERSION, created);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(ANOTHER_USER_ID);

        when(author.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(commentDao.getCommentById(COMMENT_ID)).thenReturn(comment);

        when(note.getId()).thenReturn(NOTE_ID);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = commentService.deleteComment(COMMENT_ID, TOKEN);

        Assertions.assertAll(
                () -> verify(commentDao).deleteComment(comment)
        );
    }

    @Test
    public void testDeleteCommentFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComment(COMMENT_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteCommentFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComment(COMMENT_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteCommentFail3() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        Note note = Mockito.mock(Note.class);

        Comment comment = new Comment(COMMENT_ID, BODY, note, user, SECOND_NOTE_VERSION, created);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(commentDao.getCommentById(COMMENT_ID)).thenReturn(comment);

        when(note.getId()).thenReturn(NOTE_ID);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComment(COMMENT_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteCommentFail4() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Note note = Mockito.mock(Note.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        User author = Mockito.mock(User.class);

        Comment comment = new Comment(COMMENT_ID, BODY, note, author, SECOND_NOTE_VERSION, created);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(101);

        when(note.getAuthor()).thenReturn(author);

        when(author.getId()).thenReturn(USER_ID);

        when(note.getId()).thenReturn(NOTE_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(commentDao.getCommentById(COMMENT_ID)).thenReturn(comment);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComment(COMMENT_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteComments() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        Note note = Mockito.mock(Note.class);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(note.getCurrentVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(SECOND_NOTE_VERSION);

        when(note.getAuthor()).thenReturn(user);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = commentService.deleteComments(NOTE_ID, TOKEN);

        Assertions.assertAll(
                () -> verify(commentDao).deleteCommentsByNote(NOTE_ID, SECOND_NOTE_VERSION)
        );
    }

    @Test
    public void testDeleteCommentsFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComments(NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteCommentsFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComments(NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteCommentsFail3() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        User author = Mockito.mock(User.class);

        Note note = Mockito.mock(Note.class);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(author.getId()).thenReturn(ANOTHER_USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(note.getCurrentVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(SECOND_NOTE_VERSION);

        when(note.getAuthor()).thenReturn(author);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComments(NOTE_ID, TOKEN)
        );
    }

}
