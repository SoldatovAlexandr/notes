package net.thumbtack.school.notes.dto.mappers;

import net.thumbtack.school.notes.dto.response.SectionDtoResponse;
import net.thumbtack.school.notes.model.Section;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class SectionDtoMapper {
    public static final SectionDtoMapper INSTANCE = Mappers.getMapper(SectionDtoMapper.class);

    public abstract SectionDtoResponse toSectionDtoResponse(Section section);

    public abstract List<SectionDtoResponse> toListSectionDtoResponse(List<Section> sections);
}
