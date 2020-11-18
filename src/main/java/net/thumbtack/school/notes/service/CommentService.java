package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.mappers.CommentDtoMapper;
import net.thumbtack.school.notes.dto.request.CreateCommentDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateCommentDtoRequest;
import net.thumbtack.school.notes.dto.response.CommentInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.Comment;
import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService extends BaseService {
    @Autowired
    public CommentService(UserDao userDao, SectionDao sectionDao, NoteDao noteDao, CommentDao commentDao) {
        super(userDao, sectionDao, noteDao, commentDao);
    }

    public CommentInfoDtoResponse createComment(CreateCommentDtoRequest createCommentDtoRequest, String token)
            throws ServerException {
        Session session = getSession(token);

        Comment comment = CommentDtoMapper.INSTANCE.toComment(createCommentDtoRequest);

        Note note = getNote(comment.getNoteId());

        comment.setAuthorId(session.getUser().getId());

        comment.setRevisionId(note.getNoteVersion().getRevisionId());

        comment.setCreated(getCurrentDateTime());

        commentDao.insertComment(comment);

        return CommentDtoMapper.INSTANCE.toCommentInfoDtoResponse(comment);
    }

    public List<CommentInfoDtoResponse> getComments(int noteId, String token) throws ServerException {
        Session session = getSession(token);

        Note note = getNote(noteId);

        return CommentDtoMapper.INSTANCE.toCommentsInfoDtoResponse(note.getComments());
    }

    public CommentInfoDtoResponse updateComment(UpdateCommentDtoRequest updateCommentDtoRequest,
                                                int commentId, String token) throws ServerException {
        Session session = getSession(token);

        Comment comment = getComment(commentId);

        checkCommentPermission(comment, session.getUser());

        //TODO: сделать привязывание к текущей версии заметки
        comment.setBody(updateCommentDtoRequest.getBody());

        commentDao.updateComment(comment);

        return CommentDtoMapper.INSTANCE.toCommentInfoDtoResponse(comment);
    }

    public EmptyDtoResponse deleteComment(int commentId, String token) throws ServerException {
        Session session = getSession(token);

        Comment comment = getComment(commentId);

        //TODO: сделать проверку на автора заметки
        checkCommentPermission(comment, session.getUser());

        commentDao.deleteComment(comment);

        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse deleteComments(int noteId, String token) throws ServerException {
        Session session = getSession(token);

        Note note = getNote(noteId);

        checkCommentPermission(note, session.getUser());

        commentDao.deleteCommentsByNoteId(noteId);

        return new EmptyDtoResponse();
    }

    private void checkCommentPermission(Comment comment, User user) throws ServerException {
        if (comment.getAuthorId() != user.getId()) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }
    }

    private void checkCommentPermission(Note note, User user) throws ServerException {
        if (note.getAuthorId() != user.getId()) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }
    }
}
