package net.thumbtack.school.notes.service;

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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TestSectionService {
    @MockBean
    private UserDao userDao;

    @MockBean
    private CommentDao commentDao;

    @MockBean
    private NoteDao noteDao;

    @MockBean
    private SectionDao sectionDao;

    @Test
    public void testCreateSection() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        String token = "some-token";
        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        SectionDtoRequest request = new SectionDtoRequest("section name");

        Section section = new Section(user, "section name");

        SectionDtoResponse expectedResponse = new SectionDtoResponse(0, "section name");

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        SectionDtoResponse response = sectionService.createSection(request, token);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(sectionDao).insert(section)
        );
    }

    @Test
    public void testCreateSectionFail1() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        SectionDtoRequest request = new SectionDtoRequest("section name");

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.createSection(request, "some-token")
        );
    }

    @Test
    public void testCreateSectionFail2() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        String token = "some-token";

        User user = Mockito.mock(User.class);

        SectionDtoRequest request = new SectionDtoRequest("section name");

        Section section = new Section(user, "section name");

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        doThrow(DuplicateKeyException.class).when(sectionDao).insert(section);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.createSection(request, token)
        );
    }

    @Test
    public void testRenameSection() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        String token = "some-token";

        User user = Mockito.mock(User.class);

        SectionDtoRequest request = new SectionDtoRequest("new section name");

        Section section = new Section(1, user, "section name");

        SectionDtoResponse expectedResponse = new SectionDtoResponse(1, "new section name");

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(sectionDao.getById(1)).thenReturn(section);

        SectionDtoResponse response = sectionService.renameSection(request, 1, token);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(sectionDao).update(any())
        );
    }

    @Test
    public void testRenameSectionFail1() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        SectionDtoRequest request = new SectionDtoRequest("section name");

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.renameSection(request, 1, "some-token")
        );
    }

    @Test
    public void testRenameSectionFail2() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        SectionDtoRequest request = new SectionDtoRequest("new section name");

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.renameSection(request, 1, "some-token")
        );
    }

    @Test
    public void testRenameSectionFail3() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        String token = "some-token";

        SectionDtoRequest request = new SectionDtoRequest("new section name");

        User author = Mockito.mock(User.class);

        Section section = new Section(1, author, "section name");

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(author.getId()).thenReturn(100);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(sectionDao.getById(1)).thenReturn(section);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.renameSection(request, 1, "some-token")
        );
    }

    @Test
    public void testRenameSectionFail4() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        String token = "some-token";

        User user = Mockito.mock(User.class);

        SectionDtoRequest request = new SectionDtoRequest("new section name");

        Section section = new Section(1, user, "section name");

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(sectionDao.getById(1)).thenReturn(section);

        doThrow(DuplicateKeyException.class).when(sectionDao).update(section);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.renameSection(request, 1, "some-token")
        );
    }

    @Test
    public void testRemoveSection1() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        String token = "some-token";

        User user = Mockito.mock(User.class);

        Section section = new Section(1, user, "section name");

        Session session = Mockito.mock(Session.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(10);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(sectionDao.getById(1)).thenReturn(section);

        EmptyDtoResponse response = sectionService.removeSection(1, token);

        Assertions.assertAll(
                () -> verify(sectionDao).deleteSection(section)
        );
    }

    @Test
    public void testRemoveSection2() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        String token = "some-token";

        User author = Mockito.mock(User.class);

        Section section = new Section(1, author, "section name");

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(22);

        when(author.getId()).thenReturn(100);

        when(user.getType()).thenReturn(UserType.SUPER_USER);

        when(userDao.getSessionByToken(token)).thenReturn(session);

        when(sectionDao.getById(1)).thenReturn(section);

        EmptyDtoResponse response = sectionService.removeSection(1, token);

        Assertions.assertAll(
                () -> verify(sectionDao).deleteSection(section)
        );
    }

    @Test
    public void testRemoveSectionFail1() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.removeSection(1, "some-token")
        );
    }

    @Test
    public void testRemoveSectionFail2() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.removeSection(1, "some-token")
        );
    }

    @Test
    public void testRemoveSectionFail3() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        User author = Mockito.mock(User.class);

        Section section = new Section(1, author, "section name");

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(author.getId()).thenReturn(100);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(22);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(sectionDao.getById(1)).thenReturn(section);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.removeSection(1, "some-token")
        );
    }

    @Test
    public void testGetSection() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);


        User author = Mockito.mock(User.class);

        Section section = new Section(1, author, "section name");

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(user.getId()).thenReturn(22);

        when(author.getId()).thenReturn(100);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(sectionDao.getById(1)).thenReturn(section);

        SectionDtoResponse expectedResponse = new SectionDtoResponse(1, "section name");

        SectionDtoResponse response = sectionService.getSection(1, "some-token");

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(sectionDao).getById(1)
        );
    }

    @Test
    public void testGetSectionFail1() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.getSection(1, "some-token")
        );
    }

    @Test
    public void testGetSectionFail2() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        Session session = Mockito.mock(Session.class);

        User user = Mockito.mock(User.class);

        when(session.getUser()).thenReturn(user);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.getSection(1, "some-token")
        );
    }

    @Test
    public void testGetSections() throws ServerException {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

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

        when(author.getId()).thenReturn(100);

        when(user.getId()).thenReturn(22);

        when(userDao.getSessionByToken("some-token")).thenReturn(session);

        when(sectionDao.getAllSections()).thenReturn(sections);

        List<SectionDtoResponse> response = sectionService.getSections("some-token");

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedResponse, response),
                () -> verify(sectionDao).getAllSections()
        );
    }

    @Test
    public void testGetSectionsFail() {
        SectionService sectionService = new SectionService(userDao, sectionDao, noteDao, commentDao);

        Assertions.assertThrows(
                ServerException.class, () -> sectionService.getSections("some-token")
        );
    }
}
