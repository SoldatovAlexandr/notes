package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.mappers.UserDtoMapper;
import net.thumbtack.school.notes.dto.request.*;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileItemDtoResponse;
import net.thumbtack.school.notes.dto.response.UpdateUserDtoResponse;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.model.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends BaseService {

    @Autowired
    public UserService(UserDao userDao, SectionDao sectionDao, NoteDao noteDao, CommentDao commentDao, Config config) {
        super(userDao, sectionDao, noteDao, commentDao, config);
    }

    public ProfileInfoDtoResponse registerUser(RegisterUserDtoRequest registerUserDtoRequest,
                                               HttpServletResponse httpServletResponse) throws ServerException {
        User user = UserDtoMapper.INSTANCE.toUser(registerUserDtoRequest);

        user.setType(UserType.USER);

        insertUser(user);

        addCookie(user, httpServletResponse);

        return UserDtoMapper.INSTANCE.toProfileInfoDtoResponse(user);
    }


    public EmptyDtoResponse loginUser(LoginUserRequest loginUserRequest, HttpServletResponse httpServletResponse)
            throws ServerException {
        User user = userDao.getByLogin(loginUserRequest.getLogin());

        if (user == null) {
            throw new ServerException(ServerErrorCodeWithField.LOGIN_NOT_EXIST);
        }

        if (user.isDeleted()) {
            throw new ServerException(ServerErrorCodeWithField.USER_IS_DELETED);
        }

        if (!loginUserRequest.getPassword().equals(user.getPassword())) {
            throw new ServerException(ServerErrorCodeWithField.INCORRECT_PASSWORD);
        }


        addCookie(user, httpServletResponse);

        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse logoutUser(String token) {

        userDao.deleteSessionByToken(token);

        return new EmptyDtoResponse();
    }

    public ProfileInfoDtoResponse getProfileInfo(String token) throws ServerException {

        Session session = getSession(token);

        return UserDtoMapper.INSTANCE.toProfileInfoDtoResponse(session.getUser());
    }


    public EmptyDtoResponse removeUser(PasswordDtoRequest passwordDtoRequest, String token) throws ServerException {

        Session session = getSession(token);

        User user = session.getUser();

        String password = passwordDtoRequest.getPassword();

        if (!user.getPassword().equals(password)) {
            throw new ServerException(ServerErrorCodeWithField.INCORRECT_PASSWORD);
        }

        userDao.setDeletedUser(user);

        deleteExistingSession(user);

        return new EmptyDtoResponse();
    }

    public UpdateUserDtoResponse updateUser(UpdateUserDtoRequest updateUserDtoRequest, String token)
            throws ServerException {

        Session session = getSession(token);

        String password = updateUserDtoRequest.getOldPassword();

        User user = session.getUser();

        if (!user.getPassword().equals(password)) {
            throw new ServerException(ServerErrorCodeWithField.INCORRECT_PASSWORD);
        }

        updateUserFields(updateUserDtoRequest, user);

        userDao.updateUser(user);

        return UserDtoMapper.INSTANCE.toUpdateUserDtoResponse(user);
    }

    public EmptyDtoResponse setSuperUser(String token, int id) throws ServerException {
        Session session = getSession(token);

        User author = session.getUser();

        if (author.getType() != UserType.SUPER_USER) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }

        User user = getUserById(id);

        user.setType(UserType.SUPER_USER);

        userDao.setUserType(user);

        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse following(FollowingDtoRequest followingDtoRequest, String token) throws ServerException {
        Session session = getSession(token);

        User follower = session.getUser();

        User following = getUserByLogin(followingDtoRequest.getLogin());

        insertFollowing(follower.getId(), following.getId());

        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse ignore(IgnoreDtoRequest ignoreDtoRequest, String token) throws ServerException {
        Session session = getSession(token);

        User ignoreBy = session.getUser();

        User ignore = getUserByLogin(ignoreDtoRequest.getLogin());

        insertIgnore(ignore.getId(), ignoreBy.getId());

        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse deleteFollowing(String login, String token) throws ServerException {
        Session session = getSession(token);

        User follower = session.getUser();

        User following = getUserByLogin(login);

        deleteFollowing(follower.getId(), following.getId());

        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse deleteIgnore(String login, String token) throws ServerException {
        Session session = getSession(token);

        User ignoreBy = session.getUser();

        User ignore = getUserByLogin(login);

        deleteIgnore(ignore.getId(), ignoreBy.getId());

        return new EmptyDtoResponse();
    }

    public List<? extends ProfileItemDtoResponse> getUsers(String sortByRating, String type, Integer from,
                                                           Integer count, String token) throws ServerException {
        Session session = getSession(token);

        User user = session.getUser();

        checkSortByRatingParam(sortByRating);

        LocalDateTime start = LocalDateTime.now().minusSeconds(config.getUserIdleTimeout());

        List<User> users = getUsers(user, sortByRating, from, count, start, type);

        if (user.getType().equals(UserType.SUPER_USER)) {
            return UserDtoMapper.INSTANCE.toSuperProfilesItemDtoResponse(users);
        }

        return UserDtoMapper.INSTANCE.toProfilesItemDtoResponse(users);
    }

    private List<User> getUsers(User user, String sortByRating, int from, int count, LocalDateTime start, String type)
            throws ServerException {
        switch (type) {
            case "highRating":
                return getHighRatingUsers(user, sortByRating, from, count, start);
            case "lowRating":
                return getLowRatingUsers(user, sortByRating, from, count, start);
            case "following":
                return getFollowings(user, sortByRating, from, count, start);
            case "followers":
                return getFollowers(user, sortByRating, from, count, start);
            case "ignore":
                return getIgnore(user, sortByRating, from, count, start);
            case "ignoreBy":
                return getIgnoredBy(user, sortByRating, from, count, start);
            case "deleted":
                return getDeleted(user, sortByRating, from, count, start);
            case "super":
                return getSuper(user, sortByRating, from, count, start);
            default:
                return getAllUsers(user, sortByRating, from, count, start);
        }
    }

    private List<User> getHighRatingUsers(User user, String sortByRatinggit, int from, int count, LocalDateTime start) {
        return userDao.getAllUsersWithSortASC(user, from, count, start);
    }

    private List<User> getLowRatingUsers(User user, String sortByRating, int from, int count, LocalDateTime start) {
        return userDao.getAllUsersWithSortDESC(user, from, count, start);
    }

    private List<User> getAllUsers(User user, String sortByRating, int from, int count, LocalDateTime start) {
        switch (sortByRating) {
            case "asc":
                return userDao.getAllUsersWithSortASC(user, from, count, start);
            case "desc":
                return userDao.getAllUsersWithSortDESC(user, from, count, start);
            default:
                return userDao.getAllUsers(user, from, count, start);
        }
    }

    private List<User> getSuper(User user, String sortByRating, int from, int count, LocalDateTime start)
            throws ServerException {
        if (!user.getType().equals(UserType.SUPER_USER)) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }

        switch (sortByRating) {
            case "asc":
                return userDao.getSuperUsersWithSortASC(user, from, count, start);
            case "desc":
                return userDao.getSuperUsersWithSortDESC(user, from, count, start);
            default:
                return userDao.getSuperUsers(user, from, count, start);
        }
    }

    private List<User> getDeleted(User user, String sortByRating, int from, int count, LocalDateTime start) {
        switch (sortByRating) {
            case "asc":
                return userDao.getDeletedUsersWithSortASC(user, from, count, start);
            case "desc":
                return userDao.getDeletedUsersWithSortDESC(user, from, count, start);
            default:
                return userDao.getDeletedUsers(user, from, count, start);
        }
    }

    private List<User> getIgnoredBy(User user, String sortByRating, int from, int count, LocalDateTime start) {
        switch (sortByRating) {
            case "asc":
                return userDao.getIgnoreByWithSortASC(user, from, count, start);
            case "desc":
                return userDao.getIgnoreByWithSortDESC(user, from, count, start);
            default:
                return userDao.getIgnoreBy(user, from, count, start);
        }
    }

    private List<User> getIgnore(User user, String sortByRating, int from, int count, LocalDateTime start) {
        switch (sortByRating) {
            case "asc":
                return userDao.getIgnoreWithSortASC(user, from, count, start);
            case "desc":
                return userDao.getIgnoreWithSortDESC(user, from, count, start);
            default:
                return userDao.getIgnore(user, from, count, start);
        }
    }

    private List<User> getFollowers(User user, String sortByRating, Integer from, Integer count, LocalDateTime start) {
        switch (sortByRating) {
            case "asc":
                return userDao.getFollowersWithSortASC(user, from, count, start);
            case "desc":
                return userDao.getFollowersWithSortDESC(user, from, count, start);
            default:
                return userDao.getFollowers(user, from, count, start);
        }
    }


    private List<User> getFollowings(User user, String sortByRating, Integer from, Integer count, LocalDateTime start) {
        switch (sortByRating) {
            case "asc":
                return userDao.getFollowingsWithSortASC(user, from, count, start);
            case "desc":
                return userDao.getFollowingsWithSortDESC(user, from, count, start);
            default:
                return userDao.getFollowings(user, from, count, start);
        }
    }

    private void checkSortByRatingParam(String sortByRating) throws ServerException {
        if (!(sortByRating.isEmpty() || sortByRating.equals("asc") || sortByRating.equals("desc"))) {
            throw new ServerException(ServerErrorCodeWithField.WRONG_SORT_BY_RATING);
        }
    }

    private void deleteIgnore(int ignoreId, int ignoreById) throws ServerException {
        if (userDao.deleteIgnore(ignoreId, ignoreById) == 0) {
            throw new ServerException(ServerErrorCodeWithField.NOT_IGNORING);
        }
    }

    private void deleteFollowing(int followerId, int followingId) throws ServerException {
        if (userDao.deleteFollowing(followerId, followingId) == 0) {
            throw new ServerException(ServerErrorCodeWithField.NOT_FOLLOWING);
        }
    }

    private void insertIgnore(int ignoreId, int ignoreById) throws ServerException {
        if (ignoreId == ignoreById) {
            throw new ServerException(ServerErrorCodeWithField.CAN_NOT_IGNORE);
        }

        try {
            userDao.deleteFollowing(ignoreId, ignoreById);
            userDao.insertIgnore(ignoreId, ignoreById);
        } catch (DuplicateKeyException e) {
            throw new ServerException(ServerErrorCodeWithField.IGNORE_ALREADY_EXIST);
        }
    }

    private User getUserByLogin(String login) throws ServerException {
        User user = userDao.getByLogin(login);

        if (user == null) {
            throw new ServerException(ServerErrorCodeWithField.LOGIN_NOT_EXIST);
        }
        return user;
    }


    private void insertUser(User user) throws ServerException {
        try {
            userDao.insert(user);
        } catch (DuplicateKeyException e) {
            throw new ServerException(ServerErrorCodeWithField.LOGIN_ALREADY_EXIST);
        }
    }

    private void insertFollowing(int followerId, int followingId) throws ServerException {
        if (followerId == followingId) {
            throw new ServerException(ServerErrorCodeWithField.CAN_NOT_SUBSCRIBE);
        }

        try {
            userDao.deleteIgnore(followerId, followingId);
            userDao.insertFollowing(followerId, followingId);
        } catch (DuplicateKeyException e) {
            throw new ServerException(ServerErrorCodeWithField.FOLLOWING_ALREADY_EXIST);
        }
    }

    private void addCookie(User user, HttpServletResponse httpServletResponse) {
        Cookie cookie = createCookie();

        insertSession(user, cookie);

        httpServletResponse.addCookie(cookie);
    }

    private Cookie createCookie() {
        return new Cookie(cookieName, UUID.randomUUID().toString());
    }

    private void insertSession(User user, Cookie cookie) {
        Session session = new Session(user, cookie.getValue(), getCurrentDateTime());

        userDao.insertSession(session);
    }

    private void deleteExistingSession(User user) {
        userDao.deleteSessionByUser(user);
    }

    private void updateUserFields(UpdateUserDtoRequest updateUserDtoRequest, User user) {
        User newUser = UserDtoMapper.INSTANCE.toUser(updateUserDtoRequest);

        user.setPassword(newUser.getPassword());

        user.setLastName(newUser.getLastName());

        user.setFirstName(newUser.getFirstName());

        user.setPatronymic(newUser.getPatronymic());
    }
}
