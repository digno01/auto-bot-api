package br.com.auto.bot.auth.mapper;

import br.com.auto.bot.auth.dto.ContactDTO;
import br.com.auto.bot.auth.model.Contact;

public class ContactMapper {

    public static Contact mapper(Contact contato, ContactDTO dto){
        if(contato.getId() != null) {
            contato.setId(contato.getId());
        }
        contato.setDdd(dto.getDdd());
        contato.setNumero(dto.getNumero());
        contato.setTipo(dto.getTipo());
        return contato;
    }
}
