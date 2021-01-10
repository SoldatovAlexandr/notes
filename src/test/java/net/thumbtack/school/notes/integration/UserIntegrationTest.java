package net.thumbtack.school.notes.integration;

import net.thumbtack.school.notes.dto.request.*;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileItemDtoResponse;
import net.thumbtack.school.notes.dto.response.UpdateUserDtoResponse;
import net.thumbtack.school.notes.erroritem.dto.ErrorDtoContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserIntegrationTest extends BaseIntegrationTest {

    private static Stream<Arguments> testValidationRegisterParams() {
        return Stream.of(
                Arguments.of(new RegisterUserDtoRequest(
                        "firstName", "lastName",
                        "patronymic", "login", "password"
                )),
                Arguments.of(new RegisterUserDtoRequest(
                        "firstName", "lastName",
                        "patronymic", "login", null
                )),
                Arguments.of(new RegisterUserDtoRequest(
                        null, "lastName",
                        "patronymic", "login", "password-123"
                )),
                Arguments.of(new RegisterUserDtoRequest(
                        "firstName", null,
                        "patronymic", "login", "password-123"
                )),
                Arguments.of(new RegisterUserDtoRequest(
                        "firstName", "lastName",
                        "patronymic", null, "password-123"
                )),
                Arguments.of(new RegisterUserDtoRequest(
                        "firstName", "lastName",
                        "123", "login", "password-123"
                )),
                Arguments.of(new RegisterUserDtoRequest(
                        "firstName123", "lastName",
                        "patronymic", "login", "password-123"
                )),
                Arguments.of(new RegisterUserDtoRequest(
                        "firstName", "123",
                        "patronymic", "login", "password-123"
                )),
                Arguments.of(new RegisterUserDtoRequest(
                        "._@#", "lastName",
                        "patronymic", "login", "password-123"
                )),
                Arguments.of(new RegisterUserDtoRequest(
                        "firstName", "._@#",
                        "patronymic", "login", "password-123"
                ))
        );
    }

    private static Stream<Arguments> testValidationLoginParams() {
        return Stream.of(
                Arguments.of(new LoginUserRequest(
                        "login", "password"
                )),
                Arguments.of(new LoginUserRequest(
                        null, "password-123"
                )),
                Arguments.of(new LoginUserRequest(
                        "login", null
                ))
        );
    }

    @Test
    public void testRegisterUser() {
        final String url = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest dtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> request = new HttpEntity<>(dtoRequest);

        HttpEntity<ProfileInfoDtoResponse> response = template.exchange(
                url,
                HttpMethod.POST,
                request,
                ProfileInfoDtoResponse.class);

        String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        ProfileInfoDtoResponse expectedResponse = new ProfileInfoDtoResponse(
                "firstName", "lastName", "patronymic", "login");

        assertAll(
                () -> assertNotNull(cookie),
                () -> assertEquals(expectedResponse, response.getBody())
        );
    }

    @Test
    public void testRegisterWithDuplicateLogin() {
        final String url = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest dtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> request = new HttpEntity<>(dtoRequest);

        HttpEntity<ProfileInfoDtoResponse> firstResponse = template.exchange(
                url,
                HttpMethod.POST,
                request,
                ProfileInfoDtoResponse.class);

        try {
            HttpEntity<ProfileInfoDtoResponse> secondResponse = template.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    ProfileInfoDtoResponse.class);
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getStatusCode().value());
        }
    }

    @ParameterizedTest
    @MethodSource
    public void testValidationRegisterParams(RegisterUserDtoRequest registerUserDtoRequest) {
        final String url = "http://localhost:8080/api/accounts";

        HttpEntity<RegisterUserDtoRequest> request = new HttpEntity<>(registerUserDtoRequest);

        try {
            HttpEntity<ProfileInfoDtoResponse> response = template.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    ProfileInfoDtoResponse.class);
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getStatusCode().value());
        }
    }

    @Test
    public void testLoginUser() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest = new HttpEntity<>(registerDtoRequest);

        HttpEntity<ProfileInfoDtoResponse> registerResponse = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest,
                ProfileInfoDtoResponse.class);

        final String loginUrl = "http://localhost:8080/api/sessions";

        LoginUserRequest loginDtoRequest = new LoginUserRequest("login", "password-123");

        HttpEntity<LoginUserRequest> loginRequest = new HttpEntity<>(loginDtoRequest);

        HttpEntity<EmptyDtoResponse> loginResponse = template.exchange(
                loginUrl,
                HttpMethod.POST,
                loginRequest,
                EmptyDtoResponse.class);

        String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        assertNotNull(cookie);
    }

    @Test
    public void testLoginUserFail1() {
        final String loginUrl = "http://localhost:8080/api/sessions";

        LoginUserRequest loginDtoRequest = new LoginUserRequest("login", "password-123");

        HttpEntity<LoginUserRequest> loginRequest = new HttpEntity<>(loginDtoRequest);

        try {
            HttpEntity<EmptyDtoResponse> loginResponse = template.exchange(
                    loginUrl,
                    HttpMethod.POST,
                    loginRequest,
                    EmptyDtoResponse.class);
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getStatusCode().value());
        }
    }

    @Test
    public void testLoginUserFail2() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest = new HttpEntity<>(registerDtoRequest);

        HttpEntity<ProfileInfoDtoResponse> registerResponse = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest,
                ProfileInfoDtoResponse.class);

        final String loginUrl = "http://localhost:8080/api/sessions";

        LoginUserRequest loginDtoRequest = new LoginUserRequest("login", "another-password");

        HttpEntity<LoginUserRequest> loginRequest = new HttpEntity<>(loginDtoRequest);

        try {
            HttpEntity<EmptyDtoResponse> loginResponse = template.exchange(
                    loginUrl,
                    HttpMethod.POST,
                    loginRequest,
                    EmptyDtoResponse.class);
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getStatusCode().value());
        }
    }

    @ParameterizedTest
    @MethodSource
    public void testValidationLoginParams(LoginUserRequest loginUserRequest) {
        final String url = "http://localhost:8080/api/sessions";

        HttpEntity<LoginUserRequest> request = new HttpEntity<>(loginUserRequest);

        try {
            HttpEntity<EmptyDtoResponse> response =
                    template.exchange(url, HttpMethod.POST, request, EmptyDtoResponse.class);
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getStatusCode().value());
        }
    }

    @Test
    public void testLogoutUser() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest = new HttpEntity<>(registerDtoRequest);

        HttpEntity<ProfileInfoDtoResponse> registerResponse = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest,
                ProfileInfoDtoResponse.class);

        String cookie = registerResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        final String logoutUrl = "http://localhost:8080/api/sessions";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<String> logoutRequest = new HttpEntity<>(httpHeaders);

        HttpEntity<EmptyDtoResponse> logoutResponse = template.exchange(
                logoutUrl,
                HttpMethod.DELETE,
                logoutRequest,
                EmptyDtoResponse.class);

        final String getProfileUrl = "http://localhost:8080/api/account";

        HttpEntity<String> profileRequest = new HttpEntity<>(httpHeaders);

        try {
            HttpEntity<ErrorDtoContainer> errorResponse = template.exchange(getProfileUrl, HttpMethod.GET,
                    profileRequest, ErrorDtoContainer.class);
            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

    @Test
    public void testLogoutUserFail() {
        final String logoutUrl = "http://localhost:8080/api/sessions";

        HttpHeaders httpHeaders = new HttpHeaders();

        HttpEntity<String> logoutRequest = new HttpEntity<>(httpHeaders);

        try {
            HttpEntity<EmptyDtoResponse> logoutResponse =
                    template.exchange(logoutUrl, HttpMethod.DELETE, logoutRequest, EmptyDtoResponse.class);

            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

    @Test
    public void testGetProfileInfo() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest = new HttpEntity<>(registerDtoRequest);

        HttpEntity<ProfileInfoDtoResponse> registerResponse = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest,
                ProfileInfoDtoResponse.class);

        String cookie = registerResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, cookie);

        final String getProfileUrl = "http://localhost:8080/api/account";

        HttpEntity<String> profileRequest = new HttpEntity<>(httpHeaders);

        HttpEntity<ProfileInfoDtoResponse> profileResponse = template.exchange(
                getProfileUrl,
                HttpMethod.GET,
                profileRequest,
                ProfileInfoDtoResponse.class);

        ProfileInfoDtoResponse expectedProfileResponse = new ProfileInfoDtoResponse(
                "firstName", "lastName", "patronymic", "login");

        assertEquals(expectedProfileResponse, profileResponse.getBody());
    }

    @Test
    public void testGetProfileInfoFail1() {
        HttpHeaders httpHeaders = new HttpHeaders();

        final String getProfileUrl = "http://localhost:8080/api/account";

        HttpEntity<String> profileRequest = new HttpEntity<>(httpHeaders);

        try {
            HttpEntity<ProfileInfoDtoResponse> profileResponse = template.exchange(
                    getProfileUrl,
                    HttpMethod.GET,
                    profileRequest,
                    ProfileInfoDtoResponse.class);
            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

    @Test
    public void testGetProfileInfoFail2() {
        HttpHeaders httpHeaders = new HttpHeaders();

        final String getProfileUrl = "http://localhost:8080/api/account";

        HttpEntity<String> profileRequest = new HttpEntity<>(httpHeaders);
        httpHeaders.add(HttpHeaders.COOKIE, "JAVASESSIONID=e1761327-4e92-424f-8214-8c6377f9acf0");

        try {
            HttpEntity<ProfileInfoDtoResponse> profileResponse = template.exchange(
                    getProfileUrl,
                    HttpMethod.GET,
                    profileRequest,
                    ProfileInfoDtoResponse.class);
            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

    @Test
    public void testGetUsers() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest = new HttpEntity<>(registerDtoRequest);

        HttpEntity<ProfileInfoDtoResponse> registerResponse = template.exchange(registerUrl,
                HttpMethod.POST,
                registerRequest,
                ProfileInfoDtoResponse.class);

        String cookie = registerResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        final String accountsUrl = "http://localhost:8080/api/accounts";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<String> getUsersRequest = new HttpEntity<>(httpHeaders);

        ResponseEntity<ProfileItemDtoResponse[]> usersResponse = template.exchange(
                accountsUrl,
                HttpMethod.GET,
                getUsersRequest,
                ProfileItemDtoResponse[].class);

        Assertions.assertNotNull(usersResponse.getBody());
    }

    @Test
    public void testAddFollowing() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest1 = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login1", "password-123"
        );

        RegisterUserDtoRequest registerDtoRequest2 = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login2", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest1 = new HttpEntity<>(registerDtoRequest1);

        HttpEntity<RegisterUserDtoRequest> registerRequest2 = new HttpEntity<>(registerDtoRequest2);

        HttpEntity<ProfileInfoDtoResponse> registerResponse1 = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest1,
                ProfileInfoDtoResponse.class);

        HttpEntity<ProfileInfoDtoResponse> registerResponse2 = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest2,
                ProfileInfoDtoResponse.class);

        String cookie = registerResponse1.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        final String followingsUrl = "http://localhost:8080/api/followings";

        FollowingDtoRequest followingDtoRequest = new FollowingDtoRequest("login2");

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<FollowingDtoRequest> followingRequest = new HttpEntity<>(followingDtoRequest, httpHeaders);

        ResponseEntity<EmptyDtoResponse> emptyResponse = template.exchange(
                followingsUrl,
                HttpMethod.POST,
                followingRequest,
                EmptyDtoResponse.class);

        Assertions.assertNotNull(emptyResponse.getBody());
    }

    @Test
    public void testAddIgnore() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest1 = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login1", "password-123"
        );

        RegisterUserDtoRequest registerDtoRequest2 = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login2", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest1 = new HttpEntity<>(registerDtoRequest1);

        HttpEntity<RegisterUserDtoRequest> registerRequest2 = new HttpEntity<>(registerDtoRequest2);

        HttpEntity<ProfileInfoDtoResponse> registerResponse1 = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest1,
                ProfileInfoDtoResponse.class);

        HttpEntity<ProfileInfoDtoResponse> registerResponse2 = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest2,
                ProfileInfoDtoResponse.class);

        String cookie = registerResponse1.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        final String followingsUrl = "http://localhost:8080/api/ignore";

        IgnoreDtoRequest ignoreDtoRequest = new IgnoreDtoRequest("login2");

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<IgnoreDtoRequest> followingRequest = new HttpEntity<>(ignoreDtoRequest, httpHeaders);

        ResponseEntity<EmptyDtoResponse> emptyResponse = template.exchange(
                followingsUrl,
                HttpMethod.POST,
                followingRequest,
                EmptyDtoResponse.class);

        Assertions.assertNotNull(emptyResponse.getBody());
    }

    @Test
    public void testAddAndDeleteFollowing() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest1 = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login1", "password-123"
        );

        RegisterUserDtoRequest registerDtoRequest2 = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login2", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest1 = new HttpEntity<>(registerDtoRequest1);

        HttpEntity<RegisterUserDtoRequest> registerRequest2 = new HttpEntity<>(registerDtoRequest2);

        HttpEntity<ProfileInfoDtoResponse> registerResponse1 = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest1,
                ProfileInfoDtoResponse.class);

        HttpEntity<ProfileInfoDtoResponse> registerResponse2 = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest2,
                ProfileInfoDtoResponse.class);

        String cookie = registerResponse1.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        final String followingsUrl = "http://localhost:8080/api/followings";

        FollowingDtoRequest followingDtoRequest = new FollowingDtoRequest("login2");

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<FollowingDtoRequest> followingRequest = new HttpEntity<>(followingDtoRequest, httpHeaders);

        ResponseEntity<EmptyDtoResponse> emptyResponse = template.exchange(
                followingsUrl,
                HttpMethod.POST,
                followingRequest,
                EmptyDtoResponse.class);

        final String deleteFollowingsUrl = "http://localhost:8080/api/followings/login2";

        HttpHeaders deleteHttpHeaders = new HttpHeaders();

        deleteHttpHeaders.add(HttpHeaders.COOKIE, cookie);

        deleteHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> deleteFollowingRequest = new HttpEntity<>(deleteHttpHeaders);

        ResponseEntity<EmptyDtoResponse> deleteResponse = template.exchange(
                deleteFollowingsUrl,
                HttpMethod.DELETE,
                deleteFollowingRequest,
                EmptyDtoResponse.class);

        Assertions.assertNotNull(deleteResponse.getBody());
    }

    @Test
    public void testAddAndDeleteIgnore() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest1 = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login1", "password-123"
        );

        RegisterUserDtoRequest registerDtoRequest2 = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login2", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest1 = new HttpEntity<>(registerDtoRequest1);

        HttpEntity<RegisterUserDtoRequest> registerRequest2 = new HttpEntity<>(registerDtoRequest2);

        HttpEntity<ProfileInfoDtoResponse> registerResponse1 = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest1,
                ProfileInfoDtoResponse.class);

        HttpEntity<ProfileInfoDtoResponse> registerResponse2 = template.exchange(
                registerUrl,
                HttpMethod.POST,
                registerRequest2,
                ProfileInfoDtoResponse.class);

        String cookie = registerResponse1.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        final String followingsUrl = "http://localhost:8080/api/ignore";

        IgnoreDtoRequest ignoreDtoRequest = new IgnoreDtoRequest("login2");

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<IgnoreDtoRequest> followingRequest = new HttpEntity<>(ignoreDtoRequest, httpHeaders);

        ResponseEntity<EmptyDtoResponse> emptyResponse = template.exchange(
                followingsUrl,
                HttpMethod.POST,
                followingRequest,
                EmptyDtoResponse.class);

        final String deleteIgnoreUrl = "http://localhost:8080/api/ignore/login2";

        HttpHeaders deleteHttpHeaders = new HttpHeaders();

        deleteHttpHeaders.add(HttpHeaders.COOKIE, cookie);

        deleteHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> deleteFollowingRequest = new HttpEntity<>(deleteHttpHeaders);

        ResponseEntity<EmptyDtoResponse> deleteResponse = template.exchange(
                deleteIgnoreUrl,
                HttpMethod.DELETE,
                deleteFollowingRequest,
                EmptyDtoResponse.class);

        Assertions.assertNotNull(deleteResponse.getBody());
    }

    @Test
    public void testRemoveUser() {
        String cookie = registerUser("userLogin");

        final String url = "http://localhost:8080/api/accounts";

        PasswordDtoRequest passwordDtoRequest = new PasswordDtoRequest("password-123");

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<PasswordDtoRequest> request = new HttpEntity<>(passwordDtoRequest, headers);

        HttpEntity<EmptyDtoResponse> response = template.exchange(
                url,
                HttpMethod.DELETE,
                request,
                EmptyDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateUser() {
        String cookie = registerUser("userLogin");

        final String url = "http://localhost:8080/api/accounts";

        UpdateUserDtoRequest updateUserDtoRequest = new UpdateUserDtoRequest(
                "newFirstName",
                "newLastName",
                "newPatronymic",
                "password-123",
                "new-password-123");

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<UpdateUserDtoRequest> request = new HttpEntity<>(updateUserDtoRequest, headers);

        HttpEntity<UpdateUserDtoResponse> response = template.exchange(
                url,
                HttpMethod.PUT,
                request,
                UpdateUserDtoResponse.class);

        UpdateUserDtoResponse dtoResponse = response.getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals("newFirstName", dtoResponse.getFirstName()),
                () -> Assertions.assertEquals("newLastName", dtoResponse.getLastName()),
                () -> Assertions.assertEquals("newPatronymic", dtoResponse.getPatronymic())
        );

    }


}
