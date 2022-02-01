package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.CustomerRegistration;
import fr.univcotedazur.simpletcfs.OrderProcessing;
import fr.univcotedazur.simpletcfs.Tracker;
import fr.univcotedazur.simpletcfs.entities.*;
import fr.univcotedazur.simpletcfs.exceptions.AlreadyExistingCustomerException;
import fr.univcotedazur.simpletcfs.exceptions.UnknownOrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class KitchenTest {

    @Autowired
    private InMemoryDatabase memory;

    @Autowired
    private CustomerRegistration registry;

    @Autowired
    private OrderProcessing processor;

    @Autowired
    private Tracker tracker;

    private Set<Item> items;

    @BeforeEach
    public void setUpContext() {
        memory.flush();
        items = new HashSet<>();
        items.add(new Item(Cookies.CHOCOLALALA, 3));
        items.add(new Item(Cookies.DARK_TEMPTATION, 2));
    }

    @Test
    void processCommand() throws Exception {
        Customer pat = registry.register("pat", "1234-567890");
        Order inProgress = new Order(pat, items);
        processor.process(inProgress);
        assertEquals(OrderStatus.IN_PROGRESS, tracker.retrieveStatus(inProgress.getId()));
    }

}