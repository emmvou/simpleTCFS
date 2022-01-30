package fr.univcotedazur.simpletcfs.controllers.dto;

public class CustomerDto {

    private String id; // expected to be empty when POSTing the creation of Customer, and containing the UUID when returned
    private String name;
    private String creditCard;

    public CustomerDto(String id, String name, String creditCard) {
        this.id = id;
        this.name = name;
        this.creditCard = creditCard;
    }

    public String getId() {
        return id;
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
