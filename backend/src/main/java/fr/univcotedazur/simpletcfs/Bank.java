package fr.univcotedazur.simpletcfs;

import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.exceptions.PaymentException;

public interface Bank {

    boolean pay(Customer customer, double value) throws PaymentException;
}
