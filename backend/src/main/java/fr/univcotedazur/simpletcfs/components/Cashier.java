package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.Bank;
import fr.univcotedazur.simpletcfs.OrderProcessing;
import fr.univcotedazur.simpletcfs.Payment;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.entities.Order;
import fr.univcotedazur.simpletcfs.exceptions.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Cashier implements Payment {

    @Autowired
    private Bank bankProxy;

    @Autowired
    private OrderProcessing kitchen;

    @Autowired
    private InMemoryDatabase memory;

    @Override
    public Order payOrder(Customer customer, Set<Item> items) throws PaymentException {

        Order order = new Order(customer, items);
        double price = order.getPrice();

        boolean status = false;
        status = bankProxy.pay(customer, price);
        if (!status) {
            throw new PaymentException(customer.getName(), price);
        }

        customer.add(order);
        memory.getOrders().put(order.getId(),order);
        kitchen.process(order);

        return order;
    }

}
