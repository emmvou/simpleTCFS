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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CartTest {

    @Autowired
    private InMemoryDatabase memory;

    @Autowired
    private CustomerRegistration registry;

    @Autowired
    private CustomerFinder finder;

    @Autowired
    private CartModifier cart;

    @Autowired
    private CartProcessor processor;

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
    public void addItems() {
        cart.add(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.add(john, new Item(Cookies.DARK_TEMPTATION, 3));
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 2), new Item(Cookies.DARK_TEMPTATION, 3));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void removeItems() {
        cart.add(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.remove(john, new Item(Cookies.CHOCOLALALA, 2));
        assertEquals(0,processor.contents(john).size());
        cart.add(john, new Item(Cookies.CHOCOLALALA, 6));
        cart.remove(john, new Item(Cookies.CHOCOLALALA, 5));
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 1));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void modifyQuantities() {
        cart.add(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.add(john, new Item(Cookies.DARK_TEMPTATION, 3));
        cart.add(john, new Item(Cookies.CHOCOLALALA, 3));
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 5), new Item(Cookies.DARK_TEMPTATION, 3));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void getTheRightPrice() {
        cart.add(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.add(john, new Item(Cookies.DARK_TEMPTATION, 3));
        cart.add(john, new Item(Cookies.CHOCOLALALA, 3));
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