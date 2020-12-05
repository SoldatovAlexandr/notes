package net.thumbtack.school.notes.dto.converter;

import com.google.common.base.CaseFormat;
import net.thumbtack.school.notes.dto.response.SortType;
import org.springframework.core.convert.converter.Converter;

public class StringToSortRequestTypeConverter implements Converter<String, SortType> {
    @Override
    public SortType convert(String typeString) {
        try {
            String type = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, typeString);
            return SortType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
