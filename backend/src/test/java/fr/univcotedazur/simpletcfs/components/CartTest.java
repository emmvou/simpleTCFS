package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.CartModifier;
import fr.univcotedazur.simpletcfs.CustomerFinder;
import fr.univcotedazur.simpletcfs.CustomerRegistration;
import fr.univcotedazur.simpletcfs.entities.Cookies;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.exceptions.AlreadyExistingCustomerException;
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

    private Customer john;

    @BeforeEach
    void setUp() throws AlreadyExistingCustomerException {
        memory.flush();
        registry.register("John", "credit card number");
        john = finder.findByName("John").get();
    }

    @Test
    public void emptyCartByDefault() {
        Set<Item> data = cart.contents(john);
        assertArrayEquals(new Item[] {}, data.toArray());
    }

    @Test
    public void addItems() {
        cart.add(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.add(john, new Item(Cookies.DARK_TEMPTATION, 3));
        Item[] oracle = new Item[] {new Item(Cookies.CHOCOLALALA, 2), new Item(Cookies.DARK_TEMPTATION, 3)  };
        assertEquals(new HashSet<>(Arrays.asList(oracle)), cart.contents(john));
    }

    @Test
    public void removeItems() {
        cart.add(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.remove(john, new Item(Cookies.CHOCOLALALA, 2));
        assertArrayEquals(new Item[] {}, cart.contents(john).toArray());
        cart.add(john, new Item(Cookies.CHOCOLALALA, 6));
        cart.remove(john, new Item(Cookies.CHOCOLALALA, 5));
        assertArrayEquals(new Item[] {new Item(Cookies.CHOCOLALALA, 1)}, cart.contents(john).toArray());
    }

    @Test
    public void modifyQuantities() {
        cart.add(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.add(john, new Item(Cookies.DARK_TEMPTATION, 3));
        cart.add(john, new Item(Cookies.CHOCOLALALA, 3));
        Item[] oracle = new Item[] {new Item(Cookies.CHOCOLALALA, 5), new Item(Cookies.DARK_TEMPTATION, 3)  };
        assertEquals(new HashSet<>(Arrays.asList(oracle)), cart.contents(john));
    }

}