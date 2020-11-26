package net.thumbtack.school.notes.dto.mappers;

import net.thumbtack.school.notes.dto.request.CreateCommentDtoRequest;
import net.thumbtack.school.notes.dto.response.CommentInfoDtoResponse;
import net.thumbtack.school.notes.model.Comment;
import net.thumbtack.school.notes.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public class CommentDtoMapper {
    public static final CommentDtoMapper INSTANCE = Mappers.getMapper(CommentDtoMapper.class);

    public Comment toComment(CreateCommentDtoRequest createCommentDtoRequest) {
        Note note = new Note();
        note.setId(createCommentDtoRequest.getNoteId());
        return new Comment(
                createCommentDtoRequest.getBody(),
                note
        );
    }

    public CommentInfoDtoResponse toCommentInfoDtoResponse(Comment comment) {
        return new CommentInfoDtoResponse(
                comment.getId(),
                comment.getBody(),
                comment.getNote().getId(),
                comment.getAuthor().getId(),
                comment.getRevisionId(),
                comment.getCreated().toString()
        );
    }

    public List<CommentInfoDtoResponse> toCommentsInfoDtoResponse(List<Comment> comments) {
        List<CommentInfoDtoResponse> responses = new ArrayList<>();
        for (Comment comment : comments) {
            responses.add(toCommentInfoDtoResponse(comment));
        }
        return responses;
    }
}
