package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.Comment;
import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.Section;
import net.thumbtack.school.notes.model.Session;
import org.springframework.core.NestedRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = {NestedRuntimeException.class, ServerException.class})
public class BaseService {
    protected final UserDao userDao;
    protected final SectionDao sectionDao;
    protected final NoteDao noteDao;
    protected final CommentDao commentDao;

    protected final String cookieName = "JAVASESSIONID";

    public BaseService(UserDao userDao, SectionDao sectionDao, NoteDao noteDao, CommentDao commentDao) {
        this.userDao = userDao;
        this.sectionDao = sectionDao;
        this.noteDao = noteDao;
        this.commentDao = commentDao;
    }

    protected Session getSession(String token) throws ServerException {
        Session session = userDao.getSessionByToken(token);

        if (session == null) {
            throw new ServerException(ServerErrorCodeWithField.UNAUTHORIZED_ACCESS);
        }

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
}
