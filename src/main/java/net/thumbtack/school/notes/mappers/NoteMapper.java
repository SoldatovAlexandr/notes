package net.thumbtack.school.notes.mappers;

import net.thumbtack.school.notes.model.*;
import net.thumbtack.school.notes.views.CommentView;
import net.thumbtack.school.notes.views.NoteView;
import net.thumbtack.school.notes.views.RevisionView;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface NoteMapper {
    @Insert("INSERT INTO note (user_id, section_id, created, subject) VALUES "
            + "(#{author.id}, #{section.id}, #{created}, #{subject})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertNote(Note note);

    @Insert("INSERT INTO note_version (note_id, revision_id, body, created) VALUES "
            + "(#{note.id}, #{revisionId}, #{body}, NOW())")
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
                    @Result(property = "id", column = "id"),
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

    @Select("SELECT revision_id AS id, body, created, note_id FROM note_version WHERE note_id = #{noteId}")
    @Results(
            {
                    @Result(property = "id", column = "id"),
                    @Result(property = "comments", column = "noteId=note_id, revisionId=id", javaType = List.class,
                            many = @Many(select = "net.thumbtack.school.notes.mappers.NoteMapper.getCommentViews",
                                    fetchType = FetchType.EAGER))
            }
    )
    List<RevisionView> getRevisionViews(@Param("noteId") int id);

    @Select("SELECT id, body, created, revision_id AS revisionId, user_id AS authorId " +
            "FROM comment " +
            "WHERE note_id = #{noteId} AND revision_id= #{revisionId}")
    List<CommentView> getCommentViews(@Param("noteId") int noteId,
                                      @Param("revisionId") int revisionId);


    @Select("<script> " +
            "SELECT note.id AS id, note.user_id AS authorId, section_id AS sectionId, subject, " +
            "note.created AS created, AVG(rating.number) AS rating , note_version.body AS body " +
            "FROM note " +
            "LEFT JOIN note_version ON note.id = note_version.note_id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "<if test = 'needUser eq false'>" +
            "<if test = 'onlyIgnore eq true'> RIGHT JOIN ignoring ON ignoring.ignore_id = note.user_id AND ignoring.ignore_by_id = #{profileId} </if>" +
            "<if test = 'notIgnore eq true'> LEFT JOIN ignoring ON ignoring.ignore_id = note.user_id  </if> " +
            "<if test = 'onlyFollowing eq true'> RIGHT JOIN following ON following.following_id = note.user_id  AND following.follower_id = #{profileId} </if> " +
            "</if>" +
            "<where> " +
            "note.created BETWEEN #{timeFrom} AND #{timeTo} " +
            "AND note_version.body = (SELECT body FROM note_version WHERE note_version.note_id = id AND note_version.revision_id =( SELECT MAX(revision_id) FROM note_version WHERE note_version.note_id = id)) "+
            "<if test =' needSection eq true ' > AND note.section_id= #{sectionId} </if> " +
            "<if test = ' needUser eq true' > AND note.user_id = #{userId} </if> " +
            "AND note_version.body IN (SELECT body FROM note_version WHERE revision_id = " +
            "(SELECT MAX(revision_id) FROM note_version WHERE note_id = id GROUP BY note_id)) " +
            "<if test = 'hasTags eq true '> " +
            "<if test = 'allTags eq false' > " +
            "AND note_version.body IN (SELECT body FROM note_version WHERE MATCH(body) AGAINST ( #{tags})) </if>" +
            "<if test = 'allTags eq true' > " +
            "AND note_version.body IN (SELECT body FROM note_version WHERE MATCH(body) AGAINST ( #{tags} IN BOOLEAN MODE)) </if>" +
            "</if> " +
            "<if test = 'notIgnore eq true'> AND note.user_id NOT IN (SELECT ignoring.ignore_id FROM ignoring WHERE ignoring.ignore_by_id = #{profileId} )  </if> " +
            "</where>" +
            "GROUP BY id " +
            "<if test= 'sort eq true'> ORDER BY rating " +
            "<if test= 'asc eq true'> ASC </if>" +
            "<if test= 'asc eq false'> DESC </if>" +
            "</if> " +
            "LIMIT #{count} OFFSET #{from} " +
            "</script>"
    )

    @Results(
            {
                    @Result(property = "id", column = "id"),
                    @Result(property = "revisions", column = "id", javaType = List.class,
                            many = @Many(select = "net.thumbtack.school.notes.mappers.NoteMapper.getRevisionViews",
                                    fetchType = FetchType.LAZY))
            }
    )
    List<NoteView> getNotes(@Param("sectionId") Integer sectionId,
                            @Param("tags") String tags,
                            @Param("allTags") boolean allTags,
                            @Param("timeFrom") LocalDateTime timeFrom,
                            @Param("timeTo") LocalDateTime timeTo,
                            @Param("userId") Integer userId,
                            @Param("from") Integer from,
                            @Param("count") Integer count,
                            @Param("profileId") Integer profileId,
                            @Param("hasTags") boolean hasTags,
                            @Param("sort") boolean sort,
                            @Param("asc") boolean asc,
                            @Param("needUser") boolean needUser,
                            @Param("needSection") boolean needSection,
                            @Param("onlyIgnore") boolean onlyIgnore,
                            @Param("notIgnore") boolean notIgnore,
                            @Param("onlyFollowing") boolean onlyFollowing);
}
