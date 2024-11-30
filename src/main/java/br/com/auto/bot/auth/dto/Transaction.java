package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction implements Serializable {
    private String id;
    private String user_id;
    private String amount;
    private String paymentMethod;
    private String card_id;
    private String card_hash;
    private String card_number;
    private String card_holderName;
    private String card_expirationMonth;
    private String card_expirationYear;
    private String card_cvv;
    private String installments;
    private String customer_id;
    private String customer_name;
    private String customer_email;
    private String customer_document_number;
    private String customer_document_type;
    private String customer_phone;
    private String customer_externalRef;
    private String shipping_fee;
    private String shipping_street;
    private String shipping_streetNumber;
    private String shipping_complement;
    private String shipping_zipCode;
    private String shipping_neighborhood;
    private String shipping_city;
    private String shipping_state;
    private String shipping_country;
    private String item_title;
    private String item_unitPrice;
    private String item_quantity;
    private String item_tangible;
    private String item_externalRef;
    private String boleto_expiresInDays;
    private String pix_expiresInDays;
    private String postbackUrl;
    private String metadata;
    private String traceable;
    private String ip;
    private String split_recipientId;
    private String split_amount;
    private String split_chargeProcessingFee;
    private String status;
    private String idqrcode;
    private String created_at;
    private String updated_at;
    private String split_to;
    private String percent_split;
    private String pix_payload;
    private String pix_image_base64;
}