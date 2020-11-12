package net.thumbtack.school.notes.dto.mappers;

import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.response.NoteInfoDtoResponse;
import net.thumbtack.school.notes.model.Note;
import net.thumbtack.school.notes.model.NoteVersion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public class NoteDtoMapper {
    public static final NoteDtoMapper INSTANCE = Mappers.getMapper(NoteDtoMapper.class);

    public Note toNote(CreateNoteDtoRequest request) {
        return new Note(
                request.getSubject(),
                request.getSectionId(),
                new NoteVersion(
                        request.getBody()
                )
        );
    }

    public NoteInfoDtoResponse toNoteDtoResponse(Note note) {
        NoteVersion noteVersion = note.getNoteVersion();
        return new NoteInfoDtoResponse(
                note.getId(),
                note.getSubject(),
                noteVersion.getBody(),
                note.getSectionId(),
                note.getAuthorId(),
                note.getCreated().toString(), //TODO:set format!!!!!!!!!!
                noteVersion.getRevisionId()
        );
    }
}
