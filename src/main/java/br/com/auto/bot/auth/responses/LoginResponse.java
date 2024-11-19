package br.com.auto.bot.auth.responses;

public class LoginResponse {
    private String token;
    private String refreshToken; // Adicionado campo para o refresh token
    private long expiresIn;

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public String getRefreshToken() { // Método getter para o refresh token
        return refreshToken;
    }

    public LoginResponse setRefreshToken(String refreshToken) { // Método setter para o refresh token
        this.refreshToken = refreshToken;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' + // Adicionado ao toString
                ", expiresIn=" + expiresIn +
                '}';
    }
}
