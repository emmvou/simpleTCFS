package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.CustomerFinder;
import fr.univcotedazur.simpletcfs.CustomerRegistration;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.exceptions.AlreadyExistingCustomerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerRegistryTest {

    @Autowired
    private InMemoryDatabase memory;

    @Autowired
    private CustomerRegistration registry;

    @Autowired
    private CustomerFinder finder;

    private String name = "John";
    private String creditCard = "credit card number";

    @BeforeEach
    void setUp() {
        memory.flush();
    }

    @Test
    public void unknownCustomer() {
        assertFalse(finder.findByName(name).isPresent());
    }

    @Test
    public void registerCustomer() throws Exception {
        Customer returned = registry.register(name, creditCard);
        Optional<Customer> customer = finder.findByName(name);
        assertTrue(customer.isPresent());
        Customer john = customer.get();
        assertEquals(john,returned);
        assertEquals(john,finder.findById(returned.getId()).get());
        assertEquals(name, john.getName());
        assertEquals(creditCard, john.getCreditCard());
    }

    @Test
    public void cannotRegisterTwice() throws Exception {
        registry.register(name, creditCard);
        Assertions.assertThrows( AlreadyExistingCustomerException.class, () -> {
            registry.register(name, creditCard);
        });
    }

}