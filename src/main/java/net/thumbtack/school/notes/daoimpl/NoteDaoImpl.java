package net.thumbtack.school.notes.daoimpl;

import net.thumbtack.school.notes.dao.NoteDao;
import net.thumbtack.school.notes.dto.request.params.IncludeRequestType;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import net.thumbtack.school.notes.mappers.NoteMapper;
import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.NoteVersion;
import net.thumbtack.school.notes.model.Rating;
import net.thumbtack.school.notes.views.NoteView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
    public void insertNoteVersion(NoteVersion noteVersion) {
        LOGGER.debug("DAO insert note version: {}", noteVersion);
        noteMapper.insertNoteVersion(noteVersion);
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
    public List<NoteView> getNotes(Integer sectionId, SortRequestType sortByRating, List<String> listTags,
                                   boolean allTags, LocalDateTime timeFrom, LocalDateTime timeTo, Integer userId,
                                   IncludeRequestType include, Integer from, Integer count, Integer profileId) {
        LOGGER.debug("DAO get notes");

        String tags = buildTags(listTags);

        boolean hasTags = !tags.isEmpty();

        boolean sort = !sortByRating.equals(SortRequestType.WITHOUT);

        boolean asc = sortByRating.equals(SortRequestType.ASC);

        boolean needUser = userId != null;

        boolean needSection = sectionId != null;

        boolean onlyIgnore = include.equals(IncludeRequestType.ONLY_IGNORE);

        boolean notIgnore = include.equals(IncludeRequestType.NOT_IGNORE);

        boolean onlyFollowing = include.equals(IncludeRequestType.ONLY_FOLLOWINGS);

        return noteMapper.getNotes(
                sectionId,
                tags,
                allTags,
                timeFrom,
                timeTo,
                userId,
                from,
                count,
                profileId,
                hasTags,
                sort,
                asc,
                needUser,
                needSection,
                onlyIgnore,
                notIgnore,
                onlyFollowing
        );
    }

    private String buildTags(List<String> listTags) {
        StringBuilder tags = new StringBuilder();
        if (listTags == null) {
            return "";
        }
        for (String str : listTags) {
            if (!str.isEmpty()) {
                tags.append("+").append(str).append(" ");
            }
        }
        return tags.toString();
    }
}
