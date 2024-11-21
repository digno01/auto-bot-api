package br.com.auto.bot.auth.mapper;

import br.com.auto.bot.auth.dto.RendimentoDTO;
import br.com.auto.bot.auth.model.Rendimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RendimentoMapper {

    @Mapping(target = "valorRendimento", source = "rendimento.valorRendimento")
    @Mapping(target = "dataRendimento", source = "rendimento.dataRendimento")
    @Mapping(target = "tipoRendimento", source = "rendimento.tipoRendimento.descricao")
    @Mapping(target = "percentualRendimento", source = "rendimento.percentualRendimento")
    @Mapping(target = "nomeRobo", source = "rendimento.investimento.roboInvestidor.nome")
    @Mapping(target = "valorInvestido", source = "rendimento.investimento.valorInvestido")
    @Mapping(target = "diasPeriodo", source = "rendimento.investimento.roboInvestidor.diasPeriodo")
    @Mapping(target = "rendimentoMin", source = "rendimento.investimento.roboInvestidor.percentualRendimentoMin")
    @Mapping(target = "rendimentoMax", source = "rendimento.investimento.roboInvestidor.percentualRendimentoMax")
    @Mapping(target = "isLucro", expression = "java(rendimento.getValorRendimento().compareTo(BigDecimal.ZERO) >= 0)")
    RendimentoDTO toDto(Rendimento rendimento);

    List<RendimentoDTO> toDtoList(List<Rendimento> rendimentos);
}
