package net.thumbtack.school.notes.mappers;

import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.Section;
import net.thumbtack.school.notes.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SectionMapper {
    @Insert("INSERT INTO section (user_id, name) VALUES "
            + "(#{author.id}, #{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSection(Section section);

    @Update("UPDATE section SET name=#{name} WHERE id=#{id}")
    void update(Section section);

    @Select("SELECT id, user_id AS userId, name FROM section WHERE id = #{id}")
    @Results(
            {
                    @Result(property = "author", column = "userId", javaType = User.class,
                            one = @One(select = "net.thumbtack.school.notes.mappers.UserMapper.getById",
                                    fetchType = FetchType.LAZY))
            }
    )
    Section getById(int id);

    @Delete("DELETE FROM section WHERE id = #{id}")
    int deleteSection(Section section);

    @Select("SELECT id, user_id AS userId, name FROM section")
    List<Section> getAllSections();

    @Delete("DELETE FROM section")
    void clear();
}
