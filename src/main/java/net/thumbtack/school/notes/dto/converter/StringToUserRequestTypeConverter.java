package net.thumbtack.school.notes.dto.converter;

import com.google.common.base.CaseFormat;
import net.thumbtack.school.notes.dto.request.params.UserRequestType;
import org.springframework.core.convert.converter.Converter;

public class StringToUserRequestTypeConverter implements Converter<String, UserRequestType> {
    @Override
    public UserRequestType convert(String typeString) {
        try {
            String type = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, typeString);
            return UserRequestType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return UserRequestType.ALL_USERS;
        }
    }
}
