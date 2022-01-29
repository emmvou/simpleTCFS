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
    public void register(String name, String creditCard)
            throws AlreadyExistingCustomerException {
        if(findByName(name).isPresent())
            throw new AlreadyExistingCustomerException(name);
        memory.getCustomers().put(name, new Customer(name, creditCard));
    }

    @Override
    public Optional<Customer> findByName(String name) {
        if (memory.getCustomers().containsKey(name))
            return Optional.of(memory.getCustomers().get(name));
        else
            return Optional.empty();
    }

}
