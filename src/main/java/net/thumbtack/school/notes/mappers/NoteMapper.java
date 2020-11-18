package net.thumbtack.school.notes.mappers;

import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.NoteVersion;
import net.thumbtack.school.notes.model.Rating;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface NoteMapper {
    @Insert("INSERT INTO note (user_id, section_id, created, subject) VALUES "
            + "(#{authorId}, #{sectionId}, #{created}, #{subject})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertNote(Note note);

    @Insert("INSERT INTO note_version (note_id, revision_id, body) VALUES "
            + "(#{id}, #{revisionId}, #{body})")
    @Options(useGeneratedKeys = true, keyProperty = "noteVersion.id")
    void insertNoteVersion(NoteVersion noteVersion);

    @Select("SELECT id, user_id AS authorId, section_id AS sectionId, subject, created FROM note WHERE id = #{noteId}")
    @Results(
            {
                    @Result(property = "id", column = "id"),
                    @Result(property = "noteVersion", column = "id", javaType = NoteVersion.class,
                            one = @One(select = "net.thumbtack.school.notes.mappers.NoteMapper.getNoteVersionByNoteId",
                                    fetchType = FetchType.LAZY)),
                    @Result(property = "comments", column = "id", javaType = List.class,
                            many = @Many(select = "net.thumbtack.school.notes.mappers.CommentMapper.getCommentsByNoteId",
                                    fetchType = FetchType.LAZY))
            }
    )
    Note getNoteById(int noteId);

    @Select("SELECT MAX(revision_id) AS revisionId, body, note_id AS id FROM note_version WHERE note_id = #{noteId}")
    NoteVersion getNoteVersionByNoteId(int noteId);

    @Update("UPDATE note SET  section_id = #{sectionId} WHERE id=#{id}")
    void updateNote(Note note);

    @Delete("DELETE FROM note WHERE id= #{id}")
    int deleteNote(Note note);

    @Delete("DELETE FROM note")
    void clear();

    @Insert("INSERT INTO rating (user_id, note_id, number) VALUES (#{authorId}, #{noteId}, #{number}) "
            + "ON DUPLICATE KEY UPDATE number=#{number}")
    void insertRating(Rating rating);

    @Select("SELECT user_id AS authorId, note_id AS noteId, number FROM rating" +
            " WHERE user_id = #{userId} AND note_id = #{noteId}")
    Rating getRating(@Param("userId") int userId, @Param("noteId") int noteId);
}
