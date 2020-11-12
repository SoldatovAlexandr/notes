package net.thumbtack.school.notes.mappers;

import net.thumbtack.school.notes.model.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CommentMapper {

    @Insert("INSERT INTO comment (user_id, note_id, revision_id, created, body) VALUES "
            + "(#{authorId}, #{noteId}, #{revisionId}, #{created}, #{body})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertComment(Comment comment);

    @Select("SELECT id, user_id AS authorId, note_id AS noteId, revision_id AS revisionId, body, created" +
            " FROM comment WHERE note_id = #{noteId}")
    List<Comment> getCommentsByNoteId(int noteId);

    @Select("SELECT id, user_id AS authorId, note_id AS noteId, revision_id AS revisionId, body, created" +
            " FROM comment WHERE id = #{id}")
    Comment getCommentById(int id);

    @Update("UPDATE comment SET body = #{body} WHERE id=#{id}")
    void updateComment(Comment comment);

    @Delete("DELETE FROM comment WHERE id= #{id}")
    int deleteComment(Comment comment);

    @Delete("DELETE FROM comment WHERE note_id= #{noteId}")
    int deleteCommentsByNoteId(int noteId);

    @Delete("DELETE FROM comment")
    void clear();
}
