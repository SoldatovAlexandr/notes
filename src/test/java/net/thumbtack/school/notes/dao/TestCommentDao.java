package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestCommentDao extends TestBaseDao {

    @Test
    public void testInsertAndGetComment() {
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

        Comment comment = new Comment(0, "body", note.getId(), user.getId(),
                noteVersion.getRevisionId(), getCurrentDateTime());

        commentDao.insertComment(comment);

        Comment commentFromDb = commentDao.getCommentById(comment.getId());

        Assertions.assertEquals(comment, commentFromDb);
    }

    @Test
    public void testUpdateAndGetComment() {
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

        Comment comment = new Comment(0, "body", note.getId(), user.getId(),
                noteVersion.getRevisionId(), getCurrentDateTime());

        commentDao.insertComment(comment);

        comment.setBody("new body");

        commentDao.updateComment(comment);

        Comment commentFromDb = commentDao.getCommentById(comment.getId());

        Assertions.assertEquals(comment, commentFromDb);
    }

    @Test
    public void testDeleteComment() {
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

        Comment comment = new Comment(0, "body", note.getId(), user.getId(),
                noteVersion.getRevisionId(), getCurrentDateTime());

        commentDao.insertComment(comment);

        commentDao.deleteComment(comment);

        Comment commentFromDb = commentDao.getCommentById(comment.getId());

        Assertions.assertNull(commentFromDb);
    }

    @Test
    public void testDeleteCommentsByNote() {
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

        Comment comment1 = new Comment(0, "body first comment", note.getId(), user.getId(),
                noteVersion.getRevisionId(), getCurrentDateTime());

        Comment comment2 = new Comment(0, "body second comment", note.getId(), user.getId(),
                noteVersion.getRevisionId(), getCurrentDateTime());

        commentDao.insertComment(comment1);
        commentDao.insertComment(comment2);

        commentDao.deleteCommentsByNoteId(note.getId());

        Comment comment1FromDb = commentDao.getCommentById(comment1.getId());
        Comment comment2FromDb = commentDao.getCommentById(comment2.getId());

        Assertions.assertAll(
                () -> Assertions.assertNull(comment1FromDb),
                () -> Assertions.assertNull(comment2FromDb)
        );
    }

}
