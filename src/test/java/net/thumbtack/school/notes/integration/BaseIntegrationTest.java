package net.thumbtack.school.notes.integration;

import net.thumbtack.school.notes.dao.CommonDao;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseIntegrationTest {

    protected RestTemplate template = new RestTemplate();

    @Autowired
    protected CommonDao commonDao;

    @BeforeEach
    public void clear() {
        commonDao.clear();
    }
}
