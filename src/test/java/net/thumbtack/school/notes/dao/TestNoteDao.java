package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.dto.request.params.IncludeRequestType;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.model.*;
import net.thumbtack.school.notes.views.NoteView;
import net.thumbtack.school.notes.views.RevisionView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestNoteDao extends TestBaseDao {

    @Test
    public void testInsertAndGetNote() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        NoteVersion noteVersion = new NoteVersion(null, 1, "body");
        Note note = new Note(0, "subject", section, user,
                getCurrentDateTime(), Collections.singletonList(noteVersion), new ArrayList<>());

        noteVersion.setNote(note);

        noteDao.insertNote(note);

        noteDao.insertNoteVersion(noteVersion);

        Note noteFromDb = noteDao.getNoteById(note.getId());

        NoteVersion noteVersionFromDB = noteFromDb.getCurrentVersion();

        Assertions.assertAll(
                () -> Assertions.assertEquals(note.getId(), noteFromDb.getId()),
                () -> Assertions.assertEquals(note.getCreated(), noteFromDb.getCreated()),
                () -> Assertions.assertEquals(note.getAuthor().getId(), noteFromDb.getAuthor().getId()),
                () -> Assertions.assertEquals(noteVersion.getBody(), noteVersionFromDB.getBody()),
                () -> Assertions.assertEquals(noteVersion.getRevisionId(), noteVersionFromDB.getRevisionId())
        );

    }

    @Test
    public void testUpdateNote() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section1 = new Section(user, "Some section name1");
        Section section2 = new Section(user, "Some section name2");

        sectionDao.insert(section1);
        sectionDao.insert(section2);

        Note note = new Note(0, "subject", section1, user,
                getCurrentDateTime(), Collections.emptyList(), new ArrayList<>());

        noteDao.insertNote(note);

        note.setSection(section2);

        noteDao.updateNote(note);

        Note noteFromDb = noteDao.getNoteById(note.getId());

        Assertions.assertEquals(note, noteFromDb);
    }

    @Test
    public void testDeleteNote() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);


        Note note = new Note(0, "subject", section, user, getCurrentDateTime(), Collections.emptyList(),
                new ArrayList<>());

        noteDao.insertNote(note);

        noteDao.deleteNote(note);

        Note noteFromDb = noteDao.getNoteById(note.getId());

        Assertions.assertNull(noteFromDb);
    }

    @Test
    public void testInsertAndGetRating() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);


        Note note = new Note(0, "subject", section, user,
                getCurrentDateTime(), null, new ArrayList<>());

        noteDao.insertNote(note);

        Rating rating = new Rating(user, note, 5);

        noteDao.insertRating(rating);

        Rating ratingFromDb = noteDao.getRating(user.getId(), note.getId());

        Assertions.assertEquals(rating, ratingFromDb);
    }

    @Test
    public void testDoubleInsertAndGetRating() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        Note note = new Note(0, "subject", section, user,
                getCurrentDateTime(), null, new ArrayList<>());

        noteDao.insertNote(note);

        Rating rating = new Rating(user, note, 5);

        noteDao.insertRating(rating);

        rating.setNumber(4);

        noteDao.insertRating(rating);

        Rating ratingFromDb = noteDao.getRating(user.getId(), note.getId());

        Assertions.assertEquals(rating, ratingFromDb);
    }

    @Test
    public void testInsertAndGetNotesUser() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        Note note = new Note(0, "subject", section, user,
                getCurrentDateTime(), null, new ArrayList<>());

        NoteVersion noteVersion = new NoteVersion(note, 1, "body");

        List<NoteView> expectedNotes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {

            noteDao.insertNote(note);

            noteDao.insertNoteVersion(noteVersion);

            expectedNotes.add(
                    new NoteView(note.getId(), user.getId(), section.getId(),
                            note.getSubject(), "body", note.getCreated(),
                            List.of(new RevisionView(noteVersion.getRevisionId(), noteVersion.getBody(),
                                    note.getCreated(), new ArrayList<>()))));
        }

        LocalDateTime timeTo = LocalDateTime.now();

        LocalDateTime timeFrom = timeTo.minusSeconds(60000);

        List<NoteView> notes = noteDao.getNotes(
                section.getId(), SortRequestType.WITHOUT, null, false,
                timeFrom, timeTo, user.getId(), IncludeRequestType.ONLY_IGNORE, 0, 3, user.getId());

        Assertions.assertEquals(expectedNotes, notes);
    }

    @Test
    public void testInsertAndGetNotesUserAsc() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        Note note = new Note(0, "subject", section, user,
                getCurrentDateTime(), null, new ArrayList<>());

        NoteVersion noteVersion = new NoteVersion(note, 1, "body");

        List<NoteView> expectedNotes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {

            noteDao.insertNote(note);

            noteDao.insertNoteVersion(noteVersion);

            Rating rating = new Rating(user, note, i + 1);

            noteDao.insertRating(rating);

            expectedNotes.add(
                    new NoteView(note.getId(), user.getId(), section.getId(),
                            note.getSubject(), "body", note.getCreated(),
                            List.of(new RevisionView(noteVersion.getRevisionId(), noteVersion.getBody(),
                                    note.getCreated(), new ArrayList<>()))));
        }

        LocalDateTime timeTo = LocalDateTime.now();

        LocalDateTime timeFrom = timeTo.minusSeconds(60);

        List<NoteView> notes = noteDao.getNotes(section.getId(), SortRequestType.ASC, null, false,
                timeFrom, timeTo, user.getId(), IncludeRequestType.ONLY_IGNORE, 0, 3, user.getId());

        Assertions.assertEquals(expectedNotes, notes);
    }

    @Test
    public void testInsertAndGetNotesUserDesc() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        Note note = new Note(0, "subject", section, user,
                getCurrentDateTime(), null, new ArrayList<>());

        NoteVersion noteVersion = new NoteVersion(note, 1, "body");

        List<NoteView> expectedNotes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            noteDao.insertNote(note);

            noteDao.insertNoteVersion(noteVersion);

            Rating rating = new Rating(user, note, i + 1);

            noteDao.insertRating(rating);

            expectedNotes.add(
                    new NoteView(note.getId(), user.getId(), section.getId(),
                            note.getSubject(), "body", note.getCreated(),
                            List.of(new RevisionView(noteVersion.getRevisionId(), noteVersion.getBody(),
                                    note.getCreated(), new ArrayList<>()))));
        }

        LocalDateTime timeTo = LocalDateTime.now();

        LocalDateTime timeFrom = timeTo.minusSeconds(60);

        List<NoteView> notes = noteDao.getNotes(section.getId(), SortRequestType.DESC, null, false,
                timeFrom, timeTo, user.getId(), IncludeRequestType.ONLY_IGNORE, 0, 3, user.getId());

        Collections.reverse(expectedNotes);

        Assertions.assertEquals(expectedNotes, notes);
    }

    @Test
    public void testInsertAndGetNotesUserWithTags() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        Note note = new Note(0, "subject", section, user,
                getCurrentDateTime(), null, new ArrayList<>());

        NoteVersion noteVersion = new NoteVersion(note, 1, "body");

        List<NoteView> expectedNotes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            noteDao.insertNote(note);

            noteDao.insertNoteVersion(noteVersion);

            Rating rating = new Rating(user, note, i + 1);

            noteDao.insertRating(rating);
        }

        noteVersion.setBody("animal and human");

        noteDao.insertNote(note);

        noteDao.insertNoteVersion(noteVersion);

        Rating rating = new Rating(user, note, 1);

        noteDao.insertRating(rating);

        expectedNotes.add(
                new NoteView(note.getId(), user.getId(), section.getId(),
                        note.getSubject(), noteVersion.getBody(), note.getCreated(),
                        List.of(new RevisionView(noteVersion.getRevisionId(), noteVersion.getBody(),
                                note.getCreated(), new ArrayList<>()))));


        LocalDateTime timeTo = LocalDateTime.now();

        LocalDateTime timeFrom = timeTo.minusSeconds(60);

        List<NoteView> notes = noteDao.getNotes(section.getId(), SortRequestType.DESC, List.of("animal"), false,
                timeFrom, timeTo, user.getId(), IncludeRequestType.ONLY_IGNORE, 0, 3, user.getId());

        Assertions.assertEquals(expectedNotes, notes);
    }

    @Test
    public void testInsertAndGetNotesUserWithAllTags() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        Section section = new Section(user, "Some section name");

        sectionDao.insert(section);

        Note note = new Note(0, "subject", section, user,
                getCurrentDateTime(), null, new ArrayList<>());

        NoteVersion noteVersion = new NoteVersion(note, 1, "animal");

        List<NoteView> expectedNotes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            noteDao.insertNote(note);

            noteDao.insertNoteVersion(noteVersion);

            Rating rating = new Rating(user, note, i + 1);

            noteDao.insertRating(rating);
        }

        noteVersion.setBody("animal and human");

        noteDao.insertNote(note);

        noteDao.insertNoteVersion(noteVersion);

        Rating rating = new Rating(user, note, 1);

        noteDao.insertRating(rating);

        expectedNotes.add(
                new NoteView(note.getId(), user.getId(), section.getId(),
                        note.getSubject(), noteVersion.getBody(), note.getCreated(),
                        List.of(new RevisionView(noteVersion.getRevisionId(), noteVersion.getBody(),
                                note.getCreated(), new ArrayList<>()))));


        LocalDateTime timeTo = LocalDateTime.now();

        LocalDateTime timeFrom = timeTo.minusSeconds(60);

        List<NoteView> notes = noteDao.getNotes(section.getId(), SortRequestType.DESC, List.of("animal", "human"),
                true, timeFrom, timeTo, user.getId(), IncludeRequestType.ONLY_IGNORE, 0, 3, user.getId());

        Assertions.assertEquals(expectedNotes, notes);
    }
}
