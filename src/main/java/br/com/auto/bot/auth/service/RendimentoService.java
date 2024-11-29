package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.RendimentoDTO;
import br.com.auto.bot.auth.exceptions.BussinessException;
import br.com.auto.bot.auth.mapper.RendimentoMapper;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.Rendimento;
import br.com.auto.bot.auth.repository.InvestimentoRepository;
import br.com.auto.bot.auth.repository.RendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RendimentoService {
    private final RendimentoRepository rendimentoRepository;
    private final InvestimentoRepository investimentoRepository;
    private final RendimentoMapper rendimentoMapper;

    @Autowired
    public RendimentoService(RendimentoRepository rendimentoRepository,
                             InvestimentoRepository investimentoRepository,
                             RendimentoMapper rendimentoMapper) {
        this.rendimentoRepository = rendimentoRepository;
        this.investimentoRepository = investimentoRepository;
        this.rendimentoMapper = rendimentoMapper;
    }

    public List<RendimentoDTO> buscarRendimentosPorPeriodo(Long investimentoId, Integer dias) {
        // Validar número de dias
        if (dias < 10 || dias > 500) {
            throw new IllegalArgumentException("O período deve ser entre 10 e 500 dias");
        }

        // Buscar investimento
        Investimento investimento = investimentoRepository.findById(investimentoId)
                .orElseThrow(() -> new BussinessException("Investimento não encontrado"));

        // Calcular período
        LocalDateTime dataInicial = investimento.getDataInvestimento();
        LocalDateTime dataFinal = dataInicial.plusDays(dias);

        // Buscar rendimentos
        List<Rendimento> rendimentos = rendimentoRepository.findByInvestimentoAndDataRendimentoBetweenOrderByDataRendimentoDesc(
                investimento,
                dataInicial,
                dataFinal
        );

        // Converter para DTO
        return rendimentos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RendimentoDTO convertToDTO(Rendimento rendimento) {
        return RendimentoDTO.builder()
                .id(rendimento.getId())
                .valorRendimento(rendimento.getValorRendimento())
                .dataRendimento(rendimento.getDataRendimento())
                .tipoRendimento(rendimento.getTipoRendimento().getDescricao())
                .percentualRendimento(rendimento.getPercentualRendimento())
                .tipoResultado(rendimento.getTipoResultado())
                .build();
    }
}