package net.thumbtack.school.notes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.notes.validator.Rating;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRatingDtoRequest {

    @NotNull(message = "RATING_NOT_SET")
    @Rating
    private Integer rating;
}
