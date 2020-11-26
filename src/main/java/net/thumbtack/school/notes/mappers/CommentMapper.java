package net.thumbtack.school.notes.mappers;

import net.thumbtack.school.notes.model.Comment;
import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CommentMapper {

    @Insert("INSERT INTO comment (user_id, note_id, revision_id, created, body) VALUES "
            + "(#{author.id}, #{note.id}, #{revisionId}, #{created}, #{body})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertComment(Comment comment);

    @Select("SELECT id, user_id AS authorId, note_id AS noteId, revision_id AS revisionId, body, created" +
            " FROM comment WHERE note_id = #{noteId}")
    List<Comment> getCommentsByNoteId(int noteId);

    @Select("SELECT id, user_id AS authorId, note_id AS noteId, revision_id AS revisionId, body, created" +
            " FROM comment WHERE id = #{id}")
    @Results(
            {
                    @Result(property = "author", column = "authorId", javaType = User.class,
                            one = @One(select = "net.thumbtack.school.notes.mappers.UserMapper.getById",
                                    fetchType = FetchType.LAZY)),
                    @Result(property = "note", column = "noteId", javaType = Note.class,
                            one = @One(select = "net.thumbtack.school.notes.mappers.NoteMapper.getNoteById",
                                    fetchType = FetchType.LAZY))
            }
    )
    Comment getCommentById(int id);

    @Update("UPDATE comment SET body = #{body} WHERE id=#{id}")
    void updateComment(Comment comment);

    @Delete("DELETE FROM comment WHERE id= #{id}")
    int deleteComment(Comment comment);

    @Delete("DELETE FROM comment WHERE note_id= #{noteId} AND revision_id=#{revisionId}")
    int deleteCommentsByNoteIdAndRevisionId(@Param("noteId") int noteId, @Param("revisionId") int revisionId);

    @Delete("DELETE FROM comment")
    void clear();
}
