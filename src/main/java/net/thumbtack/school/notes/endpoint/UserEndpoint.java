package net.thumbtack.school.notes.endpoint;

import net.thumbtack.school.notes.dto.request.*;
import net.thumbtack.school.notes.dto.response.EmptyDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileItemDtoResponse;
import net.thumbtack.school.notes.dto.response.UpdateUserDtoResponse;
import net.thumbtack.school.notes.erroritem.exception.ServerException;
import net.thumbtack.school.notes.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);
    private final UserService userService;
    private final String cookieName = "JAVASESSIONID";

    @Autowired
    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProfileInfoDtoResponse registerUser(@Valid @RequestBody RegisterUserDtoRequest registerUserDtoRequest,
                                               HttpServletResponse httpServletResponse) throws ServerException {
        LOGGER.info("UserEndpoint register user");
        return userService.registerUser(registerUserDtoRequest, httpServletResponse);
    }

    @PostMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse loginUser(@RequestBody LoginUserRequest loginUserRequest,
                                      HttpServletResponse httpServletResponse) throws ServerException {
        LOGGER.info("UserEndpoint login user");
        return userService.loginUser(loginUserRequest, httpServletResponse);
    }

    @DeleteMapping(value = "/sessions")
    public EmptyDtoResponse logoutUser(@CookieValue(value = cookieName) String token) {
        LOGGER.info("UserEndpoint logout user");
        return userService.logoutUser(token);
    }

    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProfileInfoDtoResponse getProfileInfo(@CookieValue(value = cookieName) String token)
            throws ServerException {
        LOGGER.info("UserEndpoint profile info");
        return userService.getProfileInfo(token);
    }

    @DeleteMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse removeUser(@RequestBody PasswordDtoRequest passwordDtoRequest,
                                       @CookieValue(value = cookieName) String token) throws ServerException {
        LOGGER.info("UserEndpoint remove user");
        return userService.removeUser(passwordDtoRequest, token);
    }

    @PutMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateUserDtoResponse updateUser(@Valid @RequestBody UpdateUserDtoRequest updateUserDtoRequest,
                                            @CookieValue(value = cookieName) String token) throws ServerException {
        LOGGER.info("UserEndpoint update user");
        return userService.updateUser(updateUserDtoRequest, token);
    }

    @PutMapping(value = "/accounts/{id}/super")
    public EmptyDtoResponse setSuperUser(@CookieValue(value = cookieName) String token,
                                         @PathVariable("id") int id) throws ServerException {
        LOGGER.info("UserEndpoint set user with id {} superuser", id);
        return userService.setSuperUser(token, id);
    }

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<? extends ProfileItemDtoResponse> getUsers(@CookieValue(value = cookieName) String token,
                                                           @RequestParam(required = false, defaultValue = "") String sortByRating,
                                                           @RequestParam(required = false, defaultValue = "") String type,
                                                           @RequestParam(required = false, defaultValue = "0") int from,
                                                           @RequestParam(required = false, defaultValue = "2147483647") Integer count
    ) throws ServerException {
        return userService.getUsers(sortByRating, type, from, count, token);
    }

    @PostMapping(value = "/followings", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse addFollowing(@Valid @RequestBody FollowingDtoRequest followingDtoRequest,
                                         @CookieValue(value = cookieName) String token
    ) throws ServerException {
        LOGGER.info("UserEndpoint add following");
        return userService.following(followingDtoRequest, token);
    }

    @PostMapping(value = "/ignore", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse addIgnore(@Valid @RequestBody IgnoreDtoRequest ignoreDtoRequest,
                                      @CookieValue(value = cookieName) String token
    ) throws ServerException {
        LOGGER.info("UserEndpoint add ignore");
        return userService.ignore(ignoreDtoRequest, token);
    }

    @DeleteMapping(value = "/followings/{login}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse deleteFollowing(@CookieValue(value = cookieName) String token,
                                            @PathVariable("login") String login) throws ServerException {
        LOGGER.info("UserEndpoint delete following");
        return userService.deleteFollowing(login, token);
    }

    @DeleteMapping(value = "/ignore/{login}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmptyDtoResponse deleteIgnore(@CookieValue(value = cookieName) String token,
                                         @PathVariable("login") String login) throws ServerException {
        LOGGER.info("UserEndpoint delete ignore");
        return userService.deleteIgnore(login, token);
    }
}
