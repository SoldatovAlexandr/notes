package net.thumbtack.school.notes.dao;

import net.thumbtack.school.notes.dto.request.params.IncludeRequestType;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.NoteVersion;
import net.thumbtack.school.notes.model.Rating;
import net.thumbtack.school.notes.views.NoteView;

import java.time.LocalDateTime;
import java.util.List;

public interface NoteDao {
    void insertNote(Note note);

    void insertNoteVersion(NoteVersion noteVersion);

    Note getNoteById(int noteId);

    void updateNote(Note note);

    int deleteNote(Note note);

    void insertRating(Rating rating);

    Rating getRating(int userId, int noteId);

    List<NoteView> getNotes(Integer sectionId, SortRequestType sortByRating, List<String> tags, boolean allTags,
                            LocalDateTime timeFrom, LocalDateTime timeTo, Integer userId, IncludeRequestType include,
                            boolean comment, boolean allVersion, boolean commentVersion, Integer from, Integer count);

}
