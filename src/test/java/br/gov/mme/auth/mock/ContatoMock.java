package br.gov.mme.auth.mock;

import br.gov.mme.auth.dto.ContactDTO;
import br.gov.mme.auth.model.Contact;
import br.gov.mme.auth.model.User;


public class ContatoMock {

    public static Contact getContato(User user){
        Contact entity = new Contact();
        entity.setUser(user);
        entity.setDdd(45);
        entity.setTipo(1);
        entity.setNumero("459924743");
        return entity;
    }

    public static ContactDTO getContatoDTO(){
        ContactDTO dto = new ContactDTO();
        dto.setId(1);
        dto.setDdd(45);
        dto.setTipo(1);
        dto.setNumero("459924743");
        dto.setId(null);
        dto.setRemove(false);
        return dto;
    }
}
