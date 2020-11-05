package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.mappers.UserDtoMapper;
import net.thumbtack.school.notes.dto.request.LoginUserRequest;
import net.thumbtack.school.notes.dto.request.PasswordDtoRequest;
import net.thumbtack.school.notes.dto.request.RegisterUserDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateUserDtoRequest;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.UpdateUserDtoResponse;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class UserService extends BaseService {

    @Autowired
    public UserService(UserDao userDao) {
        super(userDao);
    }

    public ProfileInfoDtoResponse registerUser(RegisterUserDtoRequest registerUserDtoRequest,
                                               HttpServletResponse httpServletResponse) throws ServerException {
        User user = UserDtoMapper.INSTANCE.toUser(registerUserDtoRequest);

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

        userDao.setUserIsNotActive(user);

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

        User user = session.getUser();

        if (!user.isActive()) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }

        if (!userDao.setSuperUser(id)) {
            throw new ServerException(ServerErrorCodeWithField.WRONG_ID);
        }

        return new EmptyDtoResponse();
    }

    private void insertUser(User user) throws ServerException {
        try {
            userDao.insert(user);
        } catch (DuplicateKeyException e) {
            throw new ServerException(ServerErrorCodeWithField.LOGIN_ALREADY_EXIST);
        }
    }

    private void addCookie(User user, HttpServletResponse httpServletResponse) {
        Cookie cookie = createCookie();
        deleteExistingSession(user);
        insertSession(user, cookie);
        httpServletResponse.addCookie(cookie);
    }

    private Cookie createCookie() {
        return new Cookie(cookieName, UUID.randomUUID().toString());
    }

    private void insertSession(User user, Cookie cookie) {
        userDao.insertSession(new Session(user, cookie.getValue(), LocalDate.now()));
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
