package br.com.auto.bot.auth.converter;

import br.com.auto.bot.auth.enums.TipoResultado;
import br.com.auto.bot.auth.exceptions.BusinessException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TipoResultadoConverter implements AttributeConverter<TipoResultado, String> {

    @Override
    public String convertToDatabaseColumn(TipoResultado tipoResultado) {
        if (tipoResultado == null) {
            return null;
        }
        return tipoResultado.getCodigo();
    }

    @Override
    public TipoResultado convertToEntityAttribute(String codigo) {
        if (codigo == null) {
            return null;
        }

        for (TipoResultado tipo : TipoResultado.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }

        throw new BusinessException("Código de tipo de resultado inválido: " + codigo);
    }
}
