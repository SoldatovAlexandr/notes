package net.thumbtack.school.notes.service;

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

    @MockBean
    private UserDao userDao;

    @MockBean
    private CommentDao commentDao;

    @MockBean
    private NoteDao noteDao;

    @MockBean
    private SectionDao sectionDao;

    @Captor
    ArgumentCaptor<Comment> commentCaptor;

    @Test
    public void testCreateComment() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        Note note = Mockito.mock(Note.class);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(24)).thenReturn(note);

        when(note.getNoteVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(2);

        CreateCommentDtoRequest request = new CreateCommentDtoRequest("body", 24);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(0, "body", 24, 10, 2, created);

        CommentInfoDtoResponse expectedResponse = new CommentInfoDtoResponse(0, "body", 24,
                10, 2, created.toString());

        CommentInfoDtoResponse response = commentService.createComment(request, "some-token");

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(commentDao).insertComment(comment)
        );
    }

    @Test
    public void testCreateCommentFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        CreateCommentDtoRequest request = new CreateCommentDtoRequest("body", 24);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.createComment(request, "some-token")
        );
    }

    @Test
    public void testCreateCommentFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        CreateCommentDtoRequest request = new CreateCommentDtoRequest("body", 24);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.createComment(request, "some-token")
        );
    }

    @Test
    public void testGetComments() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Note note = Mockito.mock(Note.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        List<Comment> comments = new ArrayList<>();

        comments.add(new Comment(1, "body", 12, 10, 2, created));
        comments.add(new Comment(2, "body", 12, 11, 3, created));
        comments.add(new Comment(3, "body", 12, 14, 1, created));

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(12)).thenReturn(note);

        when(note.getComments()).thenReturn(comments);

        List<CommentInfoDtoResponse> expectedResponse = new ArrayList<>();

        expectedResponse.add(new CommentInfoDtoResponse(1, "body", 12,
                10, 2, created.toString()));
        expectedResponse.add(new CommentInfoDtoResponse(2, "body", 12,
                11, 3, created.toString()));
        expectedResponse.add(new CommentInfoDtoResponse(3, "body", 12,
                14, 1, created.toString()));

        List<CommentInfoDtoResponse> response = commentService.getComments(12, "some-token");

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).getNoteById(12)
        );
    }

    @Test
    public void testGetCommentsFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.getComments(12, "some-token")
        );
    }

    @Test
    public void testGetCommentsFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.getComments(12, "some-token")
        );
    }

    @Test
    public void testUpdateComment() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(44, "body", 12, 10, 2, created);

        Note note = Mockito.mock(Note.class);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(12)).thenReturn(note);

        when(commentDao.getCommentById(44)).thenReturn(comment);

        when(note.getNoteVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(2);

        CommentInfoDtoResponse expectedResponse = new CommentInfoDtoResponse(44, "new body", 12, 10,
                2, created.toString());

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest("new body");

        CommentInfoDtoResponse response = commentService.updateComment(request, 44, "some-token");

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(commentDao).updateComment(commentCaptor.capture()),
                () -> Assertions.assertEquals("new body", commentCaptor.getValue().getBody())
        );
    }

    @Test
    public void testUpdateCommentFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest("new body");

        Assertions.assertThrows(
                ServerException.class, () -> commentService.updateComment(request, 44, "some-token")
        );
    }

    @Test
    public void testUpdateCommentFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest("new body");

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.updateComment(request, 44, "some-token")
        );
    }

    @Test
    public void testUpdateCommentFail3() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest("new body");

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(44, "body", 12, 10, 2, created);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(commentDao.getCommentById(44)).thenReturn(comment);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.updateComment(request, 44, "some-token")
        );
    }

    @Test
    public void testUpdateCommentFail4() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(44, "body", 12, 100, 2, created);

        Note note = Mockito.mock(Note.class);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(12)).thenReturn(note);

        when(commentDao.getCommentById(44)).thenReturn(comment);

        when(note.getNoteVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(2);

        UpdateCommentDtoRequest request = new UpdateCommentDtoRequest("new body");

        Assertions.assertThrows(
                ServerException.class, () -> commentService.updateComment(request, 44, "some-token")
        );
    }

    @Test
    public void testDeleteComment1() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(44, "body", 12, 10, 2, created);

        Note note = Mockito.mock(Note.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(12)).thenReturn(note);

        when(commentDao.getCommentById(44)).thenReturn(comment);

        EmptyDtoResponse response = commentService.deleteComment(44, "some-token");

        Assertions.assertAll(
                () -> verify(commentDao).deleteComment(comment)
        );
    }

    @Test
    public void testDeleteComment2() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(44, "body", 12, 10, 2, created);

        Note note = Mockito.mock(Note.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(101);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(12)).thenReturn(note);

        when(commentDao.getCommentById(44)).thenReturn(comment);

        when(note.getAuthorId()).thenReturn(101);

        EmptyDtoResponse response = commentService.deleteComment(44, "some-token");

        Assertions.assertAll(
                () -> verify(commentDao).deleteComment(comment)
        );
    }

    @Test
    public void testDeleteComment3() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(44, "body", 12, 10, 2, created);

        Note note = Mockito.mock(Note.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(101);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(12)).thenReturn(note);

        when(commentDao.getCommentById(44)).thenReturn(comment);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        EmptyDtoResponse response = commentService.deleteComment(44, "some-token");

        Assertions.assertAll(
                () -> verify(commentDao).deleteComment(comment)
        );
    }

    @Test
    public void testDeleteCommentFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComment(44, "some-token")
        );
    }

    @Test
    public void testDeleteCommentFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComment(44, "some-token")
        );
    }

    @Test
    public void testDeleteCommentFail3() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(44, "body", 12, 10, 2, created);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(commentDao.getCommentById(44)).thenReturn(comment);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComment(44, "some-token")
        );
    }

    @Test
    public void testDeleteCommentFail4() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Comment comment = new Comment(44, "body", 12, 10, 2, created);

        Note note = Mockito.mock(Note.class);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(101);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(12)).thenReturn(note);

        when(commentDao.getCommentById(44)).thenReturn(comment);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComment(44, "some-token")
        );
    }

    @Test
    public void testDeleteComments() throws ServerException {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        Note note = Mockito.mock(Note.class);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(22)).thenReturn(note);

        when(note.getNoteVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(2);

        when(note.getAuthorId()).thenReturn(10);

        EmptyDtoResponse response = commentService.deleteComments(22, "some-token");

        Assertions.assertAll(
                () -> verify(commentDao).deleteCommentsByNote(22, 2)
        );
    }

    @Test
    public void testDeleteCommentsFail1() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComments(22, "some-token")
        );
    }

    @Test
    public void testDeleteCommentsFail2() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComments(22, "some-token")
        );
    }

    @Test
    public void testDeleteCommentsFail3() {
        CommentService commentService = new CommentService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        Note note = Mockito.mock(Note.class);

        NoteVersion noteVersion = Mockito.mock(NoteVersion.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(22)).thenReturn(note);

        when(note.getNoteVersion()).thenReturn(noteVersion);

        when(noteVersion.getRevisionId()).thenReturn(2);

        when(note.getAuthorId()).thenReturn(101);

        Assertions.assertThrows(
                ServerException.class, () -> commentService.deleteComments(22, "some-token")
        );
    }

}
