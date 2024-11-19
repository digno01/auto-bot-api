package br.com.auto.bot.auth.model.error;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageDTO {
    private String message;

    public MessageDTO(String message) {
        this.message = message;
    }

}
