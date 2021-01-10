package net.thumbtack.school.notes.integration;

import net.thumbtack.school.notes.dao.CommonDao;
import net.thumbtack.school.notes.dto.request.*;
import net.thumbtack.school.notes.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    protected String registerUser(String login) {
        final String url = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest dtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", login, "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> request = new HttpEntity<>(dtoRequest);

        HttpEntity<ProfileInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                ProfileInfoDtoResponse.class);

        return response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    }

    protected int insertSection(String name, String cookie) {
        final String url = "http://localhost:8080/api/sections";

        SectionDtoRequest body = new SectionDtoRequest(name);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<SectionDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<SectionDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                SectionDtoResponse.class);

        return response.getBody().getId();
    }

    protected int insertNote(String body, String cookie, int sectionId) {
        final String url = "http://localhost:8080/api/notes";

        CreateNoteDtoRequest requestBody = new CreateNoteDtoRequest("subject", body, sectionId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<CreateNoteDtoRequest> request = new HttpEntity<>(requestBody, headers);

        HttpEntity<NoteInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                NoteInfoDtoResponse.class);

        return response.getBody().getId();
    }

    protected void addRating(int noteId, String cookie, int rating) {
        final String url = "http://localhost:8080/api/notes/" + noteId + "/rating";

        AddRatingDtoRequest addRatingDtoRequest = new AddRatingDtoRequest(rating);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<AddRatingDtoRequest> request = new HttpEntity<>(addRatingDtoRequest, headers);

        template.exchange(
                url,
                HttpMethod.POST,
                request,
                EmptyDtoResponse.class);
    }

    protected int addComment(String cookie, int noteId, String body) {
        final String url = "http://localhost:8080/api/comments";

        CreateCommentDtoRequest createCommentDtoRequest = new CreateCommentDtoRequest(body, noteId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<CreateCommentDtoRequest> request = new HttpEntity<>(createCommentDtoRequest, headers);

        HttpEntity<CommentInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                CommentInfoDtoResponse.class);

        return response.getBody().getId();
    }
}
