package net.thumbtack.school.notes.integration;

import net.thumbtack.school.notes.dto.request.CreateCommentDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateCommentDtoRequest;
import net.thumbtack.school.notes.dto.response.CommentInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CommentIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testAddComment() {
        String cookie = registerUser("login");

        int sectionId = insertSection("name", cookie);

        int noteId = insertNote("body", cookie, sectionId);

        final String url = "http://localhost:8080/api/comments";

        CreateCommentDtoRequest createCommentDtoRequest = new CreateCommentDtoRequest("body", noteId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<CreateCommentDtoRequest> request = new HttpEntity<>(createCommentDtoRequest, headers);

        HttpEntity<CommentInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                CommentInfoDtoResponse.class);

        CommentInfoDtoResponse dtoResponse = response.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals("body", dtoResponse.getBody()),
                () -> Assertions.assertNotNull(dtoResponse.getId()),
                () -> Assertions.assertEquals(noteId, dtoResponse.getNoteId())
        );
    }

    @Test
    public void testGetComment() {
        String cookie = registerUser("login");

        int sectionId = insertSection("name", cookie);

        int noteId = insertNote("body", cookie, sectionId);

        final String url = "http://localhost:8080/api/comments";

        CreateCommentDtoRequest createCommentDtoRequest = new CreateCommentDtoRequest("body", noteId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<CreateCommentDtoRequest> request = new HttpEntity<>(createCommentDtoRequest, headers);

        HttpEntity<CommentInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                CommentInfoDtoResponse.class);

        CommentInfoDtoResponse dtoResponse = response.getBody();

        final String getUrl = "http://localhost:8080/api/notes/" + noteId + "/comments";

        HttpEntity<CommentInfoDtoResponse[]> getResponse = template.exchange(
                getUrl,
                HttpMethod.GET,
                request,
                CommentInfoDtoResponse[].class);

        List<CommentInfoDtoResponse> getDtoResponse = List.of(getResponse.getBody());

        Assertions.assertAll(
                () -> Assertions.assertEquals(List.of(dtoResponse), getDtoResponse)
        );
    }

    @Test
    public void testUpdateComment() {
        String cookie = registerUser("login");

        int sectionId = insertSection("name", cookie);

        int noteId = insertNote("body", cookie, sectionId);

        int commentId = addComment(cookie, noteId, "body");

        final String url = "http://localhost:8080/api/comments/" + commentId;

        UpdateCommentDtoRequest updateCommentDtoRequest = new UpdateCommentDtoRequest("new body");

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<UpdateCommentDtoRequest> request = new HttpEntity<>(updateCommentDtoRequest, headers);

        HttpEntity<CommentInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.PUT,
                request,
                CommentInfoDtoResponse.class);

        CommentInfoDtoResponse dtoResponse = response.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(commentId, dtoResponse.getId()),
                () -> Assertions.assertEquals("new body", dtoResponse.getBody()),
                () -> Assertions.assertEquals(noteId, dtoResponse.getNoteId())
        );
    }

    @Test
    public void testDeleteComment() {
        String cookie = registerUser("login");

        int sectionId = insertSection("name", cookie);

        int noteId = insertNote("body", cookie, sectionId);

        int commentId = addComment(cookie, noteId, "body");

        final String url = "http://localhost:8080/api/comments/" + commentId;

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        HttpEntity<EmptyDtoResponse> response = template.exchange(
                url,
                HttpMethod.DELETE,
                request,
                EmptyDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
    }

    @Test
    public void testDeleteComments() {
        String cookie = registerUser("login");

        int sectionId = insertSection("name", cookie);

        int noteId = insertNote("body", cookie, sectionId);

        int commentId = addComment(cookie, noteId, "body");

        final String url = "http://localhost:8080/api/notes/" + noteId + "/comments";

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        HttpEntity<EmptyDtoResponse> response = template.exchange(
                url,
                HttpMethod.DELETE,
                request,
                EmptyDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
    }
}
