package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.mappers.SectionDtoMapper;
import net.thumbtack.school.notes.dto.request.SectionDtoRequest;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.SectionDtoResponse;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.Section;
import net.thumbtack.school.notes.model.Session;
import net.thumbtack.school.notes.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService extends BaseService {

    @Autowired
    public SectionService(UserDao userDao, SectionDao sectionDao, NoteDao noteDao, CommentDao commentDao) {
        super(userDao, sectionDao, noteDao, commentDao);
    }

    public SectionDtoResponse createSection(SectionDtoRequest sectionDtoRequest, String token)
            throws ServerException {
        Session session = getSession(token);

        Section section = new Section(session.getUser(), sectionDtoRequest.getName());

        insertSection(section);

        return SectionDtoMapper.INSTANCE.toSectionDtoResponse(section);
    }

    public SectionDtoResponse renameSection(SectionDtoRequest sectionDtoRequest, int sectionId, String token)
            throws ServerException {
        Session session = getSession(token);

        Section section = getSection(sectionId);

        if (!isAuthor(section, session.getUser())) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }

        section.setName(sectionDtoRequest.getName());

        updateSection(section);

        return SectionDtoMapper.INSTANCE.toSectionDtoResponse(section);
    }


    public EmptyDtoResponse removeSection(int sectionId, String token) throws ServerException {
        Session session = getSession(token);

        Section section = getSection(sectionId);

        checkSectionPermission(section, session.getUser());

        sectionDao.deleteSection(section);

        return new EmptyDtoResponse();
    }

    public SectionDtoResponse getSection(int sectionId, String token) throws ServerException {
        Session session = getSession(token);

        Section section = getSection(sectionId);

        return SectionDtoMapper.INSTANCE.toSectionDtoResponse(section);
    }

    public List<SectionDtoResponse> getSections(String token) throws ServerException {
        Session session = getSession(token);

        List<Section> sections = sectionDao.getAllSections();

        return SectionDtoMapper.INSTANCE.toListSectionDtoResponse(sections);
    }


    private void insertSection(Section section) throws ServerException {
        try {
            sectionDao.insert(section);
        } catch (DuplicateKeyException e) {
            throw new ServerException(ServerErrorCodeWithField.SECTION_NAME_ALREADY_EXIST);
        }
    }

    private void updateSection(Section section) throws ServerException {
        try {
            sectionDao.update(section);
        } catch (DuplicateKeyException e) {
            throw new ServerException(ServerErrorCodeWithField.SECTION_NAME_ALREADY_EXIST);
        }
    }

    private void checkSectionPermission(Section section, User user) throws ServerException {
        if (!(isAuthor(section, user) || isSuper(user))) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }
    }
}
