package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;

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

    boolean setSuperUser(int id);
}

