package br.com.auto.bot.auth.mapper;

import br.com.auto.bot.auth.dto.UserDTO;
import br.com.auto.bot.auth.model.User;

public class UserMapper {

    public static User map(User user, UserDTO dto) {
        user.setEmail(dto.getEmail());
        user.setNome(dto.getNome());
        return user;
    }
}
