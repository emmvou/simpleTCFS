package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.CatalogExplorator;
import fr.univcotedazur.simpletcfs.entities.Cookies;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.entities.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InMemoryDatabaseTest {

        @Autowired
        InMemoryDatabase db;

        @Test
        void emptyWhenFlushed() {
            db.flush();
            assertEquals(0,db.getCustomers().size());
            assertEquals(0,db.getOrders().size());
            assertEquals(0,db.getCarts().size());
        }

        @Test
        void fromFulltoFlushed() {
            db.flush();
            Customer testingMan = new Customer("testingMan","000000000");
            db.getCustomers().put(testingMan.getId(),testingMan);
            Set<Item> testingItemSet = Set.of(new Item(Cookies.CHOCOLALALA,8));
            db.getCarts().put(testingMan,testingItemSet);
            Order testingOrder = new Order(testingMan,Set.of());
            db.getOrders().put(testingOrder.getId(), testingOrder);
            assertEquals(1,db.getCustomers().size());
            assertEquals(1,db.getOrders().size());
            assertEquals(1,db.getCarts().size());
        }

}