package net.thumbtack.school.notes.dto.mappers;

import net.thumbtack.school.notes.dto.request.CreateCommentDtoRequest;
import net.thumbtack.school.notes.dto.response.CommentInfoDtoResponse;
import net.thumbtack.school.notes.model.Comment;
import net.thumbtack.school.notes.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class CommentDtoMapper {
    public static final CommentDtoMapper INSTANCE = Mappers.getMapper(CommentDtoMapper.class);

    public Comment toComment(CreateCommentDtoRequest createCommentDtoRequest) {
        Note note = new Note();
        note.setId(createCommentDtoRequest.getNoteId());
        return new Comment(
                createCommentDtoRequest.getBody(),
                note
        );
    }

    @Mappings({
            @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "note.id", target = "noteId"),
            @Mapping(source = "author.id", target = "authorId")
    })
    public abstract CommentInfoDtoResponse toCommentInfoDtoResponse(Comment comment);


    public List<CommentInfoDtoResponse> toCommentsInfoDtoResponse(Note note) {
        List<CommentInfoDtoResponse> responses = new ArrayList<>();
        for (Comment comment : note.getComments()) {
            responses.add(toCommentInfoDtoResponse(comment));
        }
        return responses;
    }
}
