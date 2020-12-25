package net.thumbtack.school.notes.integration;

import net.thumbtack.school.notes.dto.request.LoginUserRequest;
import net.thumbtack.school.notes.dto.request.RegisterUserDtoRequest;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileInfoDtoResponse;
import net.thumbtack.school.notes.erroritem.dto.ErrorDtoContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testRegisterUser() {
        final String url = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest dtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> request = new HttpEntity<>(dtoRequest);

        HttpEntity<ProfileInfoDtoResponse> response =
                template.exchange(url, HttpMethod.POST, request, ProfileInfoDtoResponse.class);

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

        HttpEntity<ProfileInfoDtoResponse> firstResponse =
                template.exchange(url, HttpMethod.POST, request, ProfileInfoDtoResponse.class);

        try {
            HttpEntity<ProfileInfoDtoResponse> secondResponse =
                    template.exchange(url, HttpMethod.POST, request, ProfileInfoDtoResponse.class);
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
            HttpEntity<ProfileInfoDtoResponse> response =
                    template.exchange(url, HttpMethod.POST, request, ProfileInfoDtoResponse.class);
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getStatusCode().value());
        }
    }

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

    @Test
    public void testLoginUser() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest = new HttpEntity<>(registerDtoRequest);

        HttpEntity<ProfileInfoDtoResponse> registerResponse =
                template.exchange(registerUrl, HttpMethod.POST, registerRequest, ProfileInfoDtoResponse.class);

        final String loginUrl = "http://localhost:8080/api/sessions";

        LoginUserRequest loginDtoRequest = new LoginUserRequest("login", "password-123");

        HttpEntity<LoginUserRequest> loginRequest = new HttpEntity<>(loginDtoRequest);

        HttpEntity<EmptyDtoResponse> loginResponse =
                template.exchange(loginUrl, HttpMethod.POST, loginRequest, EmptyDtoResponse.class);

        String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        assertNotNull(cookie);
    }

    @Test
    public void testLoginUserFail1() {
        final String loginUrl = "http://localhost:8080/api/sessions";

        LoginUserRequest loginDtoRequest = new LoginUserRequest("login", "password-123");

        HttpEntity<LoginUserRequest> loginRequest = new HttpEntity<>(loginDtoRequest);

        try {
            HttpEntity<EmptyDtoResponse> loginResponse =
                    template.exchange(loginUrl, HttpMethod.POST, loginRequest, EmptyDtoResponse.class);
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

        HttpEntity<ProfileInfoDtoResponse> registerResponse =
                template.exchange(registerUrl, HttpMethod.POST, registerRequest, ProfileInfoDtoResponse.class);

        final String loginUrl = "http://localhost:8080/api/sessions";

        LoginUserRequest loginDtoRequest = new LoginUserRequest("login", "another-password");

        HttpEntity<LoginUserRequest> loginRequest = new HttpEntity<>(loginDtoRequest);

        try {
            HttpEntity<EmptyDtoResponse> loginResponse =
                    template.exchange(loginUrl, HttpMethod.POST, loginRequest, EmptyDtoResponse.class);
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
    public void testLogoutUser() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest = new HttpEntity<>(registerDtoRequest);

        HttpEntity<ProfileInfoDtoResponse> registerResponse =
                template.exchange(registerUrl, HttpMethod.POST, registerRequest, ProfileInfoDtoResponse.class);

        String cookie = registerResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        final String logoutUrl = "http://localhost:8080/api/sessions";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, cookie);

        HttpEntity<String> logoutRequest = new HttpEntity<>(httpHeaders);

        HttpEntity<EmptyDtoResponse> logoutResponse =
                template.exchange(logoutUrl, HttpMethod.DELETE, logoutRequest, EmptyDtoResponse.class);

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
    public void getProfileInfo() {
        final String registerUrl = "http://localhost:8080/api/accounts";

        RegisterUserDtoRequest registerDtoRequest = new RegisterUserDtoRequest(
                "firstName", "lastName", "patronymic", "login", "password-123"
        );

        HttpEntity<RegisterUserDtoRequest> registerRequest = new HttpEntity<>(registerDtoRequest);

        HttpEntity<ProfileInfoDtoResponse> registerResponse =
                template.exchange(registerUrl, HttpMethod.POST, registerRequest, ProfileInfoDtoResponse.class);

        String cookie = registerResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, cookie);

        final String getProfileUrl = "http://localhost:8080/api/account";

        HttpEntity<String> profileRequest = new HttpEntity<>(httpHeaders);

        HttpEntity<ProfileInfoDtoResponse> profileResponse = template.exchange(getProfileUrl, HttpMethod.GET,
                profileRequest, ProfileInfoDtoResponse.class);

        ProfileInfoDtoResponse expectedProfileResponse = new ProfileInfoDtoResponse(
                "firstName", "lastName", "patronymic", "login");

        assertEquals(expectedProfileResponse, profileResponse.getBody());
    }

    @Test
    public void getProfileInfoFail1() {
        HttpHeaders httpHeaders = new HttpHeaders();

        final String getProfileUrl = "http://localhost:8080/api/account";

        HttpEntity<String> profileRequest = new HttpEntity<>(httpHeaders);

        try {
            HttpEntity<ProfileInfoDtoResponse> profileResponse = template.exchange(getProfileUrl, HttpMethod.GET,
                    profileRequest, ProfileInfoDtoResponse.class);
            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

    @Test
    public void getProfileInfoFail2() {
        HttpHeaders httpHeaders = new HttpHeaders();

        final String getProfileUrl = "http://localhost:8080/api/account";

        HttpEntity<String> profileRequest = new HttpEntity<>(httpHeaders);
        httpHeaders.add(HttpHeaders.COOKIE, "JAVASESSIONID=e1761327-4e92-424f-8214-8c6377f9acf0");

        try {
            HttpEntity<ProfileInfoDtoResponse> profileResponse = template.exchange(getProfileUrl, HttpMethod.GET,
                    profileRequest, ProfileInfoDtoResponse.class);
            fail();
        } catch (HttpClientErrorException exc) {
            assertEquals(400, exc.getStatusCode().value());
        }
    }

}
