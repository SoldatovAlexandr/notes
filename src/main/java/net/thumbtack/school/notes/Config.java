package net.thumbtack.school.notes;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("net.thumbtack.school.notes.mappers")
@PropertySource("classpath:application.properties")
public class Config {
    @Value("${max_name_length}")
    private int maxNameLength;
    @Value("${min_password_length}")
    private int minPasswordLength;

    public int getMaxNameLength() {
        return maxNameLength;
    }

    public int getMinPasswordLength() {
        return minPasswordLength;
    }
}
