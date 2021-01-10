package net.thumbtack.school.notes.dto.mappers;

import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.response.*;
import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.NoteVersion;
import net.thumbtack.school.notes.model.Section;
import net.thumbtack.school.notes.views.CommentView;
import net.thumbtack.school.notes.views.NoteView;
import net.thumbtack.school.notes.views.RevisionView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class NoteDtoMapper {
    public static final NoteDtoMapper INSTANCE = Mappers.getMapper(NoteDtoMapper.class);

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public Note toNote(CreateNoteDtoRequest request) {
        List<NoteVersion> noteVersions = Collections.singletonList(new NoteVersion(request.getBody()));
        Section section = new Section(request.getSectionId());
        return new Note(
                request.getSubject(),
                section,
                noteVersions
        );
    }

    public NoteInfoDtoResponse toNoteDtoResponse(Note note) {
        NoteVersion noteVersion = note.getCurrentVersion();
        return new NoteInfoDtoResponse(
                note.getId(),
                note.getSubject(),
                noteVersion.getBody(),
                note.getSection().getId(),
                note.getAuthor().getId(),
                note.getCreated().format(dateTimeFormatter),
                noteVersion.getRevisionId()
        );
    }

    public List<NoteDtoResponse> toNoteDtoResponse(List<NoteView> notes) {
        List<NoteDtoResponse> response = new ArrayList<>();
        for (NoteView note : notes) {
            response.add(toNoteDtoResponse(note));
        }
        return response;
    }

    public List<NoteDtoResponse> toNoteWithVersionsDtoResponse(List<NoteView> notes) {
        List<NoteDtoResponse> response = new ArrayList<>();
        for (NoteView note : notes) {
            response.add(toNoteWithVersionsDtoResponse(note));
        }
        return response;
    }

    public List<NoteDtoResponse> toNoteWithCommentsDtoResponse(List<NoteView> notes) {
        List<NoteDtoResponse> response = new ArrayList<>();
        for (NoteView note : notes) {
            response.add(toNoteWithCommentsDtoResponse(note));
        }
        return response;
    }

    public List<NoteDtoResponse> toNoteWithCommentsAndVersionDtoResponse(List<NoteView> notes) {
        List<NoteDtoResponse> response = new ArrayList<>();
        for (NoteView note : notes) {
            response.add(toNoteWithCommentsAndVersionsDtoResponse(note));
        }
        return response;
    }

    private NoteDtoResponse toNoteWithCommentsAndVersionsDtoResponse(NoteView noteView) {
        return new NoteWithAllVersionsDtoResponse(
                noteView.getId(),
                noteView.getAuthorId(),
                noteView.getSectionId(),
                noteView.getSubject(),
                noteView.getBody(),
                noteView.getCreated().format(dateTimeFormatter),
                toRevisionWithCommentsAndVersionsDtoItems(noteView.getRevisions()));
    }

    private List<RevisionDtoItem> toRevisionWithCommentsAndVersionsDtoItems(List<RevisionView> revisions) {
        List<RevisionDtoItem> response = new ArrayList<>();
        for (RevisionView view : revisions) {
            response.add(toRevisionWithCommentsAndVersionsDtoItem(view));
        }
        return response;
    }

    private RevisionDtoItem toRevisionWithCommentsAndVersionsDtoItem(RevisionView revisionView) {
        return new RevisionWithCommentsDtoItem(
                revisionView.getId(),
                revisionView.getBody(),
                revisionView.getCreated().format(dateTimeFormatter),
                toCommentWithVersionsDtoItems(revisionView.getComments())
        );
    }

    private List<CommentDtoItem> toCommentWithVersionsDtoItems(List<CommentView> comments) {
        List<CommentDtoItem> response = new ArrayList<>();
        for (CommentView comment : comments) {
            response.add(toCommentWithVersionDtoItem(comment));
        }
        return response;
    }

    @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    public abstract CommentDtoItem toCommentWithVersionDtoItem(CommentView comment);

    private NoteDtoResponse toNoteWithCommentsDtoResponse(NoteView noteView) {
        return new NoteWithAllVersionsDtoResponse(
                noteView.getId(),
                noteView.getAuthorId(),
                noteView.getSectionId(),
                noteView.getSubject(),
                noteView.getBody(),
                noteView.getCreated().format(dateTimeFormatter),
                toRevisionWithCommentsDtoItems(noteView.getRevisions()));
    }

    private List<RevisionDtoItem> toRevisionWithCommentsDtoItems(List<RevisionView> revisions) {
        List<RevisionDtoItem> response = new ArrayList<>();
        for (RevisionView view : revisions) {
            response.add(toRevisionWithCommentsDtoItem(view));
        }
        return response;
    }

    private RevisionDtoItem toRevisionWithCommentsDtoItem(RevisionView revisionView) {
        return new RevisionWithCommentsDtoItem(
                revisionView.getId(),
                revisionView.getBody(),
                revisionView.getCreated().format(dateTimeFormatter),
                toCommentDtoItems(revisionView.getComments())
        );
    }

    private List<CommentDtoItem> toCommentDtoItems(List<CommentView> comments) {
        List<CommentDtoItem> response = new ArrayList<>();
        for (CommentView comment : comments) {
            response.add(toCommentDtoItem(comment));
        }
        return response;
    }

    @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    public abstract CommentDtoItem toCommentDtoItem(CommentView comment);

    @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    public abstract NoteDtoResponse toNoteDtoResponse(NoteView noteView);

    private NoteDtoResponse toNoteWithVersionsDtoResponse(NoteView noteView) {
        return new NoteWithAllVersionsDtoResponse(
                noteView.getId(),
                noteView.getAuthorId(),
                noteView.getSectionId(),
                noteView.getSubject(),
                noteView.getBody(),
                noteView.getCreated().format(dateTimeFormatter),
                toRevisionDtoItems(noteView.getRevisions()));
    }

    private List<RevisionDtoItem> toRevisionDtoItems(List<RevisionView> revisionViews) {
        List<RevisionDtoItem> response = new ArrayList<>();
        for (RevisionView view : revisionViews) {
            response.add(toRevisionDtoItem(view));
        }
        return response;
    }

    @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    public abstract RevisionDtoItem toRevisionDtoItem(RevisionView revisionView);
}
