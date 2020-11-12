package net.thumbtack.school.notes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.notes.validator.NameLength;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoteDtoRequest {
    @NotNull(message = "SUBJECT_NOT_SET")
    @NameLength
    private String subject;

    @NotNull(message = "BODY_NOT_SET")
    private String body;

    @NotNull(message = "SECTION_ID_NOT_SET")
    private Integer sectionId;
}
