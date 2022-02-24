package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.CartModifier;
import fr.univcotedazur.simpletcfs.CartProcessor;
import fr.univcotedazur.simpletcfs.CustomerFinder;
import fr.univcotedazur.simpletcfs.CustomerRegistration;
import fr.univcotedazur.simpletcfs.entities.Cookies;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.exceptions.AlreadyExistingCustomerException;
import fr.univcotedazur.simpletcfs.exceptions.EmptyCartException;
import fr.univcotedazur.simpletcfs.exceptions.NegativeQuantityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CartTest {

    @Autowired
    private CartModifier cart;

    @Autowired
    private CartProcessor processor;

    @Autowired
    private InMemoryDatabase memory;

    @Autowired
    private CustomerRegistration registry;

    @Autowired
    private CustomerFinder finder;

    private Customer john;

    @BeforeEach
    void setUp() throws AlreadyExistingCustomerException {
        memory.flush();
        registry.register("John", "credit card number");
        john = finder.findByName("John").get();
    }

    @Test
    public void emptyCartByDefault() {
        assertEquals(0,processor.contents(john).size());
    }

    @Test
    public void addItems() throws NegativeQuantityException {
        cart.update(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.update(john, new Item(Cookies.DARK_TEMPTATION, 3));
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 2), new Item(Cookies.DARK_TEMPTATION, 3));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void removeItems() throws NegativeQuantityException {
        cart.update(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.update(john, new Item(Cookies.CHOCOLALALA, -2));
        assertEquals(0,processor.contents(john).size());
        cart.update(john, new Item(Cookies.CHOCOLALALA, 6));
        cart.update(john, new Item(Cookies.CHOCOLALALA, -5));
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 1));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void removeTooMuchItems() throws NegativeQuantityException {
        cart.update(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.update(john, new Item(Cookies.DARK_TEMPTATION, 3));
        Assertions.assertThrows( NegativeQuantityException.class, () -> {
            cart.update(john, new Item(Cookies.CHOCOLALALA, -3));
        });
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 2), new Item(Cookies.DARK_TEMPTATION, 3));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void modifyQuantities() throws NegativeQuantityException {
        cart.update(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.update(john, new Item(Cookies.DARK_TEMPTATION, 3));
        cart.update(john, new Item(Cookies.CHOCOLALALA, 3));
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 5), new Item(Cookies.DARK_TEMPTATION, 3));
        assertTrue(oracle.contains(new Item(Cookies.CHOCOLALALA, 5)));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void getTheRightPrice() throws NegativeQuantityException {
        cart.update(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.update(john, new Item(Cookies.DARK_TEMPTATION, 3));
        cart.update(john, new Item(Cookies.CHOCOLALALA, 3));
        assertEquals(12.20, processor.price(john), 0.01);
    }

    @Test
    public void cannotProcessEmptyCart() throws Exception {
        assertEquals(0,processor.contents(john).size());
        Assertions.assertThrows( EmptyCartException.class, () -> {
            processor.validate(john);
        });
    }

}