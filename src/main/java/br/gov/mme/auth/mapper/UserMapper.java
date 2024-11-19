package br.gov.mme.auth.mapper;

import br.gov.mme.auth.dto.UserDTO;
import br.gov.mme.auth.model.User;

public class UserMapper {

    public static User map(User user, UserDTO dto) {
        user.setEmail(dto.getEmail());
        user.setNome(dto.getNome());
        return user;
    }
}
