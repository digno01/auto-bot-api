package br.com.auto.bot.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaymentRequestDTO {
    private Customer customer;
    private Integer amount;
    private String paymentMethod;
    private List<Item> items;
    private String splitTo;
    private Integer percentSplit;
    private String postbackUrl;

    @Data
    public static class Customer {
        private Document document;
        private String name;
        private String email;
        private String phone;
    }

    @Data
    public static class Document {
        private String type;
        private String number;
    }

    @Data
    public static class Item {
        private Boolean tangible;
        private String title;
        private Integer unitPrice;
        private Integer quantity;
    }
}
