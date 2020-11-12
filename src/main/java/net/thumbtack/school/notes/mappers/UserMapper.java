package net.thumbtack.school.notes.mappers;

import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.model.UserType;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {

    @Insert("INSERT INTO user (first_name, last_name, patronymic, login, password, type) VALUES "
            + "(#{firstName}, #{lastName}, #{patronymic}, #{login}, #{password}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE user SET first_name=#{firstName}, last_name=#{lastName}," +
            " patronymic=#{patronymic}, password=#{password} WHERE id=#{id}")
    void updateUser(User user);

    @Select("SELECT id, first_name, last_name, patronymic, login, password" +
            " FROM user WHERE login = #{login}")
    User getByLogin(String login);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password" +
            " FROM user WHERE id = #{id}")
    User getById(int id);

    @Delete("DELETE FROM session WHERE token = #{token}")
    int deleteSessionByToken(String token);

    @Insert("INSERT INTO session (user_id, token, last_action) VALUES "
            + "(#{user.id}, #{token}, #{date})")
    void insertSession(Session session);

    @Delete("DELETE FROM session WHERE user_id = #{id}")
    int deleteSessionByUser(User user);

    @Select("SELECT user_id, token, last_action FROM session WHERE token = #{token}")
    @Results(
            {@Result(property = "user", column = "user_id", javaType = User.class,
                    one = @One(select = "net.thumbtack.school.notes.mappers.UserMapper.getById",
                            fetchType = FetchType.LAZY))}
    )
    Session getSessionByToken(String token);

    @Update("UPDATE user SET deleted = true WHERE id=#{id}")
    void deleteUser(User user);

    @Update("UPDATE user SET type = #{type} WHERE id=#{id} AND deleted=false")
    boolean setUserType(int id, UserType type);

    @Insert("INSERT INTO following (follower_id, following_id) VALUES "
            + "(#{followerId}, #{followingId})")
    void insertFollowing(@Param("followerId") int followerId, @Param("followingId") int followingId);

    @Insert("INSERT INTO ignoring (ignore_id, ignore_by_id) VALUES "
            + "(#{ignoreId}, #{ignoreById})")
    void insertIgnore(@Param("ignoreId") int ignoreId, @Param("ignoreById") int ignoreById);

    @Delete("DELETE FROM following WHERE  follower_id = #{followerId} AND following_id = #{followingId}")
    int deleteFollowing(@Param("followerId") int followerId, @Param("followingId") int followingId);

    @Delete("DELETE FROM ignoring WHERE  ignore_id= #{ignoreId} AND ignore_by_id = #{ignoreById}")
    int deleteIgnore(@Param("ignoreId") int ignoreId, @Param("ignoreById") int ignoreById);

    @Delete("DELETE FROM user")
    void clear();
}

