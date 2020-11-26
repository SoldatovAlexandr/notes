package net.thumbtack.school.notes.mappers;

import net.thumbtack.school.notes.model.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface NoteMapper {
    @Insert("INSERT INTO note (user_id, section_id, created, subject) VALUES "
            + "(#{author.id}, #{section.id}, #{created}, #{subject})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertNote(Note note);

    @Insert("INSERT INTO note_version (note_id, revision_id, body) VALUES "
            + "(#{note.id}, #{revisionId}, #{body})")
    void insertNoteVersion(NoteVersion noteVersion);

    @Select("SELECT id, user_id AS authorId, section_id AS sectionId, subject, created FROM note WHERE id = #{noteId}")
    @Results(
            {
                    @Result(property = "id", column = "id"),
                    @Result(property = "author", column = "authorId", javaType = User.class,
                            one = @One(select = "net.thumbtack.school.notes.mappers.UserMapper.getById",
                                    fetchType = FetchType.LAZY)),
                    @Result(property = "section", column = "sectionId", javaType = Section.class,
                            one = @One(select = "net.thumbtack.school.notes.mappers.SectionMapper.getById",
                                    fetchType = FetchType.LAZY)),
                    @Result(property = "noteVersions", column = "id", javaType = List.class,
                            many = @Many(select = "net.thumbtack.school.notes.mappers.NoteMapper.getNoteVersionByNoteId",
                                    fetchType = FetchType.LAZY)),
                    @Result(property = "comments", column = "id", javaType = List.class,
                            many = @Many(select = "net.thumbtack.school.notes.mappers.CommentMapper.getCommentsByNoteId",
                                    fetchType = FetchType.LAZY))
            }
    )
    Note getNoteById(int noteId);

    @Select("SELECT revision_id AS revisionId, body, note_id FROM note_version WHERE note_id = #{noteId}")
    @Results(
            {
                    @Result(property = "note", column = "note_id", javaType = Note.class,
                            one = @One(select = "net.thumbtack.school.notes.mappers.NoteMapper.getNoteById",
                                    fetchType = FetchType.LAZY))
            })
    List<NoteVersion> getNoteVersionByNoteId(int noteId);

    @Update("UPDATE note SET  section_id = #{section.id} WHERE id=#{id}")
    void updateNote(Note note);

    @Delete("DELETE FROM note WHERE id= #{id}")
    int deleteNote(Note note);

    @Delete("DELETE FROM note")
    void clear();

    @Insert("INSERT INTO rating (user_id, note_id, number) VALUES (#{author.id}, #{note.id}, #{number}) "
            + "ON DUPLICATE KEY UPDATE number=#{number}")
    void insertRating(Rating rating);

    @Select("SELECT user_id AS authorId, note_id AS noteId, number FROM rating" +
            " WHERE user_id = #{userId} AND note_id = #{noteId}")
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
    Rating getRating(@Param("userId") int userId, @Param("noteId") int noteId);
}
