package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.SaqueResponseDTO;
import br.com.auto.bot.auth.enums.StatusSaque;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.Saque;
import br.com.auto.bot.auth.repository.InvestimentoRepository;
import br.com.auto.bot.auth.repository.SaqueRepository;
import br.com.auto.bot.auth.dto.SaqueRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SaqueService {

    @Autowired
    private InvestimentoRepository investimentoRepository;

    @Autowired
    private SaqueRepository saqueRepository;

    public Saque solicitarSaque(SaqueRequestDTO request, Long usuarioId) {
        Investimento investimento = investimentoRepository.findById(request.getInvestimentoId())
                .orElseThrow(() -> new RuntimeException("Investimento não encontrado"));

        // Verificando se o usuário é o proprietário do investimento
        if (!investimento.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não autorizado a realizar o saque deste investimento.");
        }

        // Verificando se o saldo disponível é suficiente
        if (request.getValorSaque().compareTo(investimento.getSaldoAtual()) > 0) {
            throw new RuntimeException("Saldo insuficiente para realizar o saque.");
        }

        // Criando a solicitação de saque
        Saque saque = new Saque();
        saque.setUsuario(investimento.getUsuario());
        saque.setInvestimento(investimento);
        saque.setValorSaque(request.getValorSaque());
        saque.setStatus(StatusSaque.P); // Status Pendente
        saqueRepository.save(saque);

        return saque;
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
}
