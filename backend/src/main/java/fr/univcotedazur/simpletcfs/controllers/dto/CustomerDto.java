package fr.univcotedazur.simpletcfs.controllers.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class CustomerDto {

    private String name;
    private String creditCard;

    public CustomerDto(String name, String creditCard) {
        this.name = name;
        this.creditCard = creditCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

}
