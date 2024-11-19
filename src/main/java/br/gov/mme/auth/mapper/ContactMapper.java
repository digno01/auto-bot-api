package br.gov.mme.auth.mapper;

import br.gov.mme.auth.dto.ContactDTO;
import br.gov.mme.auth.model.Contact;
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
