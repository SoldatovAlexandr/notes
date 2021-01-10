package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.dto.request.params.UserRequestType;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.model.UserType;
import net.thumbtack.school.notes.views.UserView;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestUserDao extends TestBaseDao {

    @Test
    public void testInsertAndGetUserByLogin() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        User userFromDb = userDao.getByLogin(user.getLogin());

        Assertions.assertEquals(user, userFromDb);
    }

    @Test
    public void testInsertAndGetUserById() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        User userFromDb = userDao.getById(user.getId());

        Assertions.assertEquals(user, userFromDb);
    }

    @Test
    public void testUpdateAndGetUserById() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        user.setFirstName("newFirstName");
        user.setLastName("newLastName");
        user.setPatronymic("newPatronymic");
        user.setPassword("newPassword");

        userDao.updateUser(user);

        User userFromDb = userDao.getById(user.getId());

        Assertions.assertEquals(user, userFromDb);
    }

    @Test
    public void testSetDeletedUser() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        userDao.setDeletedUser(user);

        User userFromDb = userDao.getById(user.getId());

        Assertions.assertTrue(userFromDb.isDeleted());
    }

    @Test
    public void testSetUserType() {
        User user = new User("login", "password",
                "firstName", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        user.setType(UserType.SUPER_USER);

        userDao.setUserType(user);

        User userFromDb = userDao.getById(user.getId());

        Assertions.assertEquals(UserType.SUPER_USER, userFromDb.getType());
    }


    @Test
    public void testInsertAndGetSession() {
        User user = new User("login", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        String token = UUID.randomUUID().toString();

        Session session = new Session(user, token, getCurrentDateTime());

        userDao.insertSession(session);

        Session sessionFromDb = userDao.getSessionByToken(token);

        Assertions.assertEquals(session, sessionFromDb);
    }

    @Test
    public void testInsertAndUpdateSession() {
        User user = new User("login", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        String token = UUID.randomUUID().toString();

        LocalDateTime secondDateTime = getCurrentDateTime();

        LocalDateTime firstDataTime = secondDateTime.minusSeconds(3000);

        Session session = new Session(user, token, firstDataTime);

        userDao.insertSession(session);

        Session firstSessionFromDb = userDao.getSessionByToken(token);

        session.setDate(secondDateTime);

        userDao.updateSession(session);

        Session secondSessionFromDb = userDao.getSessionByToken(token);

        Assertions.assertAll(
                () -> Assertions.assertEquals(firstDataTime, firstSessionFromDb.getDate()),
                () -> Assertions.assertEquals(secondDateTime, secondSessionFromDb.getDate())
        );
    }

    @Test
    public void testInsertAndDeleteSessionByToken() {
        User user = new User("login", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        String token = UUID.randomUUID().toString();

        Session session = new Session(user, token, getCurrentDateTime());

        userDao.insertSession(session);

        userDao.deleteSessionByToken(token);

        Session sessionFromDb = userDao.getSessionByToken(token);

        Assertions.assertNull(sessionFromDb);
    }

    @Test
    public void testInsertAndDeleteSessionByUser() {
        User user = new User("login", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user);

        String token = UUID.randomUUID().toString();

        Session session = new Session(user, token, getCurrentDateTime());

        userDao.insertSession(session);

        userDao.deleteSessionByUser(user);

        Session sessionFromDb = userDao.getSessionByToken(token);

        Assertions.assertNull(sessionFromDb);
    }

    @Ignore
    @Test
    public void testInsertFollowing() {
        User user1 = new User("login1", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        User user2 = new User("login2", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user1);
        userDao.insert(user2);

        userDao.insertFollowing(user1.getId(), user2.getId());

        User userFromDb1 = userDao.getById(user1.getId());
        User userFromDb2 = userDao.getByLogin(user2.getLogin());

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, userFromDb1.getFollowings().size()),
                () -> Assertions.assertEquals(1, userFromDb2.getFollowers().size()),
                () -> Assertions.assertEquals(user2, userFromDb1.getFollowings().get(0)),
                () -> Assertions.assertEquals(user1, userFromDb2.getFollowers().get(0))
        );
    }

    @Test
    public void testInsertIgnore() {
        User user1 = new User("login1", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        User user2 = new User("login2", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user1);
        userDao.insert(user2);

        userDao.insertIgnore(user1.getId(), user2.getId());

        User userFromDb1 = userDao.getById(user1.getId());
        User userFromDb2 = userDao.getByLogin(user2.getLogin());

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, userFromDb1.getIgnoredBy().size()),
                () -> Assertions.assertEquals(1, userFromDb2.getIgnore().size()),
                () -> Assertions.assertEquals(user2, userFromDb1.getIgnoredBy().get(0)),
                () -> Assertions.assertEquals(user1, userFromDb2.getIgnore().get(0))
        );
    }

    @Test
    public void testDeleteFollowing() {
        User user1 = new User("login1", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        User user2 = new User("login2", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user1);
        userDao.insert(user2);

        userDao.insertFollowing(user1.getId(), user2.getId());

        userDao.deleteFollowing(user1.getId(), user2.getId());

        User userFromDb1 = userDao.getById(user1.getId());
        User userFromDb2 = userDao.getByLogin(user2.getLogin());

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, userFromDb1.getFollowings().size()),
                () -> Assertions.assertEquals(0, userFromDb2.getFollowers().size())
        );
    }

    @Test
    public void testDeleteIgnore() {
        User user1 = new User("login1", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        User user2 = new User("login2", "password",
                "first", "lastName", "patronymic", getCurrentDateTime());
        userDao.insert(user1);
        userDao.insert(user2);

        userDao.insertIgnore(user1.getId(), user2.getId());

        userDao.deleteIgnore(user1.getId(), user2.getId());

        User userFromDb1 = userDao.getById(user1.getId());
        User userFromDb2 = userDao.getByLogin(user2.getLogin());

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, userFromDb1.getIgnoredBy().size()),
                () -> Assertions.assertEquals(0, userFromDb2.getIgnore().size())
        );
    }

    @Test
    public void testGetUsers() {
        LocalDateTime time = getCurrentDateTime();
        User user1 = new User("login1", "password",
                "first", "lastName", "patronymic", time);
        User user2 = new User("login2", "password",
                "first", "lastName", "patronymic", time);
        User user3 = new User("login3", "password",
                "first", "lastName", "patronymic", time);

        userDao.insert(user1);
        userDao.insert(user2);
        userDao.insert(user3);

        List<UserView> expectedUsers = List.of(
                new UserView(user1.getId(), user1.getFirstName(), user1.getLastName(), user1.getPatronymic(),
                        user1.getLogin(), user1.isDeleted(),
                        false, 0, false, time),
                new UserView(user2.getId(), user2.getFirstName(), user2.getLastName(), user2.getPatronymic(),
                        user2.getLogin(), user2.isDeleted(),
                        false, 0, false, time),
                new UserView(user3.getId(), user3.getFirstName(), user3.getLastName(), user3.getPatronymic(),
                        user3.getLogin(), user3.isDeleted(),
                        false, 0, false, time));

        List<UserView> users = userDao.getUsers(user1, SortRequestType.WITHOUT, 0, 3,
                LocalDateTime.now().minusSeconds(60), UserRequestType.ALL_USERS);

        Assertions.assertEquals(expectedUsers, users);
    }
}
