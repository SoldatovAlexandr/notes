package net.thumbtack.school.notes.dto.mappers;

import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.response.NoteInfoDtoResponse;
import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.NoteVersion;
import net.thumbtack.school.notes.model.Section;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public class NoteDtoMapper {
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
}
