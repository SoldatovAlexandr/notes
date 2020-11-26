package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.request.AddRatingDtoRequest;
import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateNoteDtoRequest;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.NoteInfoDtoResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TestNoteService {
    @MockBean
    private UserDao userDao;

    @MockBean
    private CommentDao commentDao;

    @MockBean
    private NoteDao noteDao;

    @MockBean
    private SectionDao sectionDao;

    @Captor
    ArgumentCaptor<NoteVersion> noteVersionCaptor;

    @Captor
    ArgumentCaptor<Note> noteCaptor;

    @Test
    public void testInsertNote() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        CreateNoteDtoRequest request = new CreateNoteDtoRequest("subject", "body", 12);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(sectionDao.getById(12)).thenReturn(section);

        when(section.getId()).thenReturn(12);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(0, "subject", "body",
                12, 10, created.toString(), 1);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        List<NoteVersion> noteVersions = new ArrayList<>();
        noteVersions.add(noteVersion);

        Note note = new Note(0, "subject", section, noteVersions, user, created);
        noteVersion.setNote(note);

        NoteInfoDtoResponse response = noteService.createNote(request, token);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response)
        );
    }

    @Test
    public void testInsertNoteFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        CreateNoteDtoRequest request = new CreateNoteDtoRequest("subject", "body", 12);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.createNote(request, "some-token")
        );
    }

    @Test
    public void testInsertNoteFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        CreateNoteDtoRequest request = new CreateNoteDtoRequest("subject", "body", 12);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.createNote(request, token)
        );
    }

    @Test
    public void testGetNoteInfo() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        String token = "some-token";

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        List<NoteVersion> noteVersions = new ArrayList<>();
        noteVersions.add(noteVersion);

        Section section = Mockito.mock(Section.class);

        User user = Mockito.mock(User.class);

        Note note = new Note(1000, "subject", section, noteVersions, user, created);

        noteVersion.setNote(note);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(section.getId()).thenReturn(12);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(noteDao.getNoteById(1000)).thenReturn(note);

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(1000, "subject", "body",
                12, 10, created.toString(), 1);

        NoteInfoDtoResponse response = noteService.getNoteInfo(1000, token);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).getNoteById(1000)
        );
    }

    @Test
    public void testGetNoteInfoFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.getNoteInfo(1000, "some-token")
        );
    }

    @Test
    public void testGetNoteInfoFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.getNoteInfo(1000, "some-token")
        );
    }

    @Test
    public void testUpdateNote1() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        User user = Mockito.mock(User.class);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(1000, "subject", section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(section.getId()).thenReturn(12);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(1000)).thenReturn(note);

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(1000, "subject", "new body",
                12, 10, created.toString(), 2);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest("new body", null);

        NoteInfoDtoResponse response = noteService.updateNote(request, 1000, "some-token");

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).insertNoteVersion(noteVersionCaptor.capture()),
                () -> Assertions.assertEquals("new body", noteVersionCaptor.getValue().getBody()),
                () -> verify(noteDao, never()).updateNote(any())
        );
    }

    @Test
    public void testUpdateNote2() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        User user = Mockito.mock(User.class);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(1000, "subject", section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(1000)).thenReturn(note);

        when(sectionDao.getById(13)).thenReturn(section);

        when(section.getId()).thenReturn(13);

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(1000, "subject", "body",
                13, 10, created.toString(), 1);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest(null, 13);

        NoteInfoDtoResponse response = noteService.updateNote(request, 1000, "some-token");

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).updateNote(noteCaptor.capture()),
                () -> Assertions.assertEquals(13, noteCaptor.getValue().getSection().getId()),
                () -> verify(noteDao, never()).insertNoteVersion(any())
        );
    }

    @Test
    public void testUpdateNote3() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        User user = Mockito.mock(User.class);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(1000, "subject", section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(1000)).thenReturn(note);

        when(sectionDao.getById(13)).thenReturn(section);

        when(section.getId()).thenReturn(13);

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(1000, "subject", "new body",
                13, 10, created.toString(), 2);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest("new body", 13);

        NoteInfoDtoResponse response = noteService.updateNote(request, 1000, "some-token");

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).updateNote(noteCaptor.capture()),
                () -> Assertions.assertEquals(13, noteCaptor.getValue().getSection().getId()),
                () -> verify(noteDao).insertNoteVersion(noteVersionCaptor.capture()),
                () -> Assertions.assertEquals("new body", noteVersionCaptor.getValue().getBody())
        );
    }


    @Test
    public void testUpdateNoteFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest("new body", 13);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.updateNote(request, 1000, "some-token")
        );
    }

    @Test
    public void testUpdateNoteFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        User user = Mockito.mock(User.class);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(1000, "subject", section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(section.getId()).thenReturn(12);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(44);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(1000)).thenReturn(note);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest("new body", 13);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.updateNote(request, 1000, "some-token")
        );
    }

    @Test
    public void testUpdateNoteFail3() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest("new body", null);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.updateNote(request, 1000, "some-token")
        );
    }

    @Test
    public void testDeleteNote1() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(1000, "subject", section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(section.getId()).thenReturn(12);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(100)).thenReturn(note);

        EmptyDtoResponse emptyDtoResponse = noteService.deleteNote(100, "some-token");

        Assertions.assertAll(
                () -> verify(noteDao).deleteNote(note),
                () -> verify(noteDao).getNoteById(100)
        );
    }

    @Test
    public void testDeleteNote2() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        User author = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(1000, "subject", section, noteVersions, author, created);

        noteVersion.setNote(note);

        when(section.getId()).thenReturn(12);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(45);

        when(author.getId()).thenReturn(12);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(100)).thenReturn(note);

        EmptyDtoResponse emptyDtoResponse = noteService.deleteNote(100, "some-token");

        Assertions.assertAll(
                () -> verify(noteDao).deleteNote(note),
                () -> verify(noteDao).getNoteById(100)
        );
    }

    @Test
    public void testDeleteNoteFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.deleteNote(100, "some-token")
        );
    }

    @Test
    public void testDeleteNoteFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.deleteNote(100, "some-token")
        );
    }

    @Test
    public void testDeleteNoteFail3() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        User author = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(1000, "subject", section, noteVersions, author, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(45);

        when(author.getId()).thenReturn(10);

        when(section.getId()).thenReturn(12);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(100)).thenReturn(note);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.deleteNote(100, "some-token")
        );
    }

    @Test
    public void testAddRating() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        User user = Mockito.mock(User.class);

        Session session = Mockito.mock(Session.class);

        User author = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(100, "subject", section, noteVersions, author, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(author.getId()).thenReturn(10);

        when(user.getId()).thenReturn(45);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(100)).thenReturn(note);

        AddRatingDtoRequest request = new AddRatingDtoRequest(5);

        Rating rating = new Rating(user, note, 5);

        EmptyDtoResponse response = noteService.addRating(request, 100, "some-token");

        Assertions.assertAll(
                () -> verify(noteDao).insertRating(rating)
        );
    }

    @Test
    public void testAddRatingFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        AddRatingDtoRequest request = new AddRatingDtoRequest(5);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.addRating(request, 100, "some-token")
        );
    }

    @Test
    public void testAddRatingFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        User user = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        Session session = Mockito.mock(Session.class);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(100, "subject", section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(noteDao.getNoteById(100)).thenReturn(note);

        AddRatingDtoRequest request = new AddRatingDtoRequest(5);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.addRating(request, 100, "some-token")
        );
    }

    @Test
    public void testAddRatingFail3() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao);

        User user = Mockito.mock(User.class);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        AddRatingDtoRequest request = new AddRatingDtoRequest(5);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.addRating(request, 100, "some-token")
        );
    }
}
