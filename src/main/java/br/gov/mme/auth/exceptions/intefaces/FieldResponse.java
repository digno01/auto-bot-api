package br.gov.mme.auth.exceptions.intefaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldResponse implements Serializable {

    private static final long serialVersionUID = -807504480597471148L;

    @JsonIgnore
    @Schema(hidden = true)
    private MessageCode code;

    @Schema(description = "Name do atributo")
    private String attribute;

    @Schema(description = "Descrição da validação")
    private String description;

    /**
     * Constructor of class.
     */
    public FieldResponse() {
    }

    /**
     * Constructor of class.
     *
     * @param attribute
     * @param description
     */
    public FieldResponse(final String attribute, final String description) {
        this.attribute = attribute;
        this.description = description;
    }

    /**
     * Constructor of class.
     *
     * @param attribute
     * @param code
     */
    public FieldResponse(final String attribute, final MessageCode code) {
        this.attribute = attribute;
        this.code = code;
    }

    /**
     * @return the attribute
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the code
     */
    public MessageCode getCode() {
        return code;
    }
}
