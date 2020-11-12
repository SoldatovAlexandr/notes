package net.thumbtack.school.notes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.notes.validator.NameLength;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDtoRequest {
    @NotNull(message = "NAME_NOT_SET")
    @NameLength
    private String name;
}
