package net.thumbtack.school.notes;

import net.thumbtack.school.notes.dto.converter.StringToIncludeRequestTypeConverter;
import net.thumbtack.school.notes.dto.converter.StringToSortRequestTypeConverter;
import net.thumbtack.school.notes.dto.converter.StringToUserRequestTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToUserRequestTypeConverter());
        registry.addConverter(new StringToSortRequestTypeConverter());
        registry.addConverter(new StringToIncludeRequestTypeConverter());
    }
}
