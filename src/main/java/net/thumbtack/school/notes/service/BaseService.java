package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.Session;
import org.springframework.core.NestedRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = {NestedRuntimeException.class, ServerException.class})
public class BaseService {
    protected final UserDao userDao;

    protected final String cookieName = "JAVASESSIONID";

    public BaseService(UserDao userDao) {
        this.userDao = userDao;
    }

    protected Session getSession(String token) throws ServerException {
        Session session = userDao.getSessionByToken(token);

        if (session == null) {
            throw new ServerException(ServerErrorCodeWithField.UNAUTHORIZED_ACCESS);
        }

        return session;
    }
}
