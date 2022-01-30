package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.entities.Order;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryDatabase { // Quick and dirty InMemory maps of all entities. Should be removed when moving to persistence

    private Map<Customer, Set<Item>> carts;
    public Map<Customer, Set<Item>> getCarts() { return carts; }

    private Map<String, Customer> customers;
    public Map<String, Customer> getCustomers() { return customers; }

    private Map<String, Order> orders;
    public Map<String, Order> getOrders() { return orders; }

    private int cartCounter = 0;
    public void incrementCarts() { cartCounter++; }
    public int howManyCarts() { return cartCounter; }

    public InMemoryDatabase() {
        flush();
    }

    public void flush() {
        carts       = new HashMap<>();
        customers   = new HashMap<>();
        orders      = new HashMap<>();
        cartCounter = 0;
    }
}
