package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.request.AddRatingDtoRequest;
import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateNoteDtoRequest;
import net.thumbtack.school.notes.dto.request.params.IncludeRequestType;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.NoteDtoResponse;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TestNoteService {

    private static final int USER_ID = 10;
    private static final int ANOTHER_USER_ID = 44;
    private static final int SECTION_ID = 12;
    private static final int IDLE_TIMEOUT = 3600;
    private static final int FIRST_NOTE_VERSION = 1;
    private static final int SECOND_NOTE_VERSION = 2;
    private static final int NEW_NOTE_ID = 0;
    private static final int FIVE_RATING = 5;
    private static final int NOTE_ID = 1000;

    private static final String TOKEN = "some-token";
    private static final String BODY = "body";
    private static final String NEW_BODY = "new body";
    private static final String SUBJECT = "subject";

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    @Captor
    ArgumentCaptor<NoteVersion> noteVersionCaptor;
    @Captor
    ArgumentCaptor<Note> noteCaptor;
    @MockBean
    private UserDao userDao;
    @MockBean
    private CommentDao commentDao;
    @MockBean
    private NoteDao noteDao;
    @MockBean
    private SectionDao sectionDao;
    @MockBean
    private Config config;

    @Test
    public void testInsertNote() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        CreateNoteDtoRequest request = new CreateNoteDtoRequest(SUBJECT, BODY, SECTION_ID);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(section.getId()).thenReturn(SECTION_ID);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(NEW_NOTE_ID, SUBJECT, BODY,
                SECTION_ID, USER_ID, created.format(dateTimeFormatter), FIRST_NOTE_VERSION);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        List<NoteVersion> noteVersions = new ArrayList<>();
        noteVersions.add(noteVersion);

        Note note = new Note(NEW_NOTE_ID, SUBJECT, section, noteVersions, user, created);
        noteVersion.setNote(note);

        NoteInfoDtoResponse response = noteService.createNote(request, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response)
        );
    }

    @Test
    public void testInsertNoteFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        CreateNoteDtoRequest request = new CreateNoteDtoRequest(SUBJECT, BODY, SECTION_ID);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.createNote(request, TOKEN)
        );
    }

    @Test
    public void testInsertNoteFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        CreateNoteDtoRequest request = new CreateNoteDtoRequest(SUBJECT, BODY, SECTION_ID);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> noteService.createNote(request, TOKEN)
        );
    }

    @Test
    public void testGetNoteInfo() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        List<NoteVersion> noteVersions = new ArrayList<>();
        noteVersions.add(noteVersion);

        Section section = Mockito.mock(Section.class);

        User user = Mockito.mock(User.class);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, user, created);

        noteVersion.setNote(note);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(section.getId()).thenReturn(SECTION_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(NOTE_ID, SUBJECT, BODY,
                SECTION_ID, USER_ID, created.format(dateTimeFormatter), FIRST_NOTE_VERSION);

        NoteInfoDtoResponse response = noteService.getNoteInfo(NOTE_ID, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).getNoteById(NOTE_ID)
        );
    }

    @Test
    public void testGetNoteInfoFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.getNoteInfo(NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testGetNoteInfoFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> noteService.getNoteInfo(NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testUpdateNote1() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        User user = Mockito.mock(User.class);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(section.getId()).thenReturn(SECTION_ID);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(NOTE_ID, SUBJECT, NEW_BODY,
                SECTION_ID, USER_ID, created.format(dateTimeFormatter), SECOND_NOTE_VERSION);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest(NEW_BODY, null);

        NoteInfoDtoResponse response = noteService.updateNote(request, NOTE_ID, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).insertNoteVersion(noteVersionCaptor.capture()),
                () -> Assertions.assertEquals(NEW_BODY, noteVersionCaptor.getValue().getBody()),
                () -> verify(noteDao, never()).updateNote(any())
        );
    }

    @Test
    public void testUpdateNote2() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        User user = Mockito.mock(User.class);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(section.getId()).thenReturn(SECTION_ID);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(NOTE_ID, SUBJECT, BODY,
                SECTION_ID, USER_ID, created.format(dateTimeFormatter), FIRST_NOTE_VERSION);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest(null, SECTION_ID);

        NoteInfoDtoResponse response = noteService.updateNote(request, NOTE_ID, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).updateNote(noteCaptor.capture()),
                () -> Assertions.assertEquals(SECTION_ID, noteCaptor.getValue().getSection().getId()),
                () -> verify(noteDao, never()).insertNoteVersion(any())
        );
    }

    @Test
    public void testUpdateNote3() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        User user = Mockito.mock(User.class);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(section.getId()).thenReturn(SECTION_ID);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        NoteInfoDtoResponse expectedResponse = new NoteInfoDtoResponse(NOTE_ID, SUBJECT, BODY,
                SECTION_ID, USER_ID, created.format(dateTimeFormatter), SECOND_NOTE_VERSION);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest(BODY, SECTION_ID);

        NoteInfoDtoResponse response = noteService.updateNote(request, NOTE_ID, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(noteDao).updateNote(noteCaptor.capture()),
                () -> Assertions.assertEquals(SECTION_ID, noteCaptor.getValue().getSection().getId()),
                () -> verify(noteDao).insertNoteVersion(noteVersionCaptor.capture()),
                () -> Assertions.assertEquals(BODY, noteVersionCaptor.getValue().getBody())
        );
    }


    @Test
    public void testUpdateNoteFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest(BODY, SECTION_ID);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.updateNote(request, NOTE_ID, BODY)
        );
    }

    @Test
    public void testUpdateNoteFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        User user = Mockito.mock(User.class);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(section.getId()).thenReturn(SECTION_ID);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(ANOTHER_USER_ID);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest(NEW_BODY, SECTION_ID);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.updateNote(request, NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testUpdateNoteFail3() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        UpdateNoteDtoRequest request = new UpdateNoteDtoRequest(NEW_BODY, null);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.updateNote(request, NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteNote1() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(section.getId()).thenReturn(SECTION_ID);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse emptyDtoResponse = noteService.deleteNote(NOTE_ID, TOKEN);

        Assertions.assertAll(
                () -> verify(noteDao).deleteNote(note),
                () -> verify(noteDao).getNoteById(NOTE_ID)
        );
    }

    @Test
    public void testDeleteNote2() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        User author = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, author, created);

        noteVersion.setNote(note);

        when(section.getId()).thenReturn(SECTION_ID);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(ANOTHER_USER_ID);

        when(author.getId()).thenReturn(SECTION_ID);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse emptyDtoResponse = noteService.deleteNote(NOTE_ID, TOKEN);

        Assertions.assertAll(
                () -> verify(noteDao).deleteNote(note),
                () -> verify(noteDao).getNoteById(NOTE_ID)
        );
    }

    @Test
    public void testDeleteNoteFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.deleteNote(NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteNoteFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> noteService.deleteNote(NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testDeleteNoteFail3() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        User author = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, author, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(ANOTHER_USER_ID);

        when(author.getId()).thenReturn(USER_ID);

        when(section.getId()).thenReturn(SECTION_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> noteService.deleteNote(NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testAddRating() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        User user = Mockito.mock(User.class);

        Session session = Mockito.mock(Session.class);

        User author = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, author, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(author.getId()).thenReturn(USER_ID);

        when(user.getId()).thenReturn(ANOTHER_USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        AddRatingDtoRequest request = new AddRatingDtoRequest(FIVE_RATING);

        Rating rating = new Rating(user, note, FIVE_RATING);

        EmptyDtoResponse response = noteService.addRating(request, NOTE_ID, TOKEN);

        Assertions.assertAll(
                () -> verify(noteDao).insertRating(rating)
        );
    }

    @Test
    public void testAddRatingFail1() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        AddRatingDtoRequest request = new AddRatingDtoRequest(FIVE_RATING);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.addRating(request, NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testAddRatingFail2() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        User user = Mockito.mock(User.class);

        Section section = Mockito.mock(Section.class);

        Session session = Mockito.mock(Session.class);

        NoteVersion noteVersion = new NoteVersion(null, FIRST_NOTE_VERSION, BODY);

        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<NoteVersion> noteVersions = new ArrayList<>();

        noteVersions.add(noteVersion);

        Note note = new Note(NOTE_ID, SUBJECT, section, noteVersions, user, created);

        noteVersion.setNote(note);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(noteDao.getNoteById(NOTE_ID)).thenReturn(note);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        AddRatingDtoRequest request = new AddRatingDtoRequest(FIVE_RATING);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.addRating(request, NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testAddRatingFail3() {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        User user = Mockito.mock(User.class);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        AddRatingDtoRequest request = new AddRatingDtoRequest(FIVE_RATING);

        Assertions.assertThrows(
                ServerException.class, () -> noteService.addRating(request, NOTE_ID, TOKEN)
        );
    }

    @Test
    public void testGetNotes() throws ServerException {
        NoteService noteService = new NoteService(userDao, sectionDao, noteDao, commentDao, config);

        User user = Mockito.mock(User.class);

        Session session = Mockito.mock(Session.class);

        Section section = Mockito.mock(Section.class);

        LocalDateTime timeTo = LocalDateTime.now();

        LocalDateTime timeFrom = timeTo.minusSeconds(100);

        when(session.getUser()).thenReturn(user);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getById(USER_ID)).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        when(noteDao.getNotes(SECTION_ID, SortRequestType.WITHOUT, null, false, timeFrom, timeTo, USER_ID,
                IncludeRequestType.ONLY_IGNORE, 0, 10, USER_ID)).thenReturn(List.of());

        List<NoteDtoResponse> notes = noteService.getNotes(SECTION_ID, SortRequestType.WITHOUT, null, false,
                timeFrom, timeTo, USER_ID, IncludeRequestType.ONLY_IGNORE, false,
                false, false, 0, 10, TOKEN);

        Assertions.assertAll(
                () -> verify(noteDao).getNotes(SECTION_ID, SortRequestType.WITHOUT, null, false, timeFrom,
                        timeTo, USER_ID, IncludeRequestType.ONLY_IGNORE, 0, 10, USER_ID)
        );
    }
}
