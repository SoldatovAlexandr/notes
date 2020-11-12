package net.thumbtack.school.notes.dto.mappers;

import net.thumbtack.school.notes.dto.response.SectionDtoResponse;
import net.thumbtack.school.notes.model.Section;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public class SectionDtoMapper {
    public static final SectionDtoMapper INSTANCE = Mappers.getMapper(SectionDtoMapper.class);

    public SectionDtoResponse toSectionDtoResponse(Section section) {
        return new SectionDtoResponse(
                section.getId(),
                section.getName()
        );
    }

    public List<SectionDtoResponse> toListSectionDtoResponse(List<Section> sections) {
        List<SectionDtoResponse> sectionDtoResponses = new ArrayList<>();
        for (Section section : sections) {
            sectionDtoResponses.add(toSectionDtoResponse(section));
        }
        return sectionDtoResponses;
    }
}
