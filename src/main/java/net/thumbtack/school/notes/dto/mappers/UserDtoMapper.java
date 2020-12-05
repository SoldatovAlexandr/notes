package net.thumbtack.school.notes.dto.mappers;

import net.thumbtack.school.notes.dto.request.RegisterUserDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateUserDtoRequest;
import net.thumbtack.school.notes.dto.response.ProfileInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.ProfileItemDtoResponse;
import net.thumbtack.school.notes.dto.response.SuperProfileItemDtoResponse;
import net.thumbtack.school.notes.dto.response.UpdateUserDtoResponse;
import net.thumbtack.school.notes.model.User;
import net.thumbtack.school.notes.views.UserView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class UserDtoMapper {
    public static final UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public User toUser(RegisterUserDtoRequest registerUserDtoRequest) {
        return new User(
                registerUserDtoRequest.getLogin(),
                registerUserDtoRequest.getPassword(),
                registerUserDtoRequest.getFirstName(),
                registerUserDtoRequest.getLastName(),
                registerUserDtoRequest.getPatronymic(),
                LocalDateTime.now());
    }

    public ProfileInfoDtoResponse toProfileInfoDtoResponse(User user) {
        return new ProfileInfoDtoResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getLogin()
        );
    }


    public User toUser(UpdateUserDtoRequest updateUserDtoRequest) {
        return new User(
                updateUserDtoRequest.getNewPassword(),
                updateUserDtoRequest.getFirstName(),
                updateUserDtoRequest.getLastName(),
                updateUserDtoRequest.getPatronymic());
    }

    public UpdateUserDtoResponse toUpdateUserDtoResponse(User user) {
        return new UpdateUserDtoResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getLogin()
        );
    }

    public List<ProfileItemDtoResponse> toProfilesItemDtoResponse(List<UserView> users) {
        List<ProfileItemDtoResponse> response = new ArrayList<>();
        for (UserView user : users) {
            response.add(toProfileItemDtoResponse(user));
        }
        return response;
    }

    private ProfileItemDtoResponse toProfileItemDtoResponse(UserView user) {
        return new ProfileItemDtoResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getLogin(),
                user.getRegistered().format(dateTimeFormatter),
                user.isOnline(),
                user.isDeleted(),
                user.getUserRating()
        );
    }

    public List<SuperProfileItemDtoResponse> toSuperProfilesItemDtoResponse(List<UserView> users) {
        List<SuperProfileItemDtoResponse> response = new ArrayList<>();
        for (UserView user : users) {
            response.add(toSuperProfileItemDtoResponse(user));
        }
        return response;
    }

    private SuperProfileItemDtoResponse toSuperProfileItemDtoResponse(UserView user) {
        return new SuperProfileItemDtoResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getLogin(),
                user.getRegistered().format(dateTimeFormatter),
                user.isOnline(),
                user.isDeleted(),
                user.getUserRating(),
                user.isSuper()
        );
    }
}
