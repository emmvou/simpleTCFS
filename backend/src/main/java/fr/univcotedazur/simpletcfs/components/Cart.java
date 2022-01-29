package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.CartModifier;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.exceptions.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class Cart implements CartModifier {

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
    public String validate(Customer c) throws PaymentException {
        // should ask a cashier object (proxy to BANK API)
       // return cashier.payOrder(c, contents(c));
        return "896983"; // ASCII code for "YES"
    }

    /**
     * update the cart of a given customer
     */
    private Set<Item> updateCart(Customer c, Item item) {
        Set<Item> items = contents(c);
        Optional<Item> existing = items.stream().filter(e -> e.getCookie().equals(item.getCookie())).findFirst();
        if(existing.isPresent()) {
            items.remove(existing.get());
            Item toAdd = new Item(item.getCookie(), item.getQuantity() + existing.get().getQuantity());
            if(toAdd.getQuantity() > 0) { items.add(toAdd); }
        } else {
            items.add(item);
        }
        return items;
    }

}
