package fr.univcotedazur.simpletcfs;

import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.exceptions.NegativeQuantityException;
import fr.univcotedazur.simpletcfs.exceptions.PaymentException;

import java.util.Set;

public interface CartModifier {

    int update(Customer retrieveCustomer, Item it) throws NegativeQuantityException;

}
