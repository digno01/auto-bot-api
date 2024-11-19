package br.com.auto.bot.auth.exceptions.enuns;

import br.com.auto.bot.auth.exceptions.intefaces.MessageCode;

public enum AuthMessageCode implements MessageCode {
    ERROR_EMPTY_FIELD("value.empty", 400),

    ERROR_UNEXPECTED("ME001", 500),
    ERROR_REQUIRED_FIELDS("ME002", 400),
    ERROR_USER_PASSWORD_INVALID("ME003", 400),
    ERROR_TOKEN_INVALID("ME004", 401),
    ERROR_NO_FOUND("ME005", 404),
    ERROR_PROFILE_REGISTERED("ME006", 400),
    ERROR_UNFORMED_ACTION_FUNCTIONALITY("ME007", 400),
    ERROR_PROFILE_CANNOT_BE_DELETED("ME008", 400),
    ERROR_USER_REGISTRED("ME009", 400),
    ERROR_UNFORMED_PROFILE("ME010", 400),
    ERROR_PASSWORD_INVALID("ME011", 400),
    ERROR_CHOOSE_ANOTHER_PASSWORD("ME012", 400),
    ERROR_PASSWORDS_DO_NOT_MATCH("ME013", 400),
    ERROR_WEAK_PASSWORD("ME014", 400),
    ERROR_EMAIL_INVALID("ME015", 400),
    ERROR_PERSON_HAS_USER("ME016", 400),
    ERROR_LOGIN_CPF_INVALID("ME017", 400),
    ERROR_LOGIN_CPF_DO_NOT_MATCH("ME018", 400),
    ERROR_PRIMARY_EMAIL_REQUIRED("ME019", 400),
    ERROR_REQUIRED_FILTER("ME020", 400),
    ERROR_EMAIL_INSERTED("ME021", 400),
    ERROR_REQUIRED_RESPONSE_RECAPTCHA("ME022", 400),
    ERROR_INVALID_RESPONSE_RECAPTCHA("ME023", 400),
    ERROR_LOGIN_PASSWORD_INVALID("ME024", 400),
    ERROR_USER_BLOQUED_ATTEMPTED_ACCESS("ME025", 400),
    ERROR_ID_SECRET_INVALID("ME027", 400),
    ERROR_LOGIN_CNPJ_INVALID("ME028", 400),
    ERROR_USER_UNVALID("ME029", 400),
    NUM_MAX_TENTATIVAS_EXCEDIDAS("ME030", 400),
    SENHA_DEVE_SER_DIFERENTE_ANTERIOR("ME032", 400);

    private final String code;

    private final Integer status;

    /**
     * Constructor of class.
     *
     * @param code
     * @param status
     */
    AuthMessageCode(final String code, final Integer status) {
        this.code = code;
        this.status = status;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return code;
    }
}
