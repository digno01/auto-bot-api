package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionDto implements Serializable {

    private Long id;

    private String nome;
}
