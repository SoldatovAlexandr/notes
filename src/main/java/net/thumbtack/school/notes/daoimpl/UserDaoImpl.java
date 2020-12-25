package net.thumbtack.school.notes.daoimpl;

import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.request.params.UserRequestType;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.mappers.UserMapper;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.views.UserView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
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
    public List<UserView> getUsers(
            User user, SortRequestType sortByRating, int from, int count, LocalDateTime start, UserRequestType type) {
        switch (type) {
            case HIGH_RATING:
                return getHighRatingUsers(sortByRating, from, count, start);
            case LOW_RATING:
                return getLowRatingUsers(sortByRating, from, count, start);
            case FOLLOWING:
                return getFollowings(user, sortByRating, from, count, start);
            case FOLLOWERS:
                return getFollowers(user, sortByRating, from, count, start);
            case IGNORE:
                return getIgnore(user, sortByRating, from, count, start);
            case IGNORE_BY:
                return getIgnoreBy(user, sortByRating, from, count, start);
            case DELETED:
                return getDeleted(user, sortByRating, from, count, start);
            case SUPER:
                return getSuper(user, sortByRating, from, count, start);
            default:
                return getAllUsers(sortByRating, from, count, start);
        }
    }

    private List<UserView> getLowRatingUsers(SortRequestType sortByRating, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get low rating users");
        List<UserView> userViews = userMapper.getAllUsersWithSortASC(from, count, start);

        if (sortByRating == SortRequestType.DESC) {
            Collections.reverse(userViews);
        }

        return userViews;
    }

    private List<UserView> getHighRatingUsers(SortRequestType sortByRating, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get high rating users");
        List<UserView> userViews = userMapper.getAllUsersWithSortDESC(from, count, start);

        if (sortByRating == SortRequestType.ASC) {
            Collections.reverse(userViews);
        }

        return userViews;
    }


    private List<UserView> getFollowings(User user, SortRequestType sortByRating, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get followings users by user: {}", user);
        switch (sortByRating) {
            case ASC:
                return userMapper.getFollowingsWithSortASC(user, from, count, start);
            case DESC:
                return userMapper.getFollowingsWithSortDESC(user, from, count, start);
            default:
                return userMapper.getFollowings(user, from, count, start);
        }
    }


    private List<UserView> getFollowers(User user, SortRequestType sortByRating, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get followers users by user: {}", user);
        switch (sortByRating) {
            case ASC:
                return userMapper.getFollowersWithSortASC(user, from, count, start);
            case DESC:
                return userMapper.getFollowersWithSortDESC(user, from, count, start);
            default:
                return userMapper.getFollowers(user, from, count, start);
        }
    }

    private List<UserView> getIgnoreBy(User user, SortRequestType sortByRating, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get ignoreBy users by user: {}", user);
        switch (sortByRating) {
            case ASC:
                return userMapper.getIgnoreByWithSortASC(user, from, count, start);
            case DESC:
                return userMapper.getIgnoreByWithSortDESC(user, from, count, start);
            default:
                return userMapper.getIgnoreBy(user, from, count, start);
        }
    }

    private List<UserView> getIgnore(User user, SortRequestType sortByRating, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get ignoreBy users by user: {}", user);
        switch (sortByRating) {
            case ASC:
                return userMapper.getIgnoreWithSortASC(user, from, count, start);
            case DESC:
                return userMapper.getIgnoreWithSortDESC(user, from, count, start);
            default:
                return userMapper.getIgnore(user, from, count, start);
        }
    }


    private List<UserView> getAllUsers(SortRequestType sortByRating, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get all users by");
        switch (sortByRating) {
            case ASC:
                return userMapper.getAllUsersWithSortASC(from, count, start);
            case DESC:
                return userMapper.getAllUsersWithSortDESC(from, count, start);
            default:
                return userMapper.getAllUsers(from, count, start);
        }
    }


    private List<UserView> getSuper(User user, SortRequestType sortByRating, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get super users by user: {}", user);
        switch (sortByRating) {
            case ASC:
                return userMapper.getSuperUsersWithSortASC(user, from, count, start);
            case DESC:
                return userMapper.getSuperUsersWithSortDESC(user, from, count, start);
            default:
                return userMapper.getSuperUsers(user, from, count, start);
        }
    }

    private List<UserView> getDeleted(User user, SortRequestType sortByRating, int from, int count, LocalDateTime start) {
        LOGGER.debug("DAO get deleted users by user: {}", user);
        switch (sortByRating) {
            case ASC:
                return userMapper.getDeletedUsersWithSortASC(user, from, count, start);
            case DESC:
                return userMapper.getDeletedUsersWithSortDESC(user, from, count, start);
            default:
                return userMapper.getDeletedUsers(user, from, count, start);
        }
    }
}
