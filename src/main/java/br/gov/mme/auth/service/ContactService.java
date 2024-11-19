package br.gov.mme.auth.service;

import br.gov.mme.auth.dto.ContactDTO;
import br.gov.mme.auth.generic.service.GenericService;
import br.gov.mme.auth.mapper.ContactMapper;
import br.gov.mme.auth.model.Contact;
import br.gov.mme.auth.model.User;
import br.gov.mme.auth.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService extends GenericService<Contact, Long> {
    @Autowired
    private ContactRepository repository;

    @Autowired
    public ContactService(ContactRepository repository) {

        this.repository = repository;
    }

    @Transactional
    public List<Contact> saveOrUpdate(List<ContactDTO> list, User user) {
        List<Contact> contatosSalvos = new ArrayList<>();
        for (ContactDTO e : list) {
            if (e.isRemove()) {
                Optional<Contact> exists = repository.findByIdAndUserId(Long.valueOf(e.getId()), user.getId());
                if (exists.isPresent()) {
                    deleteById(Long.valueOf(e.getId()));
                }
            } else {
                createOrUpdate(user, contatosSalvos, e);
            }
        }
        return contatosSalvos;
    }


    private void createOrUpdate(User user, List<Contact> contatosSalvo, ContactDTO e) {
        Contact contato = new Contact();
        Optional<Contact> exists = Optional.empty();
        if(e.getId() != null) {
            exists = repository.findByIdAndUserId(Long.valueOf(e.getId()), user.getId());
        }

        if(e.getId() != null && exists.isPresent()){
            contato = ContactMapper.mapper(exists.get(), e);
            this.repository.save(contato);
        } else {
            ContactMapper.mapper(contato, e);
            contato.setUser(user);
            this.repository.save(contato);
        }
        contatosSalvo.add(contato);
    }

    @Transactional
    public Contact deleteById(Long id) {
        Optional<Contact> entityOptional = this.repository.findById(id);
        if (entityOptional.isPresent()) {
            Contact contato = entityOptional.get();
            contato.setIsDeleted(true);
            this.repository.save(contato);
        }
        return null;
    }

}