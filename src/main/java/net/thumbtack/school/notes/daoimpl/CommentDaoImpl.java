package net.thumbtack.school.notes.daoimpl;

import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.mappers.CommentMapper;
import net.thumbtack.school.notes.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDaoImpl implements CommentDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentDaoImpl.class);
    private final CommentMapper commentMapper;

    @Autowired
    public CommentDaoImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public void insertComment(Comment comment) {
        LOGGER.debug("DAO insert comment: {}", comment);
        commentMapper.insertComment(comment);
    }

    @Override
    public Comment getCommentById(int commentId) {
        LOGGER.debug("DAO get comment by id: {}", commentId);
        return commentMapper.getCommentById(commentId);
    }

    @Override
    public void updateComment(Comment comment) {
        LOGGER.debug("DAO update comment: {}", comment);
        commentMapper.updateComment(comment);
    }

    @Override
    public int deleteComment(Comment comment) {
        LOGGER.debug("DAO delete comment: {}", comment);
        return commentMapper.deleteComment(comment);
    }

    @Override
    public int deleteCommentsByNote(int noteId, int revisionId) {
        LOGGER.debug("DAO delete comments by noteId: {} and revisionId: {}", noteId, revisionId);
        return commentMapper.deleteCommentsByNoteIdAndRevisionId(noteId, revisionId);
    }
}
