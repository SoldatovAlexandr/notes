package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.mappers.UserDtoMapper;
import net.thumbtack.school.notes.dto.request.*;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.dto.request.params.UserRequestType;
import net.thumbtack.school.notes.dto.response.*;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.model.UserType;
import net.thumbtack.school.notes.views.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends ServiceBase {

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

    public List<? extends ProfileItemDtoResponse> getUsers(SortRequestType sortByRating, UserRequestType type, Integer from,
                                                           Integer count, String token) throws ServerException {
        Session session = getSession(token);

        User user = session.getUser();

        LocalDateTime start = LocalDateTime.now().minusSeconds(config.getUserIdleTimeout());

        if (type == UserRequestType.SUPER && user.getType() != UserType.SUPER_USER) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }

        List<UserView> users = userDao.getUsers(user, sortByRating, from, count, start, type);

        if (user.getType().equals(UserType.SUPER_USER)) {
            return UserDtoMapper.INSTANCE.toSuperProfilesItemDtoResponse(users);
        }

        return UserDtoMapper.INSTANCE.toProfilesItemDtoResponse(users);
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
        return new Cookie(COOKIE_NAME, UUID.randomUUID().toString());
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