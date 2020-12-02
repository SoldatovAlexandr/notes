package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.views.UserView;

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

    List<UserView> getFollowings(User user, int from, int count, LocalDateTime start);

    List<UserView> getFollowingsWithSortASC(User user, int from, int count, LocalDateTime start);

    List<UserView> getFollowingsWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<UserView> getFollowersWithSortASC(User user, int from, int count, LocalDateTime start);

    List<UserView> getFollowersWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<UserView> getFollowers(User user, int from, int count, LocalDateTime start);

    List<UserView> getIgnoreByWithSortASC(User user, int from, int count, LocalDateTime start);

    List<UserView> getIgnoreByWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<UserView> getIgnoreBy(User user, int from, int count, LocalDateTime start);

    List<UserView> getIgnoreWithSortASC(User user, int from, int count, LocalDateTime start);

    List<UserView> getIgnoreWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<UserView> getIgnore(User user, int from, int count, LocalDateTime start);

    List<UserView> getAllUsersWithSortASC(User user, int from, int count, LocalDateTime start);

    List<UserView> getAllUsersWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<UserView> getAllUsers(User user, int from, int count, LocalDateTime start);

    List<UserView> getSuperUsersWithSortASC(User user, int from, int count, LocalDateTime start);

    List<UserView> getSuperUsersWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<UserView> getSuperUsers(User user, int from, int count, LocalDateTime start);

    List<UserView> getDeletedUsersWithSortASC(User user, int from, int count, LocalDateTime start);

    List<UserView> getDeletedUsersWithSortDESC(User user, int from, int count, LocalDateTime start);

    List<UserView> getDeletedUsers(User user, int from, int count, LocalDateTime start);
}



