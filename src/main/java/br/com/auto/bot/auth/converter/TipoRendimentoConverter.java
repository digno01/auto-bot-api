package br.com.auto.bot.auth.converter;

import br.com.auto.bot.auth.enums.TipoRendimento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TipoRendimentoConverter implements AttributeConverter<TipoRendimento, String> {

    @Override
    public String convertToDatabaseColumn(TipoRendimento tipoRendimento) {
        if (tipoRendimento == null) {
            return null;
        }
        return tipoRendimento.getCodigo();
    }

    @Override
    public TipoRendimento convertToEntityAttribute(String codigo) {
        if (codigo == null) {
            return null;
        }

        for (TipoRendimento tipo : TipoRendimento.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Código de tipo de rendimento inválido: " + codigo);
    }
}
