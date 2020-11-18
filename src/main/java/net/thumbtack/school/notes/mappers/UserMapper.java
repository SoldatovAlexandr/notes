package net.thumbtack.school.notes.mappers;

import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserMapper {

    @Insert("INSERT INTO user (first_name, last_name, patronymic, login, password, type) VALUES "
            + "(#{firstName}, #{lastName}, #{patronymic}, #{login}, #{password}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE user SET first_name=#{firstName}, last_name=#{lastName}," +
            " patronymic=#{patronymic}, password=#{password} WHERE id=#{id}")
    void updateUser(User user);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            " FROM user WHERE login = #{login}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "followers", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowers",
                            fetchType = FetchType.LAZY)),
            @Result(property = "followings", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowings",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignore", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnore",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignoredBy", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreBy",
                            fetchType = FetchType.LAZY))
    })
    User getByLogin(String login);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            "FROM user WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "followers", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowers",
                            fetchType = FetchType.LAZY)),
            @Result(property = "followings", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowings",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignore", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnore",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignoredBy", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreBy",
                            fetchType = FetchType.LAZY))
    })
    User getById(int id);

    @Delete("DELETE FROM session WHERE token = #{token}")
    int deleteSessionByToken(String token);

    @Insert("INSERT INTO session (user_id, token, last_action) VALUES (#{user.id}, #{token}, #{date}) " +
            " ON DUPLICATE KEY UPDATE token=#{token}, last_action=#{date}")
    void insertSession(Session session);

    @Delete("DELETE FROM session WHERE user_id = #{id}")
    int deleteSessionByUser(User user);

    @Select("SELECT user_id, token, last_action AS date FROM session WHERE token = #{token}")
    @Results({
            @Result(property = "user", column = "user_id", javaType = User.class,
                    one = @One(select = "net.thumbtack.school.notes.mappers.UserMapper.getById",
                            fetchType = FetchType.LAZY))}
    )
    Session getSessionByToken(String token);

    @Update("UPDATE user SET deleted = true WHERE id=#{id}")
    void deleteUser(User user);

    @Update("UPDATE user SET type = #{type} WHERE id=#{id}")
    boolean setUserType(User user);

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

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            "FROM user INNER JOIN following ON id=follower_id WHERE following_id =#{userId}")
    List<User> getFollowers(int userId);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            "FROM user INNER JOIN following ON id=following_id WHERE follower_id =#{userId}")
    List<User> getFollowings(int userId);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            "FROM user INNER JOIN ignoring ON id=ignore_id WHERE ignore_by_id =#{userId}")
    List<User> getIgnore(int userId);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            "FROM user INNER JOIN ignoring ON id=ignore_by_id WHERE ignore_id =#{userId}")
    List<User> getIgnoreBy(int userId);
}

