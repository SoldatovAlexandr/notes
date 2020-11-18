package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestNoteDao extends TestBaseDao {

    @Test
    public void testInsertAndGetNote() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic");
        userDao.insert(user);

        Section section = new Section(user.getId(), "Some section name");

        sectionDao.insert(section);

        NoteVersion noteVersion = new NoteVersion(0, 1, "body");
        Note note = new Note(0, "subject", section.getId(), user.getId(),
                getCurrentDateTime(), noteVersion, new ArrayList<>());

        noteDao.insertNote(note);

        noteVersion.setId(note.getId());

        noteDao.insertNoteVersion(noteVersion);

        Note noteFromDb = noteDao.getNoteById(note.getId());

        Assertions.assertEquals(note, noteFromDb);
    }

    @Test
    public void testUpdateNote() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic");
        userDao.insert(user);

        Section section1 = new Section(user.getId(), "Some section name1");
        Section section2 = new Section(user.getId(), "Some section name2");

        sectionDao.insert(section1);
        sectionDao.insert(section2);

        Note note = new Note(0, "subject", section1.getId(), user.getId(),
                getCurrentDateTime(), null, new ArrayList<>());

        noteDao.insertNote(note);

        note.setSectionId(section2.getId());

        noteDao.updateNote(note);

        Note noteFromDb = noteDao.getNoteById(note.getId());

        Assertions.assertEquals(note, noteFromDb);
    }

    @Test
    public void testDeleteNote() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic");
        userDao.insert(user);

        Section section = new Section(user.getId(), "Some section name");

        sectionDao.insert(section);


        Note note = new Note(0, "subject", section.getId(), user.getId(),
                getCurrentDateTime(), null, new ArrayList<>());

        noteDao.insertNote(note);

        noteDao.deleteNote(note);

        Note noteFromDb = noteDao.getNoteById(note.getId());

        Assertions.assertNull(noteFromDb);
    }

    @Test
    public void testInsertAndGetRating() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic");
        userDao.insert(user);

        Section section = new Section(user.getId(), "Some section name");

        sectionDao.insert(section);


        Note note = new Note(0, "subject", section.getId(), user.getId(),
                getCurrentDateTime(), null, new ArrayList<>());

        noteDao.insertNote(note);

        Rating rating = new Rating(user.getId(), note.getId(), 5);

        noteDao.insertRating(rating);

        Rating ratingFromDb = noteDao.getRating(user.getId(), note.getId());

        Assertions.assertEquals(rating, ratingFromDb);
    }

    @Test
    public void testDoubleInsertAndGetRating() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic");
        userDao.insert(user);

        Section section = new Section(user.getId(), "Some section name");

        sectionDao.insert(section);

        Note note = new Note(0, "subject", section.getId(), user.getId(),
                getCurrentDateTime(), null, new ArrayList<>());

        noteDao.insertNote(note);

        Rating rating = new Rating(user.getId(), note.getId(), 5);

        noteDao.insertRating(rating);

        rating.setNumber(4);

        noteDao.insertRating(rating);

        Rating ratingFromDb = noteDao.getRating(user.getId(), note.getId());

        Assertions.assertEquals(rating, ratingFromDb);
    }
}
