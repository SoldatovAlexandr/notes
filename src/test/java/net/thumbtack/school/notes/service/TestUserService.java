package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.request.*;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.dto.request.params.UserRequestType;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileItemDtoResponse;
import net.thumbtack.school.notes.dto.response.UpdateUserDtoResponse;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.model.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TestUserService {

    private static final int IDLE_TIMEOUT = 3600;
    private static final int USER_ID = 10;
    private static final int ANOTHER_USER_ID = 100;

    private final static String COOKIE_NAME = "JAVASESSIONID";
    private final static String LOGIN = "login";
    private final static String ANOTHER_LOGIN = "anotherLogin";
    private final static String FIRST_NAME = "firstName";
    private final static String OLD_FIRST_NAME = "oldFirstName";
    private final static String ANOTHER_FIRST_NAME = "anotherFirstName";
    private final static String LAST_NAME = "lastName";
    private final static String OLD_LAST_NAME = "oldLastName";
    private final static String ANOTHER_LAST_NAME = "anotherLastName";
    private final static String PASSWORD = "password";
    private final static String OLD_PASSWORD = "oldPassword";
    private final static String INCORRECT_PASSWORD = "incorrectPassword";
    private final static String PATRONYMIC = "patronymic";
    private final static String OLD_PATRONYMIC = "oldPatronymic";
    private final static String ANOTHER_PATRONYMIC = "anotherPatronymic";
    private static final String TOKEN = "some-token";
    @Captor
    ArgumentCaptor<User> userCaptor;
    @Captor
    ArgumentCaptor<Cookie> cookieCaptor;
    @Captor
    ArgumentCaptor<Session> sessionCaptor;
    @MockBean
    private UserDao userDao;
    @MockBean
    private CommentDao commentDao;
    @MockBean
    private NoteDao noteDao;
    @MockBean
    private SectionDao sectionDao;
    @MockBean
    private Config config;

    @Test
    public void testRegisterUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        User user = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        RegisterUserDtoRequest request = new RegisterUserDtoRequest(FIRST_NAME, LAST_NAME, PATRONYMIC, LOGIN, PASSWORD);

        ProfileInfoDtoResponse expectedResponse = new ProfileInfoDtoResponse(FIRST_NAME, LAST_NAME, PATRONYMIC, LOGIN);

        ProfileInfoDtoResponse actualResponse = userService.registerUser(request, httpServletResponse);

        Assertions.assertAll(
                () -> verify(httpServletResponse).addCookie(cookieCaptor.capture()),
                () -> verify(userDao).insert(userCaptor.capture()),
                () -> verify(userDao).insertSession(sessionCaptor.capture()),
                () -> Assertions.assertEquals(expectedResponse, actualResponse),
                () -> Assertions.assertEquals(user, userCaptor.getValue()),
                () -> Assertions.assertEquals(COOKIE_NAME, cookieCaptor.getValue().getName()),
                () -> Assertions.assertEquals(sessionCaptor.getValue().getToken(), cookieCaptor.getValue().getValue())
        );
    }

    @Test
    public void testRegisterUserFail() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        doThrow(DuplicateKeyException.class).when(userDao).insert(any());

        RegisterUserDtoRequest request = new RegisterUserDtoRequest(FIRST_NAME, LAST_NAME, PATRONYMIC, LOGIN, PASSWORD);

        Assertions.assertThrows(
                ServerException.class, () -> userService.registerUser(request, httpServletResponse)
        );
    }

    @Test
    public void testLoginUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        User user = new User(PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC);

        when(userDao.getByLogin(LOGIN)).thenReturn(user);

        LoginUserRequest request = new LoginUserRequest(LOGIN, PASSWORD);

        EmptyDtoResponse response = userService.loginUser(request, httpServletResponse);

        Assertions.assertAll(
                () -> verify(httpServletResponse).addCookie(cookieCaptor.capture()),
                () -> verify(userDao).insertSession(sessionCaptor.capture()),
                () -> Assertions.assertEquals(COOKIE_NAME, cookieCaptor.getValue().getName()),
                () -> Assertions.assertEquals(sessionCaptor.getValue().getToken(), cookieCaptor.getValue().getValue())
        );

    }

    @Test
    public void testLoginUserFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        User user = new User(PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC);

        when(userDao.getByLogin(LOGIN)).thenReturn(user);

        LoginUserRequest request = new LoginUserRequest(LOGIN, INCORRECT_PASSWORD);

        Assertions.assertThrows(
                ServerException.class, () -> userService.loginUser(request, httpServletResponse)
        );
    }

    @Test
    public void testLoginUserFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        when(userDao.getByLogin(LOGIN)).thenReturn(null);

        LoginUserRequest request = new LoginUserRequest(LOGIN, PASSWORD);

        Assertions.assertThrows(
                ServerException.class, () -> userService.loginUser(request, httpServletResponse)
        );
    }

    @Test
    public void testLoginUserFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        User user = new User(PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC);

        user.setDeleted(true);

        when(userDao.getByLogin(LOGIN)).thenReturn(user);

        LoginUserRequest request = new LoginUserRequest(LOGIN, PASSWORD);

        Assertions.assertThrows(
                ServerException.class, () -> userService.loginUser(request, httpServletResponse)
        );
    }

    @Test
    public void testLogout() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        EmptyDtoResponse response = userService.logoutUser(TOKEN);

        verify(userDao).deleteSessionByToken(TOKEN);
    }

    @Test
    public void testGetProfileInfo1() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        ProfileInfoDtoResponse expectedResponse = new ProfileInfoDtoResponse(FIRST_NAME, LAST_NAME, PATRONYMIC, LOGIN);

        Session session = Mockito.mock(Session.class);

        User user = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        ProfileInfoDtoResponse response = userService.getProfileInfo(TOKEN);

        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testGetProfileInfo2() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        ProfileInfoDtoResponse expectedResponse = new ProfileInfoDtoResponse(FIRST_NAME, LAST_NAME, PATRONYMIC, LOGIN);

        Session session = Mockito.mock(Session.class);

        User user = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now().minusSeconds(IDLE_TIMEOUT - 1));

        ProfileInfoDtoResponse response = userService.getProfileInfo(TOKEN);

        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testGetProfileInfoFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> userService.getProfileInfo(TOKEN)
        );
    }

    @Test
    public void testGetProfileInfoFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now().minusSeconds(IDLE_TIMEOUT + 400));

        Assertions.assertThrows(
                ServerException.class, () -> userService.getProfileInfo(TOKEN)
        );
    }

    @Test
    public void testGetProfileInfoFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now().minusSeconds(IDLE_TIMEOUT));

        Assertions.assertThrows(
                ServerException.class, () -> userService.getProfileInfo(TOKEN)
        );
    }

    @Test
    public void testGetRemoveUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        PasswordDtoRequest passwordDtoRequest = new PasswordDtoRequest(PASSWORD);

        Session session = Mockito.mock(Session.class);

        User user = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = userService.removeUser(passwordDtoRequest, TOKEN);

        Assertions.assertAll(
                () -> verify(userDao).setDeletedUser(user),
                () -> verify(userDao).deleteSessionByUser(user)
        );
    }

    @Test
    public void testGetRemoveUserFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        PasswordDtoRequest passwordDtoRequest = new PasswordDtoRequest(INCORRECT_PASSWORD);

        Session session = Mockito.mock(Session.class);

        User user = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.removeUser(passwordDtoRequest, TOKEN)
        );
    }

    @Test
    public void testGetRemoveUserFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        PasswordDtoRequest passwordDtoRequest = new PasswordDtoRequest(PASSWORD);

        Assertions.assertThrows(
                ServerException.class, () -> userService.removeUser(passwordDtoRequest, TOKEN)
        );
    }

    @Test
    public void testUpdateUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        UpdateUserDtoResponse expectedResponse = new UpdateUserDtoResponse(0, FIRST_NAME, LAST_NAME, PATRONYMIC, LOGIN);

        User user = new User(LOGIN, OLD_PASSWORD, OLD_FIRST_NAME, OLD_LAST_NAME, OLD_PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        UpdateUserDtoRequest request = new UpdateUserDtoRequest(FIRST_NAME, LAST_NAME, PATRONYMIC, OLD_PASSWORD, PASSWORD);

        UpdateUserDtoResponse response = userService.updateUser(request, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(userDao).updateUser(user)
        );
    }

    @Test
    public void testUpdateUserFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = new User(LOGIN, OLD_PASSWORD, OLD_FIRST_NAME, OLD_LAST_NAME, OLD_PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        UpdateUserDtoRequest request = new UpdateUserDtoRequest(FIRST_NAME, LAST_NAME, PATRONYMIC, INCORRECT_PASSWORD,
                PASSWORD);

        Assertions.assertThrows(
                ServerException.class, () -> userService.updateUser(request, TOKEN)
        );
    }


    @Test
    public void testUpdateUserFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        UpdateUserDtoRequest request = new UpdateUserDtoRequest(FIRST_NAME, LAST_NAME, PATRONYMIC, OLD_PASSWORD, PASSWORD);

        Assertions.assertThrows(
                ServerException.class, () -> userService.updateUser(request, TOKEN)
        );
    }

    @Test
    public void testSetSuperUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User author = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        author.setType(UserType.SUPER_USER);

        User user = new User(ANOTHER_LOGIN, PASSWORD, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PATRONYMIC,
                LocalDateTime.now());

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        when(session.getUser()).thenReturn(author);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(userDao.getById(USER_ID)).thenReturn(user);

        EmptyDtoResponse response = userService.setSuperUser(TOKEN, USER_ID);

        Assertions.assertAll(
                () -> verify(userDao).setUserType(user),
                () -> Assertions.assertEquals(UserType.SUPER_USER, user.getType())
        );
    }

    @Test
    public void testSetSuperUserFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User author = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(author);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> userService.setSuperUser(TOKEN, USER_ID)
        );
    }

    @Test
    public void testSetSuperUserFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> userService.setSuperUser(TOKEN, USER_ID)
        );
    }

    @Test
    public void testSetSuperUserFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User author = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(author);

        author.setType(UserType.SUPER_USER);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> userService.setSuperUser(TOKEN, USER_ID)
        );
    }

    @Test
    public void testFollowing() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User follower = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());
        User following = new User(ANOTHER_LOGIN, PASSWORD, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PATRONYMIC,
                LocalDateTime.now());

        follower.setId(USER_ID);

        following.setId(ANOTHER_USER_ID);

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(userDao.getByLogin(ANOTHER_LOGIN)).thenReturn(following);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        FollowingDtoRequest request = new FollowingDtoRequest(ANOTHER_LOGIN);

        EmptyDtoResponse response = userService.following(request, TOKEN);

        Assertions.assertAll(
                () -> verify(userDao).insertFollowing(USER_ID, ANOTHER_USER_ID)
        );
    }

    @Test
    public void testFollowingFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        FollowingDtoRequest request = new FollowingDtoRequest(LOGIN);

        Assertions.assertThrows(
                ServerException.class, () -> userService.following(request, TOKEN)
        );
    }

    @Test
    public void testFollowingFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User follower = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        FollowingDtoRequest request = new FollowingDtoRequest(ANOTHER_LOGIN);

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.following(request, TOKEN)
        );
    }

    @Test
    public void testFollowingFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User follower = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());
        User following = new User(ANOTHER_LOGIN, PASSWORD, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PATRONYMIC,
                LocalDateTime.now());

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(userDao.getByLogin(ANOTHER_LOGIN)).thenReturn(following);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        FollowingDtoRequest request = new FollowingDtoRequest(ANOTHER_LOGIN);

        Assertions.assertThrows(
                ServerException.class, () -> userService.following(request, TOKEN)
        );
    }

    @Test
    public void testFollowingFail4() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User follower = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());
        User following = new User(ANOTHER_LOGIN, PASSWORD, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PATRONYMIC,
                LocalDateTime.now());

        follower.setId(USER_ID);
        following.setId(ANOTHER_USER_ID);

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(userDao.getByLogin(ANOTHER_LOGIN)).thenReturn(following);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        doThrow(DuplicateKeyException.class).when(userDao).insertFollowing(USER_ID, ANOTHER_USER_ID);

        FollowingDtoRequest request = new FollowingDtoRequest(ANOTHER_LOGIN);

        Assertions.assertThrows(
                ServerException.class, () -> userService.following(request, TOKEN)
        );
    }

    @Test
    public void testIgnore() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());
        User ignore = new User(ANOTHER_LOGIN, PASSWORD, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PATRONYMIC,
                LocalDateTime.now());

        ignoreBy.setId(USER_ID);
        ignore.setId(ANOTHER_USER_ID);

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(userDao.getByLogin(ANOTHER_LOGIN)).thenReturn(ignore);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        IgnoreDtoRequest request = new IgnoreDtoRequest(ANOTHER_LOGIN);

        EmptyDtoResponse response = userService.ignore(request, TOKEN);

        Assertions.assertAll(
                () -> verify(userDao).insertIgnore(ANOTHER_USER_ID, USER_ID)
        );
    }

    @Test
    public void testIgnoreFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        IgnoreDtoRequest request = new IgnoreDtoRequest(LOGIN);

        Assertions.assertThrows(
                ServerException.class, () -> userService.ignore(request, TOKEN)
        );
    }

    @Test
    public void testIgnoreFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        IgnoreDtoRequest request = new IgnoreDtoRequest(LOGIN);

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.ignore(request, TOKEN)
        );
    }

    @Test
    public void testIgnoreFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());
        User ignore = new User(ANOTHER_LOGIN, PASSWORD, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PATRONYMIC,
                LocalDateTime.now());

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(userDao.getByLogin(ANOTHER_LOGIN)).thenReturn(ignore);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        IgnoreDtoRequest request = new IgnoreDtoRequest(ANOTHER_LOGIN);

        Assertions.assertThrows(
                ServerException.class, () -> userService.ignore(request, TOKEN)
        );
    }

    @Test
    public void testIgnoreFail4() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());
        User ignore = new User(ANOTHER_LOGIN, PASSWORD, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PATRONYMIC,
                LocalDateTime.now());

        ignoreBy.setId(USER_ID);
        ignore.setId(ANOTHER_USER_ID);

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(userDao.getByLogin(ANOTHER_LOGIN)).thenReturn(ignore);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        doThrow(DuplicateKeyException.class).when(userDao).insertIgnore(ANOTHER_USER_ID, USER_ID);

        IgnoreDtoRequest request = new IgnoreDtoRequest(ANOTHER_LOGIN);

        Assertions.assertThrows(
                ServerException.class, () -> userService.ignore(request, TOKEN)
        );
    }

    @Test
    public void testDeleteFollowing() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User follower = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());
        User following = new User(ANOTHER_LOGIN, PASSWORD, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PATRONYMIC,
                LocalDateTime.now());

        follower.setId(USER_ID);
        following.setId(ANOTHER_USER_ID);

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(userDao.getByLogin(ANOTHER_LOGIN)).thenReturn(following);

        when(userDao.deleteFollowing(USER_ID, ANOTHER_USER_ID)).thenReturn(1);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = userService.deleteFollowing(ANOTHER_LOGIN, TOKEN);

        Assertions.assertAll(
                () -> verify(userDao).deleteFollowing(USER_ID, ANOTHER_USER_ID)
        );
    }

    @Test
    public void testDeleteFollowingFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteFollowing(LOGIN, TOKEN)
        );
    }

    @Test
    public void testDeleteFollowingFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User follower = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteFollowing(ANOTHER_LOGIN, TOKEN)
        );
    }


    @Test
    public void testDeleteIgnore() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());
        User ignore = new User(ANOTHER_LOGIN, PASSWORD, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PATRONYMIC,
                LocalDateTime.now());

        ignoreBy.setId(USER_ID);
        ignore.setId(ANOTHER_USER_ID);

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(userDao.getByLogin(ANOTHER_LOGIN)).thenReturn(ignore);

        when(userDao.deleteIgnore(ANOTHER_USER_ID, USER_ID)).thenReturn(1);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = userService.deleteIgnore(ANOTHER_LOGIN, TOKEN);

        Assertions.assertAll(
                () -> verify(userDao).deleteIgnore(ANOTHER_USER_ID, USER_ID)
        );
    }

    @Test
    public void testDeleteIgnoreFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteIgnore(LOGIN, TOKEN)
        );
    }

    @Test
    public void testDeleteIgnoreFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteIgnore(ANOTHER_LOGIN, TOKEN)
        );
    }

    @Test
    public void testGetUsers() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        LocalDateTime dateTime = LocalDateTime.now();

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(session.getUser()).thenReturn(user);

        when(session.getDate()).thenReturn(dateTime);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(userDao.getUsers(user, SortRequestType.WITHOUT, 0, 10, dateTime, UserRequestType.ALL_USERS))
                .thenReturn(List.of());

        List<? extends ProfileItemDtoResponse> users = userService.getUsers(SortRequestType.WITHOUT,
                UserRequestType.ALL_USERS, 0, 10, TOKEN);

        List<? extends ProfileItemDtoResponse> expectedUsers = List.of();

        Assertions.assertEquals(expectedUsers, users);
    }

    @Test
    public void testGetUsersFail() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = new User(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, PATRONYMIC, LocalDateTime.now());

        LocalDateTime dateTime = LocalDateTime.now();

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(session.getUser()).thenReturn(user);

        when(session.getDate()).thenReturn(dateTime);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(userDao.getUsers(user, SortRequestType.WITHOUT, 0, 10, dateTime, UserRequestType.SUPER))
                .thenReturn(List.of());

        Assertions.assertThrows(
                ServerException.class, () -> userService.getUsers(SortRequestType.WITHOUT, UserRequestType.SUPER,
                        0, 10, TOKEN)
        );
    }

}

