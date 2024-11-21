
package br.com.auto.bot.auth.mapper;


import br.com.auto.bot.auth.dto.UserSessionDto;
import br.com.auto.bot.auth.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserSessionMapper{

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    UserSessionDto toSessionDto(User user);

    List<UserSessionDto> toSessionDtoList(List<User> users);


    @InheritInverseConfiguration
    User toEntity(UserSessionDto dto);
}


