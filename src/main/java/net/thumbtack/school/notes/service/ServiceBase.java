package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.*;
import org.springframework.core.NestedRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional(rollbackFor = {NestedRuntimeException.class, ServerException.class})
public class ServiceBase {
    protected final static String COOKIE_NAME = "JAVASESSIONID";
    protected final UserDao userDao;
    protected final SectionDao sectionDao;
    protected final NoteDao noteDao;
    protected final CommentDao commentDao;
    protected final Config config;


    public ServiceBase(UserDao userDao, SectionDao sectionDao, NoteDao noteDao, CommentDao commentDao, Config config) {
        this.userDao = userDao;
        this.sectionDao = sectionDao;
        this.noteDao = noteDao;
        this.commentDao = commentDao;
        this.config = config;
    }

    protected Session getSession(String token) throws ServerException {
        Session session = userDao.getSessionByToken(token);

        LocalDateTime now = LocalDateTime.now();

        if (session == null || !isActiveSession(session.getDate(), now)) {
            throw new ServerException(ServerErrorCodeWithField.UNAUTHORIZED_ACCESS);
        }

        updateSession(session, now);

        return session;
    }

    protected Section getSection(int sectionId) throws ServerException {
        Section section = sectionDao.getById(sectionId);

        if (section == null) {
            throw new ServerException(ServerErrorCodeWithField.WRONG_SECTION_ID);
        }

        return section;
    }

    protected Note getNote(int noteId) throws ServerException {
        Note note = noteDao.getNoteById(noteId);

        if (note == null) {
            throw new ServerException(ServerErrorCodeWithField.WRONG_NOTE_ID);
        }

        return note;
    }

    protected Comment getComment(int commentId) throws ServerException {
        Comment comment = commentDao.getCommentById(commentId);

        if (comment == null) {
            throw new ServerException(ServerErrorCodeWithField.WRONG_COMMENT_ID);
        }

        return comment;
    }

    protected User getUserById(int id) throws ServerException {
        User user = userDao.getById(id);

        if (user == null) {
            throw new ServerException(ServerErrorCodeWithField.WRONG_ID);
        }

        return user;
    }

    protected LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }


    protected boolean isSuper(User user) {
        return user.getType() == UserType.SUPER_USER;
    }

    protected boolean isAuthor(Comment comment, User user) {
        return comment.getAuthor().getId() == user.getId();
    }

    protected boolean isAuthor(Section section, User user) {
        return section.getAuthor().getId() == user.getId();
    }


    protected boolean isAuthor(Note note, User user) {
        return note.getAuthor().getId() == user.getId();
    }

    private boolean isActiveSession(LocalDateTime dateTime, LocalDateTime now) {
        return now.minusSeconds(config.getUserIdleTimeout()).isBefore(dateTime);
    }

    private void updateSession(Session session, LocalDateTime now) {
        session.setDate(now);

        userDao.updateSession(session);
    }
}
