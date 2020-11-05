package net.thumbtack.school.notes.daoimpl;

import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.mappers.UserMapper;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);
    private final UserMapper userMapper;

    @Autowired
    public UserDaoImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public void insert(User user) {
        LOGGER.debug("DAO insert user: {}", user);
        userMapper.insert(user);
    }

    @Override
    public void updateUser(User user) {
        LOGGER.debug("DAO update user: {}", user);
        userMapper.updateUser(user);
    }

    @Override
    public User getByLogin(String login) {
        LOGGER.debug("DAO select user by login: {}", login);
        return userMapper.getByLogin(login);
    }

    @Override
    public User getById(int id) {
        LOGGER.debug("DAO select user by id: {}", id);
        return userMapper.getById(id);
    }

    @Override
    public void insertSession(Session session) {
        LOGGER.debug("DAO insert session: {}", session);
        userMapper.insertSession(session);
    }

    @Override
    public Session getSessionByToken(String token) {
        LOGGER.debug("DAO get session by token: {}", token);
        return userMapper.getSessionByToken(token);
    }

    @Override
    public int deleteSessionByToken(String token) {
        LOGGER.debug("DAO delete session by token: {}", token);
        return userMapper.deleteSessionByToken(token);
    }

    @Override
    public int deleteSessionByUser(User user) {
        LOGGER.debug("DAO delete session by user: {}", user);
        return userMapper.deleteSessionByUser(user);
    }

    @Override
    public void setUserIsNotActive(User user) {
        LOGGER.debug("DAO set user : {} is not active", user);
        userMapper.setUserIsNotActive(user);
    }

    @Override
    public boolean setSuperUser(int id) {
        LOGGER.debug("DAO set user super where id is  {}", id);
        return userMapper.setSuperUser(id);
    }

}
