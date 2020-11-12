package net.thumbtack.school.notes.daoimpl;

import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.mappers.NoteMapper;
import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.NoteVersion;
import net.thumbtack.school.notes.model.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class NoteDaoImpl implements NoteDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoteDaoImpl.class);
    private final NoteMapper noteMapper;

    @Autowired
    public NoteDaoImpl(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }


    @Override
    public void insertNote(Note note) {
        LOGGER.debug("DAO insert note: {}", note);
        noteMapper.insertNote(note);
    }

    @Override
    public void insertNoteVersion(NoteVersion noteVersion, int noteId) {
        LOGGER.debug("DAO insert note version: {}", noteVersion);
        noteMapper.insertNoteVersion(noteVersion, noteId);
    }

    @Override
    public Note getNoteById(int noteId) {
        LOGGER.debug("DAO get note by noteId: {}", noteId);
        return noteMapper.getNoteById(noteId);
    }

    @Override
    public void updateNote(Note note) {
        LOGGER.debug("DAO update note: {}", note);
        noteMapper.updateNote(note);
    }

    @Override
    public int deleteNote(Note note) {
        LOGGER.debug("DAO delete note: {}", note);
        return noteMapper.deleteNote(note);
    }

    @Override
    public void insertRating(Rating rating) {
        LOGGER.debug("DAO insert rating: {}", rating);
        noteMapper.insertRating(rating);
    }

    @Override
    public Rating getRating(int userId, int noteId) {
        LOGGER.debug("DAO get rating by userId: {} , noteId {}", userId, noteId);
        return noteMapper.getRating(userId, noteId);
    }

    @Override
    public void updateRating(Rating rating) {
        LOGGER.debug("DAO update rating: {}", rating);
        noteMapper.updateRating(rating);
    }
}
