package fr.univcotedazur.simpletcfs;

import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.exceptions.PaymentException;

import java.util.Set;

public interface CartModifier {

    boolean add(Customer c, Item item);

    boolean remove(Customer c, Item item);

    Set<Item> contents(Customer c);

    String validate(Customer c) throws PaymentException;

}
