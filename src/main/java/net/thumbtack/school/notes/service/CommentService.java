package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
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
public class CommentService extends ServiceBase {
    @Autowired
    public CommentService(UserDao userDao, SectionDao sectionDao, NoteDao noteDao, CommentDao commentDao, Config config) {
        super(userDao, sectionDao, noteDao, commentDao, config);
    }

    public CommentInfoDtoResponse createComment(CreateCommentDtoRequest createCommentDtoRequest, String token)
            throws ServerException {
        Session session = getSession(token);

        Comment comment = CommentDtoMapper.INSTANCE.toComment(createCommentDtoRequest);

        Note note = getNote(comment.getNote().getId());

        comment.setAuthor(session.getUser());

        comment.setRevisionId(note.getCurrentVersion().getRevisionId());

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

        Note note = getNote(comment.getNote().getId());

        checkCommentPermission(comment, session.getUser());

        comment.setBody(updateCommentDtoRequest.getBody());

        comment.setRevisionId(note.getCurrentVersion().getRevisionId());

        commentDao.updateComment(comment);

        return CommentDtoMapper.INSTANCE.toCommentInfoDtoResponse(comment);
    }

    public EmptyDtoResponse deleteComment(int commentId, String token) throws ServerException {
        Session session = getSession(token);

        Comment comment = getComment(commentId);

        Note note = getNote(comment.getNote().getId());

        checkCommentPermission(note, session.getUser(), comment);

        commentDao.deleteComment(comment);

        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse deleteComments(int noteId, String token) throws ServerException {
        Session session = getSession(token);

        Note note = getNote(noteId);

        checkCommentPermission(note, session.getUser());

        commentDao.deleteCommentsByNote(noteId, note.getCurrentVersion().getRevisionId());

        return new EmptyDtoResponse();
    }

    private void checkCommentPermission(Comment comment, User user) throws ServerException {
        if (comment.getAuthor().getId() != user.getId()) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }
    }

    private void checkCommentPermission(Note note, User user, Comment comment) throws ServerException {
        if (!(isSuper(user) || isAuthor(note, user) || isAuthor(comment, user))) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }
    }

    private void checkCommentPermission(Note note, User user) throws ServerException {
        if (!isAuthor(note, user)) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }
    }
}
