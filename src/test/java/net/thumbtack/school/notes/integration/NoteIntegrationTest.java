package net.thumbtack.school.notes.integration;

import net.thumbtack.school.notes.dto.request.AddRatingDtoRequest;
import net.thumbtack.school.notes.dto.request.CreateNoteDtoRequest;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.NoteDtoResponse;
import net.thumbtack.school.notes.dto.response.NoteInfoDtoResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class NoteIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testAddNote() {
        String cookie = registerUser("login");

        int sectionId = insertSection("name", cookie);

        final String url = "http://localhost:8080/api/notes";

        CreateNoteDtoRequest body = new CreateNoteDtoRequest("subject", "body", sectionId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<CreateNoteDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<NoteInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                NoteInfoDtoResponse.class);

        NoteInfoDtoResponse noteInfoDtoResponse = response.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals("body", noteInfoDtoResponse.getBody()),
                () -> Assertions.assertEquals("subject", noteInfoDtoResponse.getSubject()),
                () -> Assertions.assertNotNull(noteInfoDtoResponse.getId())
        );
    }

    @Test
    public void testGetNote() {
        String cookie = registerUser("login");

        int sectionId = insertSection("name", cookie);

        final String url = "http://localhost:8080/api/notes";

        CreateNoteDtoRequest body = new CreateNoteDtoRequest("subject", "body", sectionId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<CreateNoteDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<NoteInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                NoteInfoDtoResponse.class);

        NoteInfoDtoResponse addDtoResponse = response.getBody();

        HttpEntity<NoteInfoDtoResponse> getResponse = template.exchange(
                url + "/" + addDtoResponse.getId(),
                HttpMethod.GET,
                request,
                NoteInfoDtoResponse.class);

        NoteInfoDtoResponse getDtoResponse = getResponse.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(addDtoResponse.getBody(), getDtoResponse.getBody()),
                () -> Assertions.assertEquals(addDtoResponse.getSubject(), getDtoResponse.getSubject()),
                () -> Assertions.assertEquals(addDtoResponse.getId(), getDtoResponse.getId())
        );
    }

    @Test
    public void testUpdateNote() {
        String cookie = registerUser("login");

        int firstSectionId = insertSection("first", cookie);

        int secondSectionId = insertSection("second", cookie);

        final String url = "http://localhost:8080/api/notes";

        CreateNoteDtoRequest body = new CreateNoteDtoRequest("subject", "body", firstSectionId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<CreateNoteDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<NoteInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                NoteInfoDtoResponse.class);

        NoteInfoDtoResponse addDtoResponse = response.getBody();

        body.setBody("new body");
        body.setSectionId(secondSectionId);

        HttpEntity<NoteInfoDtoResponse> getResponse = template.exchange(
                url + "/" + addDtoResponse.getId(),
                HttpMethod.PUT,
                request,
                NoteInfoDtoResponse.class);

        NoteInfoDtoResponse getDtoResponse = getResponse.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals("new body", getDtoResponse.getBody()),
                () -> Assertions.assertEquals(addDtoResponse.getSubject(), getDtoResponse.getSubject()),
                () -> Assertions.assertEquals(secondSectionId, getDtoResponse.getSectionId()),
                () -> Assertions.assertEquals(addDtoResponse.getId(), getDtoResponse.getId())
        );
    }

    @Test
    public void testUpdateNoteFail() {
        String firstCookie = registerUser("first");

        String secondCookie = registerUser("second");

        int firstSectionId = insertSection("first", firstCookie);

        int secondSectionId = insertSection("second", firstCookie);

        final String url = "http://localhost:8080/api/notes";

        CreateNoteDtoRequest body = new CreateNoteDtoRequest("subject", "body", firstSectionId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, firstCookie);

        HttpEntity<CreateNoteDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<NoteInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                NoteInfoDtoResponse.class);

        NoteInfoDtoResponse addDtoResponse = response.getBody();


        CreateNoteDtoRequest updateBody = new CreateNoteDtoRequest("subject", "new body", secondSectionId);

        HttpHeaders updateHeaders = new HttpHeaders();

        updateHeaders.add(HttpHeaders.COOKIE, secondCookie);

        HttpEntity<CreateNoteDtoRequest> updateRequest = new HttpEntity<>(updateBody, updateHeaders);

        try {
            template.exchange(
                    url + "/" + addDtoResponse.getId(),
                    HttpMethod.PUT,
                    updateRequest,
                    NoteInfoDtoResponse.class);

            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

    @Test
    public void testDeleteNote() {
        String cookie = registerUser("login");

        int sectionId = insertSection("name", cookie);

        final String url = "http://localhost:8080/api/notes";

        CreateNoteDtoRequest body = new CreateNoteDtoRequest("subject", "body", sectionId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<CreateNoteDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<NoteInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                NoteInfoDtoResponse.class);

        NoteInfoDtoResponse addDtoResponse = response.getBody();

        HttpEntity<EmptyDtoResponse> emptyDtoResponseHttpEntity = template.exchange(
                url + "/" + addDtoResponse.getId(),
                HttpMethod.DELETE,
                request,
                EmptyDtoResponse.class);

        try {
            template.exchange(
                    url + "/" + addDtoResponse.getId(),
                    HttpMethod.GET,
                    request,
                    NoteInfoDtoResponse.class);

            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

    @Test
    public void testDeleteNoteFail() {
        String firstCookie = registerUser("first");

        String secondCookie = registerUser("second");

        int firstSectionId = insertSection("first", firstCookie);

        final String url = "http://localhost:8080/api/notes";

        CreateNoteDtoRequest body = new CreateNoteDtoRequest("subject", "body", firstSectionId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, firstCookie);

        HttpEntity<CreateNoteDtoRequest> request = new HttpEntity<>(body, headers);

        HttpEntity<NoteInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                NoteInfoDtoResponse.class);

        NoteInfoDtoResponse addDtoResponse = response.getBody();


        HttpHeaders deleteHttpHeaders = new HttpHeaders();

        deleteHttpHeaders.add(HttpHeaders.COOKIE, secondCookie);

        deleteHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> updateRequest = new HttpEntity<>(deleteHttpHeaders);

        try {
            template.exchange(
                    url + "/" + addDtoResponse.getId(),
                    HttpMethod.DELETE,
                    updateRequest,
                    NoteInfoDtoResponse.class);

            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

    @Test
    public void testAddRating() {
        String cookie = registerUser("login");

        int sectionId = insertSection("section", cookie);

        int noteId = insertNote("body", cookie, sectionId);

        String secondCookie = registerUser("newLogin");

        final String url = "http://localhost:8080/api/notes/" + noteId + "/rating";

        AddRatingDtoRequest addRatingDtoRequest = new AddRatingDtoRequest(5);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, secondCookie);

        HttpEntity<AddRatingDtoRequest> request = new HttpEntity<>(addRatingDtoRequest, headers);

        HttpEntity<EmptyDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                EmptyDtoResponse.class);

        Assertions.assertNotNull(response);
    }

    @Test
    public void testGetAllNotes() {
        String firstCookie = registerUser("first");
        String secondCookie = registerUser("second");
        String thirdCookie = registerUser("third");

        int sectionId = insertSection("section", firstCookie);

        int noteId1 = insertNote("body", firstCookie, sectionId);
        int noteId2 = insertNote("body", firstCookie, sectionId);
        int noteId3 = insertNote("body", firstCookie, sectionId);
        int noteId4 = insertNote("body", secondCookie, sectionId);
        int noteId5 = insertNote("body", secondCookie, sectionId);
        int noteId6 = insertNote("body", thirdCookie, sectionId);
        int noteId7 = insertNote("body", thirdCookie, sectionId);

        final String url = "http://localhost:8080/api/notes?sortByRating";


        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, secondCookie);

        HttpEntity<String> request = new HttpEntity<>(headers);

        HttpEntity<NoteDtoResponse[]> response = template.exchange(
                url,
                HttpMethod.GET,
                request,
                NoteDtoResponse[].class);

        NoteDtoResponse[] dtoResponses = response.getBody();

        List<Integer> noteIds = new ArrayList<>();

        for (NoteDtoResponse item : dtoResponses) {
            noteIds.add(item.getId());
        }
        List<Integer> expectedIds = List.of(noteId1, noteId2, noteId3, noteId4, noteId5, noteId6, noteId7);

        Assertions.assertEquals(expectedIds, noteIds);
    }

    @Test
    public void testGetNotesBySection() {
        String firstCookie = registerUser("first");
        String secondCookie = registerUser("second");
        String thirdCookie = registerUser("third");

        int sectionId = insertSection("section", firstCookie);
        int secondSectionId = insertSection("first section", firstCookie);

        int noteId1 = insertNote("body", firstCookie, sectionId);
        int noteId2 = insertNote("body", firstCookie, secondSectionId);
        int noteId3 = insertNote("body", firstCookie, sectionId);
        int noteId4 = insertNote("body", secondCookie, secondSectionId);
        int noteId5 = insertNote("body", secondCookie, sectionId);
        int noteId6 = insertNote("body", thirdCookie, secondSectionId);
        int noteId7 = insertNote("body", thirdCookie, sectionId);

        final String url = "http://localhost:8080/api/notes?sectionId=" + secondSectionId;

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, secondCookie);

        HttpEntity<String> request = new HttpEntity<>(headers);

        HttpEntity<NoteDtoResponse[]> response = template.exchange(
                url,
                HttpMethod.GET,
                request,
                NoteDtoResponse[].class);

        NoteDtoResponse[] dtoResponses = response.getBody();

        List<Integer> noteIds = new ArrayList<>();

        for (NoteDtoResponse item : dtoResponses) {
            noteIds.add(item.getId());
        }
        List<Integer> expectedIds = List.of(noteId2, noteId4, noteId6);

        Assertions.assertEquals(expectedIds, noteIds);
    }

    @Test
    public void testGetNotesByDesc() {
        String firstCookie = registerUser("first");
        String secondCookie = registerUser("second");
        String thirdCookie = registerUser("third");

        int sectionId = insertSection("section", firstCookie);
        int secondSectionId = insertSection("first section", firstCookie);

        int noteId1 = insertNote("body", firstCookie, sectionId);
        int noteId2 = insertNote("body", firstCookie, secondSectionId);
        int noteId3 = insertNote("body", firstCookie, sectionId);
        int noteId4 = insertNote("body", secondCookie, secondSectionId);
        int noteId5 = insertNote("body", secondCookie, sectionId);
        int noteId6 = insertNote("body", thirdCookie, secondSectionId);
        int noteId7 = insertNote("body", thirdCookie, sectionId);

        addRating(noteId1, secondCookie, 5);
        addRating(noteId1, thirdCookie, 4);
        addRating(noteId3, thirdCookie, 4);
        addRating(noteId3, secondCookie, 3);
        addRating(noteId2, thirdCookie, 3);
        addRating(noteId2, secondCookie, 3);
        addRating(noteId7, firstCookie, 3);
        addRating(noteId7, secondCookie, 2);
        addRating(noteId6, firstCookie, 2);
        addRating(noteId6, secondCookie, 2);
        addRating(noteId5, thirdCookie, 2);
        addRating(noteId5, firstCookie, 1);
        addRating(noteId4, thirdCookie, 1);
        addRating(noteId4, firstCookie, 1);

        final String url = "http://localhost:8080/api/notes?sortByRating=desc";

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, secondCookie);

        HttpEntity<String> request = new HttpEntity<>(headers);

        HttpEntity<NoteDtoResponse[]> response = template.exchange(
                url,
                HttpMethod.GET,
                request,
                NoteDtoResponse[].class);

        NoteDtoResponse[] dtoResponses = response.getBody();

        List<Integer> noteIds = new ArrayList<>();

        for (NoteDtoResponse item : dtoResponses) {
            noteIds.add(item.getId());
        }

        List<Integer> expectedIds = List.of(noteId1, noteId3, noteId2, noteId7, noteId6, noteId5, noteId4);

        Assertions.assertEquals(expectedIds, noteIds);
    }

    @Test
    public void testGetNotesByAsc() {
        String firstCookie = registerUser("first");
        String secondCookie = registerUser("second");
        String thirdCookie = registerUser("third");

        int sectionId = insertSection("section", firstCookie);
        int secondSectionId = insertSection("first section", firstCookie);

        int noteId1 = insertNote("body", firstCookie, sectionId);
        int noteId2 = insertNote("body", firstCookie, secondSectionId);
        int noteId3 = insertNote("body", firstCookie, sectionId);
        int noteId4 = insertNote("body", secondCookie, secondSectionId);
        int noteId5 = insertNote("body", secondCookie, sectionId);
        int noteId6 = insertNote("body", thirdCookie, secondSectionId);
        int noteId7 = insertNote("body", thirdCookie, sectionId);

        addRating(noteId1, secondCookie, 5);
        addRating(noteId1, thirdCookie, 4);
        addRating(noteId3, thirdCookie, 4);
        addRating(noteId3, secondCookie, 3);
        addRating(noteId2, thirdCookie, 3);
        addRating(noteId2, secondCookie, 3);
        addRating(noteId7, firstCookie, 3);
        addRating(noteId7, secondCookie, 2);
        addRating(noteId6, firstCookie, 2);
        addRating(noteId6, secondCookie, 2);
        addRating(noteId5, thirdCookie, 2);
        addRating(noteId5, firstCookie, 1);
        addRating(noteId4, thirdCookie, 1);
        addRating(noteId4, firstCookie, 1);

        final String url = "http://localhost:8080/api/notes?sortByRating=asc";

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, secondCookie);

        HttpEntity<String> request = new HttpEntity<>(headers);

        HttpEntity<NoteDtoResponse[]> response = template.exchange(
                url,
                HttpMethod.GET,
                request,
                NoteDtoResponse[].class);

        NoteDtoResponse[] dtoResponses = response.getBody();

        List<Integer> noteIds = new ArrayList<>();

        for (NoteDtoResponse item : dtoResponses) {
            noteIds.add(item.getId());
        }

        List<Integer> expectedIds = List.of(noteId4, noteId5, noteId6, noteId7, noteId2, noteId3, noteId1);

        Assertions.assertEquals(expectedIds, noteIds);
    }

    @Test
    public void testGetNotesByAscTop3() {
        String firstCookie = registerUser("first");
        String secondCookie = registerUser("second");
        String thirdCookie = registerUser("third");

        int sectionId = insertSection("section", firstCookie);
        int secondSectionId = insertSection("first section", firstCookie);

        int noteId1 = insertNote("body", firstCookie, sectionId);
        int noteId2 = insertNote("body", firstCookie, secondSectionId);
        int noteId3 = insertNote("body", firstCookie, sectionId);
        int noteId4 = insertNote("body", secondCookie, secondSectionId);
        int noteId5 = insertNote("body", secondCookie, sectionId);
        int noteId6 = insertNote("body", thirdCookie, secondSectionId);
        int noteId7 = insertNote("body", thirdCookie, sectionId);

        addRating(noteId1, secondCookie, 5);
        addRating(noteId1, thirdCookie, 4);
        addRating(noteId3, thirdCookie, 4);
        addRating(noteId3, secondCookie, 3);
        addRating(noteId2, thirdCookie, 3);
        addRating(noteId2, secondCookie, 3);
        addRating(noteId7, firstCookie, 3);
        addRating(noteId7, secondCookie, 2);
        addRating(noteId6, firstCookie, 2);
        addRating(noteId6, secondCookie, 2);
        addRating(noteId5, thirdCookie, 2);
        addRating(noteId5, firstCookie, 1);
        addRating(noteId4, thirdCookie, 1);
        addRating(noteId4, firstCookie, 1);

        final String url = "http://localhost:8080/api/notes?sortByRating=desc&count=3&from=0";

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, secondCookie);

        HttpEntity<String> request = new HttpEntity<>(headers);

        HttpEntity<NoteDtoResponse[]> response = template.exchange(
                url,
                HttpMethod.GET,
                request,
                NoteDtoResponse[].class);

        NoteDtoResponse[] dtoResponses = response.getBody();

        List<Integer> noteIds = new ArrayList<>();

        for (NoteDtoResponse item : dtoResponses) {
            noteIds.add(item.getId());
        }

        List<Integer> expectedIds = List.of(noteId1, noteId3, noteId2);

        Assertions.assertEquals(expectedIds, noteIds);
    }
}
