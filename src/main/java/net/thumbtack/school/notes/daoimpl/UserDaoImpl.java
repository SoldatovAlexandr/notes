package net.thumbtack.school.notes.daoimpl;

import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.mappers.UserMapper;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.views.UserView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public List<UserView> getFollowings(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get followings users by user: {}", user);
        return userMapper.getFollowings(user, from, count, start);
    }

    @Override
    public List<UserView> getFollowingsWithSortASC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get followings users by user: {}, with sort by asc", user);
        return userMapper.getFollowingsWithSortASC(user, from, count, start);
    }

    @Override
    public List<UserView> getFollowingsWithSortDESC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get followings users by user: {}, with sort by desc", user);
        return userMapper.getFollowingsWithSortDESC(user, from, count, start);
    }

    @Override
    public List<UserView> getFollowersWithSortASC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get followers users by user: {}, with sort by asc", user);
        return userMapper.getFollowersWithSortASC(user, from, count, start);
    }

    @Override
    public List<UserView> getFollowersWithSortDESC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get followers users by user: {}, with sort by desc", user);
        return userMapper.getFollowersWithSortDESC(user, from, count, start);
    }

    @Override
    public List<UserView> getFollowers(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get followers users by user: {}", user);
        return userMapper.getFollowers(user, from, count, start);
    }

    @Override
    public List<UserView> getIgnoreByWithSortASC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get ignoreBy users by user: {}, with sort by asc", user);
        return userMapper.getIgnoreByWithSortASC(user, from, count, start);
    }

    @Override
    public List<UserView> getIgnoreByWithSortDESC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get ignoreBy users by user: {}, with sort by desc", user);
        return userMapper.getIgnoreByWithSortDESC(user, from, count, start);
    }

    @Override
    public List<UserView> getIgnoreBy(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get ignoreBy users by user: {}", user);
        return userMapper.getIgnoreBy(user, from, count, start);
    }

    @Override
    public List<UserView> getIgnoreWithSortASC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get ignoreBy users by user: {}, with sort by asc", user);
        return userMapper.getIgnoreWithSortASC(user, from, count, start);
    }

    @Override
    public List<UserView> getIgnoreWithSortDESC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get ignoreBy users by user: {}, with sort by desc", user);
        return userMapper.getIgnoreWithSortDESC(user, from, count, start);
    }

    @Override
    public List<UserView> getIgnore(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get ignoreBy users by user: {}", user);
        return userMapper.getIgnore(user, from, count, start);
    }

    @Override
    public List<UserView> getAllUsersWithSortASC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get all users by user: {}, with sort by asc", user);
        return userMapper.getAllUsersWithSortASC(user, from, count, start);
    }

    @Override
    public List<UserView> getAllUsersWithSortDESC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get all users by user: {}, with sort by desc", user);
        return userMapper.getAllUsersWithSortDESC(user, from, count, start);
    }

    @Override
    public List<UserView> getAllUsers(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get all users by user: {}", user);
        return userMapper.getAllUsers(user, from, count, start);
    }

    @Override
    public List<UserView> getSuperUsersWithSortASC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get super users by user: {}, with sort by asc", user);
        return userMapper.getSuperUsersWithSortASC(user, from, count, start);
    }

    @Override
    public List<UserView> getSuperUsersWithSortDESC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get super users by user: {}, with sort by desc", user);
        return userMapper.getSuperUsersWithSortDESC(user, from, count, start);
    }

    @Override
    public List<UserView> getSuperUsers(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get super users by user: {}", user);
        return userMapper.getSuperUsers(user, from, count, start);
    }

    @Override
    public List<UserView> getDeletedUsersWithSortASC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get deleted users by user: {}, with sort by asc", user);
        return userMapper.getDeletedUsersWithSortASC(user, from, count, start);
    }

    @Override
    public List<UserView> getDeletedUsersWithSortDESC(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get deleted users by user: {}, with sort by desc", user);
        return userMapper.getDeletedUsersWithSortDESC(user, from, count, start);
    }

    @Override
    public List<UserView> getDeletedUsers(User user, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get deleted users by user: {}", user);
        return userMapper.getDeletedUsers(user, from, count, start);
    }
}
