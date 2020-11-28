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
    public void updateSession(Session session) {
        LOGGER.debug("DAO update session: {}", session);
        userMapper.updateSession(session);
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
    public void setDeletedUser(User user) {
        LOGGER.debug("DAO set user : {} is not active", user);
        userMapper.deleteUser(user);
    }

    @Override
    public boolean setUserType(User user) {
        LOGGER.debug("DAO set user type for user:{}", user);
        return userMapper.setUserType(user);
    }

    @Override
    public void insertFollowing(int followerId, int followingId) {
        LOGGER.debug("DAO following user: {} on user: {}", followerId, followingId);
        userMapper.insertFollowing(followerId, followingId);
    }

    @Override
    public void insertIgnore(int ignoreId, int ignoreById) {
        LOGGER.debug("DAO ignore user: {} on user: {}", ignoreId, ignoreById);
        userMapper.insertIgnore(ignoreId, ignoreById);
    }

    @Override
    public int deleteFollowing(int followerId, int followingId) {
        LOGGER.debug("DAO delete following with follower: {}, following: {}", followerId, followingId);
        return userMapper.deleteFollowing(followerId, followingId);
    }

    @Override
    public int deleteIgnore(int ignoreId, int ignoreById) {
        LOGGER.debug("DAO delete ignore with ignore: {}, ignoreBy: {}", ignoreId, ignoreById);
        return userMapper.deleteIgnore(ignoreId, ignoreById);
    }
}
