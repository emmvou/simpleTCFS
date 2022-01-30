package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.CustomerFinder;
import fr.univcotedazur.simpletcfs.CustomerRegistration;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.exceptions.AlreadyExistingCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerRegistry implements CustomerRegistration, CustomerFinder {

    @Autowired
    private InMemoryDatabase memory;

    @Override
    public Customer register(String name, String creditCard)
            throws AlreadyExistingCustomerException {
        if(findByName(name).isPresent())
            throw new AlreadyExistingCustomerException(name);
        Customer newcustomer = new Customer(name, creditCard);
        memory.getCustomers().put(newcustomer.getId(), newcustomer);
        return newcustomer;
    }

    @Override
    public Optional<Customer> findByName(String name) {
        return memory.getCustomers().values().stream()
                 .filter(cust -> name.equals(cust.getName())).findAny();
    }

    @Override
    public Optional<Customer> findById(String id) {
        if (memory.getCustomers().containsKey(id))
            return Optional.of(memory.getCustomers().get(id));
        else
            return Optional.empty();
    }

}
