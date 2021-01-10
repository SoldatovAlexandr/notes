package net.thumbtack.school.notes.dto.converter;

import com.google.common.base.CaseFormat;
import net.thumbtack.school.notes.dto.request.params.SortRequestType;
import org.springframework.core.convert.converter.Converter;

public class StringToSortRequestTypeConverter implements Converter<String, SortRequestType> {
    @Override
    public SortRequestType convert(String typeString) {
        try {
            String type = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, typeString);
            return SortRequestType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return SortRequestType.WITHOUT;
        }
    }
}
