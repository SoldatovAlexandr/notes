package net.thumbtack.school.notes.integration;

import net.thumbtack.school.notes.dto.request.SectionDtoRequest;
import net.thumbtack.school.notes.dto.response.SectionDtoResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testCreateSection() {
        String cookie = registerUser("login");

        final String url = "http://localhost:8080/api/sections";

        SectionDtoRequest body = new SectionDtoRequest("section");

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<SectionDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<SectionDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                SectionDtoResponse.class);

        SectionDtoResponse sectionDtoResponse = response.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals("section", sectionDtoResponse.getName()),
                () -> Assertions.assertNotNull(sectionDtoResponse.getId())
        );
    }

    @Test
    public void testDoubleCreateSection() {
        String cookie = registerUser("login");

        final String url = "http://localhost:8080/api/sections";

        SectionDtoRequest body = new SectionDtoRequest("section");

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<SectionDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<SectionDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                SectionDtoResponse.class);

        try {
            template.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    SectionDtoResponse.class);
            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

    @Test
    public void testRenameSection() {
        String cookie = registerUser("login");

        final String url = "http://localhost:8080/api/sections";

        SectionDtoRequest body = new SectionDtoRequest("section");

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<SectionDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<SectionDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                SectionDtoResponse.class);

        SectionDtoResponse sectionDtoResponse = response.getBody();

        body.setName("new section");

        HttpEntity<SectionDtoResponse> renameResponse = template.exchange(
                url + "/" + sectionDtoResponse.getId(),
                HttpMethod.PUT,
                request,
                SectionDtoResponse.class);

        SectionDtoResponse renameDtoResponse = renameResponse.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals("new section", renameDtoResponse.getName()),
                () -> Assertions.assertEquals(sectionDtoResponse.getId(), renameDtoResponse.getId())
        );
    }

    @Test
    public void testRemoveSection() {
        String cookie = registerUser("login");

        final String url = "http://localhost:8080/api/sections";

        SectionDtoRequest body = new SectionDtoRequest("section");

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<SectionDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<SectionDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                SectionDtoResponse.class);

        SectionDtoResponse sectionDtoResponse = response.getBody();

        HttpEntity<SectionDtoResponse> deleteResponse = template.exchange(
                url + "/" + sectionDtoResponse.getId(),
                HttpMethod.DELETE,
                request,
                SectionDtoResponse.class);

        Assertions.assertNotNull(deleteResponse);
    }

    @Test
    public void testGetSection() {
        String cookie = registerUser("login");

        final String url = "http://localhost:8080/api/sections";

        SectionDtoRequest body = new SectionDtoRequest("section");

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<SectionDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<SectionDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                SectionDtoResponse.class);

        SectionDtoResponse sectionDtoResponse = response.getBody();

        HttpEntity<SectionDtoResponse> getResponse = template.exchange(
                url + "/" + sectionDtoResponse.getId(),
                HttpMethod.GET,
                request,
                SectionDtoResponse.class);

        SectionDtoResponse getDtoResponse = getResponse.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals("section", getDtoResponse.getName()),
                () -> Assertions.assertEquals(sectionDtoResponse.getId(), getDtoResponse.getId())
        );
    }

    @Test
    public void testGetAllSection() {
        String cookie = registerUser("login");

        final String url = "http://localhost:8080/api/sections";

        SectionDtoRequest body = new SectionDtoRequest("section");

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<SectionDtoRequest> request = new HttpEntity<>(body, headers);

        List<SectionDtoResponse> expectedResponse = new ArrayList<>();

        List<String> sectionNames = List.of("first", "second", "third");

        for (String name : sectionNames) {
            body.setName(name);

            HttpEntity<SectionDtoResponse> response = template.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    SectionDtoResponse.class);

            expectedResponse.add(response.getBody());
        }

        HttpEntity<SectionDtoResponse[]> getResponse = template.exchange(
                url,
                HttpMethod.GET,
                request,
                SectionDtoResponse[].class);

        List<SectionDtoResponse> getDtoResponse = List.of(getResponse.getBody());

        Assertions.assertEquals(expectedResponse, getDtoResponse);
    }
}
