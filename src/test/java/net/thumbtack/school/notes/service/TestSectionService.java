package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.request.SectionDtoRequest;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.SectionDtoResponse;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.Section;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.model.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TestSectionService {

    private static final int NEW_SECTION_ID = 0;
    private static final int USER_ID = 10;
    private static final int ANOTHER_USER_ID = 44;
    private static final int SECTION_ID = 1;
    private static final int IDLE_TIMEOUT = 3600;

    private static final String SECTION_NAME = "section name";
    private static final String NEW_SECTION_NAME = "new section name";
    private static final String TOKEN = "some-token";

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
    public void testCreateSection() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        SectionDtoRequest request = new SectionDtoRequest(SECTION_NAME);

        Section section = new Section(user, SECTION_NAME);

        SectionDtoResponse expectedResponse = new SectionDtoResponse(NEW_SECTION_ID, SECTION_NAME);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        SectionDtoResponse response = sectionService.createSection(request, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(sectionDao).insert(section)
        );
    }

    @Test
    public void testCreateSectionFail1() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        SectionDtoRequest request = new SectionDtoRequest(SECTION_NAME);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.createSection(request, TOKEN)
        );
    }

    @Test
    public void testCreateSectionFail2() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        User user = Mockito.mock(User.class);

        SectionDtoRequest request = new SectionDtoRequest(SECTION_NAME);

        Section section = new Section(user, SECTION_NAME);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        doThrow(DuplicateKeyException.class).when(sectionDao).insert(section);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.createSection(request, TOKEN)
        );
    }

    @Test
    public void testRenameSection() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        User user = Mockito.mock(User.class);

        SectionDtoRequest request = new SectionDtoRequest(NEW_SECTION_NAME);

        Section section = new Section(SECTION_ID, user, SECTION_NAME);

        SectionDtoResponse expectedResponse = new SectionDtoResponse(SECTION_ID, NEW_SECTION_NAME);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        SectionDtoResponse response = sectionService.renameSection(request, SECTION_ID, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(sectionDao).update(any())
        );
    }

    @Test
    public void testRenameSectionFail1() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        SectionDtoRequest request = new SectionDtoRequest(SECTION_NAME);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.renameSection(request, SECTION_ID, TOKEN)
        );
    }

    @Test
    public void testRenameSectionFail2() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        SectionDtoRequest request = new SectionDtoRequest(NEW_SECTION_NAME);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.renameSection(request, SECTION_ID, TOKEN)
        );
    }

    @Test
    public void testRenameSectionFail3() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        SectionDtoRequest request = new SectionDtoRequest(NEW_SECTION_NAME);

        User author = Mockito.mock(User.class);

        Section section = new Section(SECTION_ID, author, SECTION_NAME);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(author.getId()).thenReturn(ANOTHER_USER_ID);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.renameSection(request, SECTION_ID, TOKEN)
        );
    }

    @Test
    public void testRenameSectionFail4() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        User user = Mockito.mock(User.class);

        SectionDtoRequest request = new SectionDtoRequest(NEW_SECTION_NAME);

        Section section = new Section(SECTION_ID, user, SECTION_NAME);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        doThrow(DuplicateKeyException.class).when(sectionDao).update(section);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.renameSection(request, SECTION_ID, TOKEN)
        );
    }

    @Test
    public void testRemoveSection1() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        User user = Mockito.mock(User.class);

        Section section = new Section(SECTION_ID, user, SECTION_NAME);

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(sectionDao.getById(1)).thenReturn(section);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = sectionService.removeSection(SECTION_ID, TOKEN);

        Assertions.assertAll(
                () -> verify(sectionDao).deleteSection(section)
        );
    }

    @Test
    public void testRemoveSection2() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        User author = Mockito.mock(User.class);

        Section section = new Section(SECTION_ID, author, SECTION_NAME);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(author.getId()).thenReturn(ANOTHER_USER_ID);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        EmptyDtoResponse response = sectionService.removeSection(SECTION_ID, TOKEN);

        Assertions.assertAll(
                () -> verify(sectionDao).deleteSection(section)
        );
    }

    @Test
    public void testRemoveSectionFail1() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.removeSection(SECTION_ID, TOKEN)
        );
    }

    @Test
    public void testRemoveSectionFail2() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.removeSection(SECTION_ID, TOKEN)
        );
    }

    @Test
    public void testRemoveSectionFail3() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        User author = Mockito.mock(User.class);

        Section section = new Section(SECTION_ID, author, SECTION_NAME);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(author.getId()).thenReturn(ANOTHER_USER_ID);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.removeSection(SECTION_ID, TOKEN)
        );
    }

    @Test
    public void testGetSection() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        User author = Mockito.mock(User.class);

        Section section = new Section(SECTION_ID, author, SECTION_NAME);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(USER_ID);

        when(author.getId()).thenReturn(ANOTHER_USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(sectionDao.getById(SECTION_ID)).thenReturn(section);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        SectionDtoResponse expectedResponse = new SectionDtoResponse(SECTION_ID, SECTION_NAME);

        SectionDtoResponse response = sectionService.getSection(SECTION_ID, TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(sectionDao).getById(SECTION_ID)
        );
    }

    @Test
    public void testGetSectionFail1() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.getSection(SECTION_ID, TOKEN)
        );
    }

    @Test
    public void testGetSectionFail2() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.getSection(SECTION_ID, TOKEN)
        );
    }

    @Test
    public void testGetSections() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        List<Section> sections = new ArrayList<>();

        User author = Mockito.mock(User.class);

        sections.add(new Section(1, author, "section name 1"));
        sections.add(new Section(2, author, "section name 2"));
        sections.add(new Section(3, author, "section name 3"));

        List<SectionDtoResponse> expectedResponse = new ArrayList<>();
        expectedResponse.add(new SectionDtoResponse(1, "section name 1"));
        expectedResponse.add(new SectionDtoResponse(2, "section name 2"));
        expectedResponse.add(new SectionDtoResponse(3, "section name 3"));

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(author.getId()).thenReturn(ANOTHER_USER_ID);

        when(user.getId()).thenReturn(USER_ID);

        when(userDao.getSessionByToken(TOKEN)).thenReturn(session);

        when(sectionDao.getAllSections()).thenReturn(sections);

        when(config.getUserIdleTimeout()).thenReturn(IDLE_TIMEOUT);

        when(session.getDate()).thenReturn(LocalDateTime.now());

        List<SectionDtoResponse> response = sectionService.getSections(TOKEN);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(sectionDao).getAllSections()
        );
    }

    @Test
    public void testGetSectionsFail() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao, config);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.getSections(TOKEN)
        );
    }
}
