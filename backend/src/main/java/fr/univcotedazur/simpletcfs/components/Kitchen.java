package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.OrderProcessing;
import fr.univcotedazur.simpletcfs.Tracker;
import fr.univcotedazur.simpletcfs.entities.Order;
import fr.univcotedazur.simpletcfs.entities.OrderStatus;
import fr.univcotedazur.simpletcfs.exceptions.UnknownOrderId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Kitchen implements OrderProcessing, Tracker {

    @Autowired
    private InMemoryDatabase memory;

    @Override
    public void process(Order order) {
        order.setStatus(OrderStatus.IN_PROGRESS);
        memory.getOrders().put(order.getId(),order);
    }

    @Override
    public OrderStatus retrieveStatus(String orderId) throws UnknownOrderId {
        Order order = memory.getOrders().get(orderId);
        if (order == null)
            throw new UnknownOrderId(orderId);
        return  order.getStatus();
    }

}
