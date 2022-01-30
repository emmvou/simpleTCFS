package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.CartModifier;
import fr.univcotedazur.simpletcfs.CartProcessor;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.exceptions.EmptyCartException;
import fr.univcotedazur.simpletcfs.exceptions.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class Cart implements CartModifier, CartProcessor {

    @Autowired
    private InMemoryDatabase memory;

    @Override
    public boolean add(Customer c, Item item) {
        memory.getCarts().put(c, updateCart(c, item));
        return true;
    }

    @Override
    public final boolean remove(Customer c, Item item) {
        return add(c, new Item(item.getCookie(), -item.getQuantity()));
    }

    @Override
    public Set<Item> contents(Customer c) {
        return memory.getCarts().getOrDefault(c, new HashSet<Item>());
    }

    @Override
    public double price(Customer c) {
        double result = 0.0;
        for (Item item : contents(c)) {
            result += (item.getQuantity() * item.getCookie().getPrice());
        }
        return result;
    }

    @Override
    public String validate(Customer c) throws PaymentException, EmptyCartException {
        if (contents(c).isEmpty())
            throw new EmptyCartException(c.getName());
        // should ask a cashier object (proxy to BANK API)
        // return cashier.payOrder(c, contents(c));
        contents(c).clear();
        return "896983"; // ASCII code for "YES"
    }

    /**
     * update the cart of a given customer
     */
    private Set<Item> updateCart(Customer c, Item item) {
        Set<Item> items = contents(c);
        Optional<Item> existing = items.stream().filter(e -> e.getCookie().equals(item.getCookie())).findFirst();
        if (existing.isPresent()) {
            items.remove(existing.get());
            Item toAdd = new Item(item.getCookie(), item.getQuantity() + existing.get().getQuantity());
            if (toAdd.getQuantity() > 0) {
                items.add(toAdd);
            }
        } else {
            items.add(item);
        }
        return items;
    }

}
