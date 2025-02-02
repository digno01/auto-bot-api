package br.gov.mme.auth.service;

import br.gov.mme.auth.generic.service.GenericService;
import br.gov.mme.auth.model.permissoes.PerfilAcesso;
import br.gov.mme.auth.repository.PerfilAcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerfilAcessoService extends GenericService<PerfilAcesso, Long> {

    @Autowired
    private PerfilAcessoRepository repository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<PerfilAcesso> findAll() {
        return repository.findAll();
    }


}