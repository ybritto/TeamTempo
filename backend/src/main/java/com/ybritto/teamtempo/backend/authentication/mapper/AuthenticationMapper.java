package com.ybritto.teamtempo.backend.authentication.mapper;


import com.ybritto.teamtempo.backend.authentication.entity.SecurityRoleEnum;
import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.gen.model.LoginResponseDto;
import com.ybritto.teamtempo.backend.gen.model.RegisterUserDto;
import com.ybritto.teamtempo.backend.gen.model.SecurityRoleEnumDto;
import com.ybritto.teamtempo.backend.gen.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface AuthenticationMapper {

    @Mapping(target = "token", source = "token")
    @Mapping(target = "expiresIn", source = "expirationTime")
    LoginResponseDto mapDto(UserEntity loginUserDto, String token, long expirationTime);

    default SecurityRoleEnumDto getSecurityRoleEnum(SecurityRoleEnum securityRoleEnum) {
        if (securityRoleEnum == null)
            return null;

        return SecurityRoleEnumDto.fromValue(securityRoleEnum.name());
    }

    @Mapping(target = "password", source = "password")
    UserEntity mapEntity(RegisterUserDto registerUserDto, String password);

    UserDto mapDto(UserEntity userSaved);
}
