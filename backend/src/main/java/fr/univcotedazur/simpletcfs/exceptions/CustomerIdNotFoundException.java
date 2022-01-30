package fr.univcotedazur.simpletcfs.exceptions;

public class CustomerIdNotFoundException extends Exception {

    private String id;

    public CustomerIdNotFoundException(String id) {
        this.id = id;
    }

    public CustomerIdNotFoundException() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
