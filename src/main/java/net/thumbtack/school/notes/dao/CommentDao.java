package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.Comment;

public interface CommentDao {
    void insertComment(Comment comment);

    Comment getCommentById(int commentId);

    void updateComment(Comment comment);

    int deleteComment(Comment comment);

    int deleteCommentsByNote(int noteId, int revisionId);
}