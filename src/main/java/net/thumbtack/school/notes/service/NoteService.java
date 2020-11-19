package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.mappers.NoteDtoMapper;
import net.thumbtack.school.notes.dto.request.AddRatingDtoRequest;
import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateNoteDtoRequest;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.NoteInfoDtoResponse;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteService extends BaseService {
    private static final int START_REVISION_ID = 1;
    private static final int INCREMENT_REVISION_ID = 1;

    @Autowired
    public NoteService(UserDao userDao, SectionDao sectionDao, NoteDao noteDao, CommentDao commentDao) {
        super(userDao, sectionDao, noteDao, commentDao);
    }


    public NoteInfoDtoResponse createNote(CreateNoteDtoRequest createNoteDtoRequest, String token)
            throws ServerException {
        Session session = getSession(token);

        Note note = NoteDtoMapper.INSTANCE.toNote(createNoteDtoRequest);

        Section section = getSection(note.getSectionId());

        note.setAuthorId(session.getUser().getId());

        note.setCreated(getCurrentDateTime());

        note.getNoteVersion().setRevisionId(START_REVISION_ID);

        insertNote(note);

        return NoteDtoMapper.INSTANCE.toNoteDtoResponse(note);
    }

    public NoteInfoDtoResponse getNoteInfo(int noteId, String token) throws ServerException {
        Session session = getSession(token);

        Note note = getNote(noteId);

        return NoteDtoMapper.INSTANCE.toNoteDtoResponse(note);
    }


    public NoteInfoDtoResponse updateNote(UpdateNoteDtoRequest updateNoteDtoRequest, int noteId, String token)
            throws ServerException {
        Session session = getSession(token);

        Note note = getNote(noteId);

        if (!isAuthor(note, session.getUser())) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }

        String body = updateNoteDtoRequest.getBody();

        if (body != null) {
            addNoteVersion(body, note);
        }

        Integer sectionId = updateNoteDtoRequest.getSectionId();

        if (sectionId != null) {
            moveNoteToSection(note, sectionId);
        }

        return NoteDtoMapper.INSTANCE.toNoteDtoResponse(note);
    }

    public EmptyDtoResponse deleteNote(int noteId, String token) throws ServerException {
        Session session = getSession(token);

        Note note = getNote(noteId);

        checkNotePermission(note, session.getUser());

        noteDao.deleteNote(note);

        return new EmptyDtoResponse();
    }

    public EmptyDtoResponse addRating(AddRatingDtoRequest addRatingDtoRequest, int noteId, String token)
            throws ServerException {
        Session session = getSession(token);

        int userId = session.getUser().getId();

        Note note = getNote(noteId);

        if (note.getAuthorId() == userId) {
            throw new ServerException(ServerErrorCodeWithField.CAN_NOT_RATE);
        }

        Rating rating = new Rating(userId, noteId, addRatingDtoRequest.getRating());

        noteDao.insertRating(rating);

        return new EmptyDtoResponse();
    }

    private void checkNotePermission(Note note, User user) throws ServerException {
        if (!(isAuthor(note, user) || isSuper(user))) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }
    }

    private void addNoteVersion(String body, Note note) {
        NoteVersion noteVersion = new NoteVersion(body);

        int newRevisionId = note.getNoteVersion().getRevisionId() + INCREMENT_REVISION_ID;

        noteVersion.setRevisionId(newRevisionId);

        insertNoteVersion(noteVersion, note.getId());

        note.setNoteVersion(noteVersion);
    }

    private void moveNoteToSection(Note note, int sectionId) throws ServerException {
        Section section = getSection(sectionId);

        note.setSectionId(sectionId);

        noteDao.updateNote(note);
    }

    private void insertNoteVersion(NoteVersion noteVersion, int noteId) {
        noteVersion.setId(noteId);
        noteDao.insertNoteVersion(noteVersion);
    }

    private void insertNote(Note note) {
        noteDao.insertNote(note);
        insertNoteVersion(note.getNoteVersion(), note.getId());
    }
}
