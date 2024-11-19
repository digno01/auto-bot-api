package br.com.auto.bot.auth.exceptions.intefaces;

import br.com.auto.bot.auth.util.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageResponse implements Serializable {

    private static final long serialVersionUID = 4878825827657916191L;

    @Schema(description = "Código da Mensagem")
    private String code;

    @Schema(description = "Status HTTP")
    private Integer status;

    @Schema(description = "Descrição do erro HTTP")
    private String error;

    @Schema(description = "Mensagem de negócio")
    private String message;

    @Schema(description = "Parâmetros da mensagem")
    private Object[] parameters;

    @Schema(description = "Atributos de validação")
    private List<FieldResponse> attributes;

    /**
     * Adiciona uma instância de {@link FieldResponse}.
     *
     * @param field
     * @return
     */
    public MessageResponse addAttribute(final FieldResponse field) {
        if (CollectionUtil.isEmpty(attributes)) {
            attributes = new ArrayList<FieldResponse>();
        }
        attributes.add(field);
        return this;
    }

    /**
     * Verifica se existem campos com código de mensagem.
     *
     * @return
     */
    public boolean hasFieldCode() {
        boolean hasField = Boolean.FALSE;

        if (!CollectionUtil.isEmpty(attributes)) {
            hasField = attributes.stream().anyMatch(field -> field.getCode() != null);
        }
        return hasField;
    }

    /**
     * @return o código
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code o código a ser definido
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return o status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status o status a ser definido
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return o erro
     */
    public String getError() {
        return error;
    }

    /**
     * @param error o erro a ser definido
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return a mensagem
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message a mensagem a ser definida
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return os parâmetros
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * @param parameters os parâmetros a serem definidos
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    /**
     * @return os atributos
     */
    public List<FieldResponse> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes os atributos a serem definidos
     */
    public void setAttributes(List<FieldResponse> attributes) {
        this.attributes = attributes;
    }
}
