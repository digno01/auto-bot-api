package br.com.auto.bot.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_OPERACAO_CRIPTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperacaoCripto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_OPERACAO_CRIPTO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_RENDIMENTO")
    private Rendimento rendimento;

    @Column(name = "DS_MOEDA")
    private String moeda;  // BTC, ETH, etc.

    @Column(name = "VL_COMPRA")
    private BigDecimal valorCompra;

    @Column(name = "VL_VENDA")
    private BigDecimal valorVenda;

    @Column(name = "QT_MOEDA")
    private BigDecimal quantidadeMoeda;

    @Column(name = "DT_COMPRA")
    private LocalDateTime dataCompra;

    @Column(name = "DT_VENDA")
    private LocalDateTime dataVenda;

    @Column(name = "PC_VARIACAO")
    private BigDecimal percentualVariacao;

    @Column(name = "VL_LUCRO")
    private BigDecimal valorLucro;

    @Column(name = "DS_URL_IMAGEM", length = 500)
    private String urlImagem;
}
