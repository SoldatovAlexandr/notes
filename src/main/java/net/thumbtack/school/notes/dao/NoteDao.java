package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.NoteVersion;
import net.thumbtack.school.notes.model.Rating;

public interface NoteDao {
    void insertNote(Note note);

    void insertNoteVersion(NoteVersion noteVersion);

    Note getNoteById(int noteId);

    void updateNote(Note note);

    int deleteNote(Note note);

    void insertRating(Rating rating);

    Rating getRating(int userId, int noteId);
}
