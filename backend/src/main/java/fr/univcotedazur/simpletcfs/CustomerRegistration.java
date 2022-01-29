package fr.univcotedazur.simpletcfs;

import fr.univcotedazur.simpletcfs.exceptions.AlreadyExistingCustomerException;

public interface CustomerRegistration {

    void register(String name, String creditCard)
            throws AlreadyExistingCustomerException;
}
