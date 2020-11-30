package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDao {

    void insert(User user);

    void updateUser(User user);

    User getByLogin(String login);

    User getById(int id);

    void insertSession(Session session);

    void updateSession(Session session);

    Session getSessionByToken(String token);

    int deleteSessionByToken(String token);

    int deleteSessionByUser(User user);

    void setDeletedUser(User user);

    boolean setUserType(User user);

    void insertFollowing(int followerId, int followingId);

    void insertIgnore(int ignoreId, int ignoreById);

    int deleteFollowing(int followerId, int followingId);

    int deleteIgnore(int ignoreId, int ignoreById);

    List<User> getFollowings(User user, int from, int count, LocalDateTime start);

    List<User> getFollowingsWithSortASC(User user, int from, int count, LocalDateTime start);

    List<User> getFollowingsWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<User> getFollowersWithSortASC(User user, int from, int count, LocalDateTime start);

    List<User> getFollowersWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<User> getFollowers(User user, int from, int count, LocalDateTime start);

    List<User> getIgnoreByWithSortASC(User user, int from, int count, LocalDateTime start);

    List<User> getIgnoreByWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<User> getIgnoreBy(User user, int from, int count, LocalDateTime start);

    List<User> getIgnoreWithSortASC(User user, int from, int count, LocalDateTime start);

    List<User> getIgnoreWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<User> getIgnore(User user, int from, int count, LocalDateTime start);

    List<User> getAllUsersWithSortASC(User user, int from, int count, LocalDateTime start);

    List<User> getAllUsersWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<User> getAllUsers(User user, int from, int count, LocalDateTime start);

    List<User> getSuperUsersWithSortASC(User user, int from, int count, LocalDateTime start);

    List<User> getSuperUsersWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<User> getSuperUsers(User user, int from, int count, LocalDateTime start);

    List<User> getDeletedUsersWithSortASC(User user, int from, int count, LocalDateTime start);

    List<User> getDeletedUsersWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<User> getDeletedUsers(User user, int from, int count, LocalDateTime start);
}



