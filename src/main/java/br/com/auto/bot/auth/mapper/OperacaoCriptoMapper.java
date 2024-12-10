package br.com.auto.bot.auth.mapper;

import br.com.auto.bot.auth.dto.OperacaoCriptoDTO;
import br.com.auto.bot.auth.dto.RendimentoDTO;
import br.com.auto.bot.auth.model.OperacaoCripto;
import br.com.auto.bot.auth.model.Rendimento;
import org.springframework.stereotype.Component;

@Component
public class OperacaoCriptoMapper {

    public OperacaoCriptoDTO toDTO(OperacaoCripto operacao) {
        if (operacao == null) {
            return null;
        }

        OperacaoCriptoDTO dto = new OperacaoCriptoDTO();
        dto.setId(operacao.getId());
        dto.setMoeda(operacao.getMoeda());
        dto.setValorCompra(operacao.getValorCompra());
        dto.setValorVenda(operacao.getValorVenda());
        dto.setQuantidadeMoeda(operacao.getQuantidadeMoeda());
        dto.setDataCompra(operacao.getDataCompra());
        dto.setDataVenda(operacao.getDataVenda());
        dto.setPercentualVariacao(operacao.getPercentualVariacao());
        dto.setValorLucro(operacao.getValorLucro());
        dto.setUrlImagem(operacao.getUrlImagem());

        if (operacao.getRendimento() != null) {
            dto.setRendimento(toRendimentoDTO(operacao.getRendimento()));
        }

        return dto;
    }

    private RendimentoDTO toRendimentoDTO(Rendimento rendimento) {
        return RendimentoDTO.builder()
                .id(rendimento.getId())
                .valorRendimento(rendimento.getValorRendimento())
                .dataRendimento(rendimento.getDataRendimento())
                .tipoRendimento(rendimento.getTipoRendimento().name())
                .percentualRendimento(rendimento.getPercentualRendimento())
                .tipoResultado(rendimento.getTipoResultado())
                .valorAcumulado(rendimento.getValorAcumulado())
                .build();
    }
}