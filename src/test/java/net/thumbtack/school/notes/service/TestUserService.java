package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.request.*;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileInfoDtoResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TestUserService {

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

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Captor
    ArgumentCaptor<Cookie> cookieCaptor;

    @Captor
    ArgumentCaptor<Session> sessionCaptor;

    private final String cookieName = "JAVASESSIONID";

    @Test
    public void testRegisterUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        User user = new User("login", "password", "firstName", "lastName",
                "patronymic");

        RegisterUserDtoRequest request = new RegisterUserDtoRequest("firstName", "lastName",
                "patronymic", "login", "password");

        ProfileInfoDtoResponse expectedResponse = new ProfileInfoDtoResponse("firstName", "lastName",
                "patronymic", "login");

        ProfileInfoDtoResponse actualResponse = userService.registerUser(request, httpServletResponse);

        Assertions.assertAll(
                () -> verify(httpServletResponse).addCookie(cookieCaptor.capture()),
                () -> verify(userDao).insert(userCaptor.capture()),
                () -> verify(userDao).insertSession(sessionCaptor.capture()),
                () -> Assertions.assertEquals(expectedResponse, actualResponse),
                () -> Assertions.assertEquals(user, userCaptor.getValue()),
                () -> Assertions.assertEquals(cookieName, cookieCaptor.getValue().getName()),
                () -> Assertions.assertEquals(sessionCaptor.getValue().getToken(), cookieCaptor.getValue().getValue())
        );
    }

    @Test
    public void testRegisterUserFail() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        doThrow(DuplicateKeyException.class).when(userDao).insert(any());

        RegisterUserDtoRequest request = new RegisterUserDtoRequest("firstName", "lastName",
                "patronymic", "login", "password-123");

        Assertions.assertThrows(
                ServerException.class, () -> userService.registerUser(request, httpServletResponse)
        );
    }

    @Test
    public void testLoginUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        User user = new User("password", "firstName", "lastName", "patronymic");

        when(userDao.getByLogin("login")).thenReturn(user);

        LoginUserRequest request = new LoginUserRequest("login", "password");

        EmptyDtoResponse response = userService.loginUser(request, httpServletResponse);

        Assertions.assertAll(
                () -> verify(httpServletResponse).addCookie(cookieCaptor.capture()),
                () -> verify(userDao).insertSession(sessionCaptor.capture()),
                () -> Assertions.assertEquals(cookieName, cookieCaptor.getValue().getName()),
                () -> Assertions.assertEquals(sessionCaptor.getValue().getToken(), cookieCaptor.getValue().getValue())
        );

    }

    @Test
    public void testLoginUserFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        User user = new User("password", "firstName", "lastName", "patronymic");

        when(userDao.getByLogin("login")).thenReturn(user);

        LoginUserRequest request = new LoginUserRequest("login", "incorrectPassword");

        Assertions.assertThrows(
                ServerException.class, () -> userService.loginUser(request, httpServletResponse)
        );
    }

    @Test
    public void testLoginUserFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        when(userDao.getByLogin("login")).thenReturn(null);

        LoginUserRequest request = new LoginUserRequest("login", "password");

        Assertions.assertThrows(
                ServerException.class, () -> userService.loginUser(request, httpServletResponse)
        );
    }

    @Test
    public void testLoginUserFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);

        User user = new User("password", "firstName", "lastName", "patronymic");

        user.setDeleted(true);

        when(userDao.getByLogin("login")).thenReturn(user);

        LoginUserRequest request = new LoginUserRequest("login", "password");

        Assertions.assertThrows(
                ServerException.class, () -> userService.loginUser(request, httpServletResponse)
        );
    }

    @Test
    public void testLogout() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        EmptyDtoResponse response = userService.logoutUser(token);

        verify(userDao).deleteSessionByToken(token);
    }

    @Test
    public void testGetProfileInfo1() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        ProfileInfoDtoResponse expectedResponse = new ProfileInfoDtoResponse("firstName", "lastName",
                "patronymic", "login");

        Session session = Mockito.mock(Session.class);

        User user = new User("login", "password", "firstName", "lastName",
                "patronymic");

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        ProfileInfoDtoResponse response = userService.getProfileInfo(token);

        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testGetProfileInfo2() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        ProfileInfoDtoResponse expectedResponse = new ProfileInfoDtoResponse("firstName", "lastName",
                "patronymic", "login");

        Session session = Mockito.mock(Session.class);

        User user = new User("login", "password", "firstName", "lastName",
                "patronymic");

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now().minusSeconds(3599));

        ProfileInfoDtoResponse response = userService.getProfileInfo(token);

        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testGetProfileInfoFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "invalid-token";

        Assertions.assertThrows(
                ServerException.class, () -> userService.getProfileInfo(token)
        );
    }

    @Test
    public void testGetProfileInfoFail2() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        ProfileInfoDtoResponse expectedResponse = new ProfileInfoDtoResponse("firstName", "lastName",
                "patronymic", "login");

        Session session = Mockito.mock(Session.class);

        User user = new User("login", "password", "firstName", "lastName",
                "patronymic");

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now().minusSeconds(4000));

        Assertions.assertThrows(
                ServerException.class, () -> userService.getProfileInfo(token)
        );
    }

    @Test
    public void testGetProfileInfoFail3() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        ProfileInfoDtoResponse expectedResponse = new ProfileInfoDtoResponse("firstName", "lastName",
                "patronymic", "login");

        Session session = Mockito.mock(Session.class);

        User user = new User("login", "password", "firstName", "lastName",
                "patronymic");

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now().minusSeconds(3600));

        Assertions.assertThrows(
                ServerException.class, () -> userService.getProfileInfo(token)
        );
    }

    @Test
    public void testGetRemoveUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        PasswordDtoRequest passwordDtoRequest = new PasswordDtoRequest("password");

        Session session = Mockito.mock(Session.class);

        User user = new User("login", "password", "firstName", "lastName",
                "patronymic");

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = userService.removeUser(passwordDtoRequest, token);

        Assertions.assertAll(
                () -> verify(userDao).setDeletedUser(user),
                () -> verify(userDao).deleteSessionByUser(user)
        );
    }

    @Test
    public void testGetRemoveUserFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        PasswordDtoRequest passwordDtoRequest = new PasswordDtoRequest("invalid-password");

        Session session = Mockito.mock(Session.class);

        User user = new User("login", "password", "firstName", "lastName",
                "patronymic");

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.removeUser(passwordDtoRequest, token)
        );
    }

    @Test
    public void testGetRemoveUserFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        PasswordDtoRequest passwordDtoRequest = new PasswordDtoRequest("password");

        Assertions.assertThrows(
                ServerException.class, () -> userService.removeUser(passwordDtoRequest, token)
        );
    }

    @Test
    public void testUpdateUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        UpdateUserDtoResponse expectedResponse = new UpdateUserDtoResponse(0, "firstName",
                "lastName", "patronymic", "login");

        User user = new User("login", "oldPassword", "oldFirstName", "oldLastName",
                "oldPatronymic");

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        UpdateUserDtoRequest request = new UpdateUserDtoRequest("firstName", "lastName",
                "patronymic", "oldPassword", "newPassword");

        UpdateUserDtoResponse response = userService.updateUser(request, token);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(userDao).updateUser(user)
        );
    }

    @Test
    public void testUpdateUserFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User user = new User("login", "oldPassword", "oldFirstName", "oldLastName",
                "oldPatronymic");

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        UpdateUserDtoRequest request = new UpdateUserDtoRequest("firstName", "lastName",
                "patronymic", "invalidOldPassword", "newPassword");

        Assertions.assertThrows(
                ServerException.class, () -> userService.updateUser(request, token)
        );
    }


    @Test
    public void testUpdateUserFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        UpdateUserDtoRequest request = new UpdateUserDtoRequest("firstName", "lastName",
                "patronymic", "invalidOldPassword", "newPassword");

        Assertions.assertThrows(
                ServerException.class, () -> userService.updateUser(request, token)
        );
    }

    @Test
    public void testSetSuperUser() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        int userId = 100;

        Session session = Mockito.mock(Session.class);

        User author = new User("login1", "oldPassword", "oldFirstName", "oldLastName",
                "oldPatronymic");

        author.setType(UserType.SUPER_USER);

        User user = new User("login2", "oldPassword", "oldFirstName", "oldLastName",
                "oldPatronymic");

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        when(session.getUser()).thenReturn(author);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getById(userId)).thenReturn(user);

        EmptyDtoResponse response = userService.setSuperUser(token, userId);

        Assertions.assertAll(
                () -> verify(userDao).setUserType(user),
                () -> Assertions.assertEquals(UserType.SUPER_USER, user.getType())
        );
    }

    @Test
    public void testSetSuperUserFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        int userId = 100;

        Session session = Mockito.mock(Session.class);

        User author = new User("login1", "oldPassword", "oldFirstName", "oldLastName",
                "oldPatronymic");

        when(session.getUser()).thenReturn(author);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        when(userDao.getSessionByToken(token)).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> userService.setSuperUser(token, userId)
        );
    }

    @Test
    public void testSetSuperUserFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        int userId = 100;

        Assertions.assertThrows(
                ServerException.class, () -> userService.setSuperUser(token, userId)
        );
    }

    @Test
    public void testSetSuperUserFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        int userId = 100;

        Session session = Mockito.mock(Session.class);

        User author = new User("login1", "oldPassword", "oldFirstName", "oldLastName",
                "oldPatronymic");

        when(session.getUser()).thenReturn(author);

        author.setType(UserType.SUPER_USER);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        when(userDao.getSessionByToken(token)).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> userService.setSuperUser(token, userId)
        );
    }

    @Test
    public void testFollowing() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User follower = new User("follower", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User following = new User("following", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        follower.setId(10);

        following.setId(100);

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("following")).thenReturn(following);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        FollowingDtoRequest request = new FollowingDtoRequest("following");

        EmptyDtoResponse response = userService.following(request, token);

        Assertions.assertAll(
                () -> verify(userDao).insertFollowing(10, 100)
        );
    }

    @Test
    public void testFollowingFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        FollowingDtoRequest request = new FollowingDtoRequest("following");

        Assertions.assertThrows(
                ServerException.class, () -> userService.following(request, token)
        );
    }

    @Test
    public void testFollowingFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User follower = new User("follower", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        FollowingDtoRequest request = new FollowingDtoRequest("following");

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.following(request, token)
        );
    }

    @Test
    public void testFollowingFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User follower = new User("follower", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User following = new User("following", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("following")).thenReturn(following);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        FollowingDtoRequest request = new FollowingDtoRequest("following");

        Assertions.assertThrows(
                ServerException.class, () -> userService.following(request, token)
        );
    }

    @Test
    public void testFollowingFail4() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User follower = new User("follower", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User following = new User("following", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        follower.setId(10);
        following.setId(100);

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("following")).thenReturn(following);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        doThrow(DuplicateKeyException.class).when(userDao).insertFollowing(10, 100);

        FollowingDtoRequest request = new FollowingDtoRequest("following");

        Assertions.assertThrows(
                ServerException.class, () -> userService.following(request, token)
        );
    }

    @Test
    public void testIgnore() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User("ignoreBy", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User ignore = new User("ignore", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        ignoreBy.setId(10);
        ignore.setId(100);

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("ignore")).thenReturn(ignore);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        IgnoreDtoRequest request = new IgnoreDtoRequest("ignore");

        EmptyDtoResponse response = userService.ignore(request, token);

        Assertions.assertAll(
                () -> verify(userDao).insertIgnore(100, 10)
        );
    }

    @Test
    public void testIgnoreFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        IgnoreDtoRequest request = new IgnoreDtoRequest("ignore");

        Assertions.assertThrows(
                ServerException.class, () -> userService.ignore(request, token)
        );
    }

    @Test
    public void testIgnoreFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User("ignoreBy", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        IgnoreDtoRequest request = new IgnoreDtoRequest("ignore");

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.ignore(request, token)
        );
    }

    @Test
    public void testIgnoreFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User("ignoreBy", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User ignore = new User("ignore", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("ignore")).thenReturn(ignore);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        IgnoreDtoRequest request = new IgnoreDtoRequest("ignore");

        Assertions.assertThrows(
                ServerException.class, () -> userService.ignore(request, token)
        );
    }

    @Test
    public void testIgnoreFail4() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User("ignoreBy", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User ignore = new User("ignore", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        ignoreBy.setId(10);
        ignore.setId(100);

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("ignore")).thenReturn(ignore);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        doThrow(DuplicateKeyException.class).when(userDao).insertIgnore(100, 10);

        IgnoreDtoRequest request = new IgnoreDtoRequest("ignore");

        Assertions.assertThrows(
                ServerException.class, () -> userService.ignore(request, token)
        );
    }

    @Test
    public void testDeleteFollowing() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User follower = new User("follower", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User following = new User("following", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        follower.setId(10);
        following.setId(100);

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("following")).thenReturn(following);

        when(userDao.deleteFollowing(10, 100)).thenReturn(1);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = userService.deleteFollowing("following", token);

        Assertions.assertAll(
                () -> verify(userDao).deleteFollowing(10, 100)
        );
    }

    @Test
    public void testDeleteFollowingFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteFollowing("following", token)
        );
    }

    @Test
    public void testDeleteFollowingFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User follower = new User("follower", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteFollowing("following", token)
        );
    }

    @Test
    public void testDeleteFollowingFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User follower = new User("follower", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User following = new User("following", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        when(session.getUser()).thenReturn(follower);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("following")).thenReturn(following);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteFollowing("following", token)
        );
    }

    @Test
    public void testDeleteIgnore() throws ServerException {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User("ignoreBy", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User ignore = new User("ignore", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        ignoreBy.setId(10);
        ignore.setId(100);

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("ignore")).thenReturn(ignore);

        when(userDao.deleteIgnore(100, 10)).thenReturn(1);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = userService.deleteIgnore("ignore", token);

        Assertions.assertAll(
                () -> verify(userDao).deleteIgnore(100, 10)
        );
    }

    @Test
    public void testDeleteIgnoreFail1() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteIgnore("ignore", token)
        );
    }

    @Test
    public void testDeleteIgnoreFail2() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User("ignoreBy", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteIgnore("ignore", token)
        );
    }

    @Test
    public void testDeleteIgnoreFail3() {
        UserService userService = new UserService(userDao, sectionDao, noteDao, commentDao, config);

        String token = "some-token";

        Session session = Mockito.mock(Session.class);

        User ignoreBy = new User("ignoreBy", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");
        User ignore = new User("ignore", "oldPassword", "oldFirstName",
                "oldLastName", "oldPatronymic");

        when(session.getUser()).thenReturn(ignoreBy);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(userDao.getByLogin("ignore")).thenReturn(ignore);

        when(config.getUserIdleTimeout()).thenReturn(3600);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> userService.deleteIgnore("ignore", token)
        );
    }


}

