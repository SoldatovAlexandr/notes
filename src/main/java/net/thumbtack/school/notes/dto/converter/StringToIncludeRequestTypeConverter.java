package net.thumbtack.school.notes.dto.converter;

import com.google.common.base.CaseFormat;
import net.thumbtack.school.notes.dto.request.params.IncludeRequestType;
import org.springframework.core.convert.converter.Converter;

public class StringToIncludeRequestTypeConverter implements Converter<String, IncludeRequestType> {
    @Override
    public IncludeRequestType convert(String typeString) {
        try {
            String type = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, typeString);
            return IncludeRequestType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
