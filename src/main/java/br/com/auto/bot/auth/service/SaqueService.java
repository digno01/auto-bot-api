package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.SaqueResponseDTO;
import br.com.auto.bot.auth.enums.StatusInvestimento;
import br.com.auto.bot.auth.enums.StatusSaque;
import br.com.auto.bot.auth.enums.TipoNotificacao;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.Saque;
import br.com.auto.bot.auth.repository.InvestimentoRepository;
import br.com.auto.bot.auth.repository.SaqueRepository;
import br.com.auto.bot.auth.dto.SaqueRequestDTO;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SaqueService {

    @Autowired
    private InvestimentoRepository investimentoRepository;

    @Autowired
    private SaqueRepository saqueRepository;
    @Autowired
    private NotificacaoUsuarioService notificacaoUsuarioService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Saque perepararSaque(SaqueRequestDTO request, Long usuarioId) {

        Investimento investimento =  investimentoRepository.findInvestimentoAtivoByUsuarioId(request.getInvestimentoId(), ObterDadosUsuarioLogado.getUsuarioLogadoId());
        if(investimento == null){
            throw new BusinessException("Investimento não encontrado");
        }

        // Verificando se o saldo disponível é suficiente
        if (request.getValorSaque().compareTo(investimento.getSaldoAtual()) > 0) {
            throw new BusinessException("Saldo insuficiente para realizar o saque.");
        }
        try{

            // Criando a solicitação de saque
            Saque saque = new Saque();
            saque.setUsuario(investimento.getUsuario());
            saque.setInvestimento(investimento);
            saque.setValorSaque(request.getValorSaque());
            saque.setStatus(StatusSaque.P); // Status Pendente
            saqueRepository.save(saque);
            investimento.setStatus(StatusInvestimento.SL);
            investimentoRepository.save(investimento);

            notificacaoUsuarioService.criarNotificacao(
                    investimento.getUsuario(),
                    "Saque Solicitado",
                    "Solicitado saque no valor de  R$ " + saque.getValorSaque() + " para o investimento no " + investimento.getRoboInvestidor().getNome(),
                    saque.getValorSaque(),
                    TipoNotificacao.SAQUE_SOLICITADO
            );
            return saque;
        }catch (Exception e ){
            throw new BusinessException("Erro ao processar saque.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Saque save(Saque saque) {
        return saqueRepository.save(saque);
    }

    public List<SaqueResponseDTO> listarSolicitacoesSaque(Long usuarioId) {
        List<Saque> saques = saqueRepository.findByUsuarioId(usuarioId);

        return saques.stream()
                .map(saque -> new SaqueResponseDTO(
                        saque.getInvestimento().getRoboInvestidor().getNome(),
                        saque.getValorSaque(),
                        saque.getStatus().getDescricao(),
                        saque.getDataSolicitacao(),
                        saque.getDataProcessamento()
                ))
                .collect(Collectors.toList());
    }


    public Page<SaqueResponseDTO> findAllPendentes(Pageable pageable) {
        Page<Saque> saques = saqueRepository.findAllByStatus(StatusSaque.P, pageable);
        return saques.map(SaqueResponseDTO::fromEntity);
    }
}
