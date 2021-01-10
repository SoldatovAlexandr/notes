package net.thumbtack.school.notes.service;

import net.thumbtack.school.notes.Config;
import net.thumbtack.school.notes.dao.CommentDao;
import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dao.SectionDao;
import net.thumbtack.school.notes.dao.UserDao;
import net.thumbtack.school.notes.dto.mappers.NoteDtoMapper;
import net.thumbtack.school.notes.dto.request.AddRatingDtoRequest;
import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateNoteDtoRequest;
import net.thumbtack.school.notes.dto.request.params.IncludeRequestType;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.NoteDtoResponse;
import net.thumbtack.school.notes.dto.response.NoteInfoDtoResponse;
import net.thumbtack.school.notes.erroritem.code.ServerErrorCodeWithField;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.model.*;
import net.thumbtack.school.notes.views.NoteView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService extends ServiceBase {
    private static final int START_REVISION_ID = 1;
    private static final int INCREMENT_REVISION_ID = 1;

    @Autowired
    public NoteService(UserDao userDao, SectionDao sectionDao, NoteDao noteDao, CommentDao commentDao, Config config) {
        super(userDao, sectionDao, noteDao, commentDao, config);
    }


    public NoteInfoDtoResponse createNote(CreateNoteDtoRequest createNoteDtoRequest, String token)
            throws ServerException {
        Session session = getSession(token);

        Note note = NoteDtoMapper.INSTANCE.toNote(createNoteDtoRequest);

        Section section = getSection(note.getSection().getId());

        note.setAuthor(session.getUser());

        note.setCreated(getCurrentDateTime());

        note.getCurrentVersion().setRevisionId(START_REVISION_ID);

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

        User user = session.getUser();

        Note note = getNote(noteId);

        if (note.getAuthor().getId() == user.getId()) {
            throw new ServerException(ServerErrorCodeWithField.CAN_NOT_RATE);
        }

        Rating rating = new Rating(user, note, addRatingDtoRequest.getRating());

        noteDao.insertRating(rating);

        return new EmptyDtoResponse();
    }

    public List<NoteDtoResponse> getNotes(Integer sectionId, SortRequestType sortByRating, List<String> tags,
                                          boolean allTags, LocalDateTime timeFrom, LocalDateTime timeTo, Integer userId,
                                          IncludeRequestType include, boolean comment, boolean allVersion,
                                          boolean commentVersion, Integer from, Integer count,
                                          String token) throws ServerException {
        Session session = getSession(token);

        if (sectionId != null) {
            Section section = getSection(sectionId);
        }
        if (userId != null) {
            User user = getUserById(userId);
        }
        if (timeTo == null) {
            timeTo = LocalDateTime.now();
        }

        List<NoteView> notes = noteDao.getNotes(sectionId, sortByRating, tags, allTags, timeFrom, timeTo,
                userId, include, from, count, session.getUser().getId());

        return toNoteDtoResponse(notes, comment, allVersion, commentVersion);
    }

    private List<NoteDtoResponse> toNoteDtoResponse(List<NoteView> notes, boolean comment, boolean allVersion,
                                                    boolean commentVersion) {
        if (allVersion) {
            if (comment) {
                if (commentVersion) {
                    return NoteDtoMapper.INSTANCE.toNoteWithCommentsAndVersionDtoResponse(notes);
                } else {
                    return NoteDtoMapper.INSTANCE.toNoteWithCommentsDtoResponse(notes);
                }
            } else {
                return NoteDtoMapper.INSTANCE.toNoteWithVersionsDtoResponse(notes);
            }
        } else {
            return NoteDtoMapper.INSTANCE.toNoteDtoResponse(notes);
        }
    }

    private void checkNotePermission(Note note, User user) throws ServerException {
        if (!(isAuthor(note, user) || isSuper(user))) {
            throw new ServerException(ServerErrorCodeWithField.NO_PERMISSIONS);
        }
    }

    private void addNoteVersion(String body, Note note) {
        NoteVersion noteVersion = new NoteVersion(body);

        int newRevisionId = note.getCurrentVersion().getRevisionId() + INCREMENT_REVISION_ID;

        noteVersion.setRevisionId(newRevisionId);

        insertNoteVersion(noteVersion, note);

        note.getNoteVersions().add(noteVersion);
    }

    private void moveNoteToSection(Note note, int sectionId) throws ServerException {
        Section section = getSection(sectionId);

        note.getSection().setId(sectionId);

        noteDao.updateNote(note);
    }

    private void insertNoteVersion(NoteVersion noteVersion, Note note) {
        noteVersion.setNote(note);

        noteDao.insertNoteVersion(noteVersion);
    }

    private void insertNote(Note note) {
        noteDao.insertNote(note);

        insertNoteVersion(note.getCurrentVersion(), note);
    }
}
