package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.model.UserType;

public interface UserDao {

    void insert(User user);

    void updateUser(User user);

    User getByLogin(String login);

    User getById(int id);

    void insertSession(Session session);

    Session getSessionByToken(String token);

    int deleteSessionByToken(String token);

    int deleteSessionByUser(User user);

    void setUserIsNotActive(User user);

    boolean setUserType(int id, UserType type);

    void insertFollowing(int followerId, int followingId);

    void insertIgnore(int ignoreId, int ignoreById);

    int deleteFollowing(int followerId, int followingId);

    int deleteIgnore(int ignoreId, int ignoreById);
}

