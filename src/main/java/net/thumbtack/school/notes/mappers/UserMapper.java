package net.thumbtack.school.notes.mappers;

import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface UserMapper {

    @Insert("INSERT INTO user (first_name, last_name, patronymic, login, password, type, registered) VALUES "
            + "(#{firstName}, #{lastName}, #{patronymic}, #{login}, #{password}, #{type}, #{registered})")
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
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowersById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "followings", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowingsById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignore", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignoredBy", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreByById",
                            fetchType = FetchType.LAZY))
    })
    User getByLogin(String login);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            "FROM user WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "followers", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowersById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "followings", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowingsById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignore", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignoredBy", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreByById",
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
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "followers", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowersById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "followings", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowingsById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignore", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignoredBy", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreByById",
                            fetchType = FetchType.LAZY))
    })
    List<User> getFollowersById(int userId);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            "FROM user INNER JOIN following ON id=following_id WHERE follower_id =#{userId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "followers", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowersById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "followings", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowingsById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignore", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignoredBy", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreByById",
                            fetchType = FetchType.LAZY))
    })
    List<User> getFollowingsById(int userId);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            "FROM user INNER JOIN ignoring ON id=ignore_id WHERE ignore_by_id =#{userId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "followers", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowersById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "followings", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowingsById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignore", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignoredBy", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreByById",
                            fetchType = FetchType.LAZY))
    })
    List<User> getIgnoreById(int userId);

    @Select("SELECT id, first_name AS firstName, last_name AS lastName, patronymic, login, password, type, deleted " +
            "FROM user INNER JOIN ignoring ON id=ignore_by_id WHERE ignore_id =#{userId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "followers", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowersById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "followings", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getFollowingsById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignore", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreById",
                            fetchType = FetchType.LAZY)),
            @Result(property = "ignoredBy", column = "id", javaType = List.class,
                    many = @Many(select = "net.thumbtack.school.notes.mappers.UserMapper.getIgnoreByById",
                            fetchType = FetchType.LAZY))
    })
    List<User> getIgnoreByById(int userId);

    @Update("UPDATE session SET last_action=#{date} WHERE token=#{token}")
    void updateSession(Session session);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT following_id FROM following WHERE follower_id=#{user.id}) " +
            "GROUP BY user.id LIMIT #{count} OFFSET #{from}")
    List<User> getFollowings(@Param("user") User user, @Param("from") Integer from,
                             @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT following_id FROM following WHERE follower_id=#{user.id}) " +
            "GROUP BY user.id ORDER BY userRating ASC LIMIT #{count} OFFSET #{from}")
    List<User> getFollowingsWithSortASC(@Param("user") User user, @Param("from") Integer from,
                                        @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT following_id FROM following WHERE follower_id=#{user.id}) " +
            "GROUP BY user.id ORDER BY userRating DESC LIMIT #{count} OFFSET #{from}")
    List<User> getFollowingsWithSortDESC(@Param("user") User user, @Param("from") Integer from,
                                         @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT follower_id FROM following WHERE following_id=#{user.id}) " +
            "GROUP BY user.id ORDER BY userRating ASC LIMIT #{count} OFFSET #{from}")
    List<User> getFollowersWithSortASC(@Param("user") User user, @Param("from") Integer from,
                                       @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT follower_id FROM following WHERE following_id=#{user.id}) " +
            "GROUP BY user.id ORDER BY userRating DESC LIMIT #{count} OFFSET #{from}")
    List<User> getFollowersWithSortDESC(@Param("user") User user, @Param("from") Integer from,
                                        @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT follower_id FROM following WHERE following_id=#{user.id}) " +
            "GROUP BY user.id LIMIT #{count} OFFSET #{from}")
    List<User> getFollowers(@Param("user") User user, @Param("from") Integer from,
                            @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT ignore_by_id FROM ignoring WHERE ignore_id=#{user.id}) " +
            "GROUP BY user.id ORDER BY userRating ASC LIMIT #{count} OFFSET #{from}")
    List<User> getIgnoreByWithSortASC(@Param("user") User user, @Param("from") Integer from,
                                      @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT ignore_by_id FROM ignoring WHERE ignore_id=#{user.id}) " +
            "GROUP BY user.id ORDER BY userRating DESC LIMIT #{count} OFFSET #{from}")
    List<User> getIgnoreByWithSortDESC(@Param("user") User user, @Param("from") Integer from,
                                       @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT ignore_by_id FROM ignoring WHERE ignore_id=#{user.id}) " +
            "GROUP BY user.id LIMIT #{count} OFFSET #{from}")
    List<User> getIgnoreBy(@Param("user") User user, @Param("from") Integer from,
                           @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT ignore_id FROM ignoring WHERE ignore_by_id=#{user.id}) " +
            "GROUP BY user.id ORDER BY userRating ASC LIMIT #{count} OFFSET #{from}")
    List<User> getIgnoreWithSortASC(@Param("user") User user, @Param("from") Integer from,
                                    @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT ignore_id FROM ignoring WHERE ignore_by_id=#{user.id}) " +
            "GROUP BY user.id ORDER BY userRating DESC LIMIT #{count} OFFSET #{from}")
    List<User> getIgnoreWithSortDESC(@Param("user") User user, @Param("from") Integer from,
                                     @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.id IN (SELECT ignore_id FROM ignoring WHERE ignore_by_id=#{user.id}) " +
            "GROUP BY user.id LIMIT #{count} OFFSET #{from}")
    List<User> getIgnore(@Param("user") User user, @Param("from") Integer from,
                         @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "GROUP BY user.id ORDER BY userRating ASC LIMIT #{count} OFFSET #{from}")
    List<User> getAllUsersWithSortASC(@Param("user") User user, @Param("from") Integer from,
                                      @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "GROUP BY user.id ORDER BY userRating DESC LIMIT #{count} OFFSET #{from}")
    List<User> getAllUsersWithSortDESC(@Param("user") User user, @Param("from") Integer from,
                                       @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "GROUP BY user.id  LIMIT #{count} OFFSET #{from}")
    List<User> getAllUsers(@Param("user") User user, @Param("from") Integer from,
                           @Param("count") Integer count, @Param("start") LocalDateTime start);


    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.type='SUPER_USER' " +
            "GROUP BY user.id ORDER BY userRating ASC LIMIT #{count} OFFSET #{from}")
    List<User> getSuperUsersWithSortASC(@Param("user") User user, @Param("from") Integer from,
                                        @Param("count") Integer count, @Param("start") LocalDateTime start);


    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.type='SUPER_USER' " +
            "GROUP BY user.id ORDER BY userRating DESC LIMIT #{count} OFFSET #{from}")
    List<User> getSuperUsersWithSortDESC(@Param("user") User user, @Param("from") Integer from,
                                         @Param("count") Integer count, @Param("start") LocalDateTime start);


    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.type='SUPER_USER' " +
            "GROUP BY user.id  LIMIT #{count} OFFSET #{from}")
    List<User> getSuperUsers(@Param("user") User user, @Param("from") Integer from,
                             @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.deleted = true " +
            "GROUP BY user.id ORDER BY userRating ASC LIMIT #{count} OFFSET #{from}")
    List<User> getDeletedUsersWithSortASC(@Param("user") User user, @Param("from") Integer from,
                                          @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.deleted = true " +
            "GROUP BY user.id ORDER BY userRating DESC LIMIT #{count} OFFSET #{from}")
    List<User> getDeletedUsersWithSortDESC(@Param("user") User user, @Param("from") Integer from,
                                           @Param("count") Integer count, @Param("start") LocalDateTime start);

    @Select("SELECT user.id AS id, user.login AS login, user.first_name AS firstName, user.last_name AS lastName, " +
            "user.patronymic AS patronymic, user.deleted AS deleted, user.registered AS registered, " +
            "user.type AS type, AVG(rating.number) AS userRating, " +
            "last_action between #{start} AND CURRENT_TIMESTAMP() AS online " +
            "FROM user " +
            "LEFT JOIN note ON note.user_id = user.id " +
            "LEFT JOIN rating ON note.id = rating.note_id " +
            "LEFT JOIN session ON user.id = session.user_id " +
            "WHERE user.deleted = true  " +
            "GROUP BY user.id  LIMIT #{count} OFFSET #{from}")
    List<User> getDeletedUsers(@Param("user") User user, @Param("from") Integer from,
                               @Param("count") Integer count, @Param("start") LocalDateTime start);
}

