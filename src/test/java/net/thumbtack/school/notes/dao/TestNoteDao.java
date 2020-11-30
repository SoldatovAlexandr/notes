package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;

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
}
