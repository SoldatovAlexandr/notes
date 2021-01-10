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
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class UserDtoMapper {
    public static final UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    @Mapping(target = "registered", expression = "java(java.time.LocalDateTime.now())")
    public abstract User toUser(RegisterUserDtoRequest registerUserDtoRequest);

    public abstract ProfileInfoDtoResponse toProfileInfoDtoResponse(User user);

    @Mapping(source = "newPassword", target = "password")
    public abstract User toUser(UpdateUserDtoRequest updateUserDtoRequest);

    public abstract UpdateUserDtoResponse toUpdateUserDtoResponse(User user);

    public List<ProfileItemDtoResponse> toProfilesItemDtoResponse(List<UserView> users) {
        List<ProfileItemDtoResponse> response = new ArrayList<>();
        for (UserView user : users) {
            response.add(toProfileItemDtoResponse(user));
        }
        return response;
    }

    @Named("toProfileItemDtoResponse")
    @Mapping(source = "registered", target = "timeRegistered", dateFormat = "yyyy-MM-dd HH:mm:ss")
    public abstract ProfileItemDtoResponse toProfileItemDtoResponse(UserView user);

    public List<SuperProfileItemDtoResponse> toSuperProfilesItemDtoResponse(List<UserView> users) {
        List<SuperProfileItemDtoResponse> response = new ArrayList<>();
        for (UserView user : users) {
            response.add(toSuperProfileItemDtoResponse(user));
        }
        return response;
    }

    @Mapping(source = "registered", target = "timeRegistered", dateFormat = "yyyy-MM-dd HH:mm:ss")
    abstract SuperProfileItemDtoResponse toSuperProfileItemDtoResponse(UserView user);
}
