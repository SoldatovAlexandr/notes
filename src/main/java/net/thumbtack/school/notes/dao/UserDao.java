package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;

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
}

