package net.thumbtack.school.notes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.notes.validator.UpdateNote;

@Data
@AllArgsConstructor
@NoArgsConstructor
@UpdateNote(first = "body", second = "sectionId")
public class UpdateNoteDtoRequest {

    private String body;

    private Integer sectionId;
}
