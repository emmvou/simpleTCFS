package fr.univcotedazur.simpletcfs.cli;

import fr.univcotedazur.simpletcfs.cli.model.Customer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CliMemory {

    private Map<String, Customer> customers;

    public Map<String, Customer> getCustomers() {
        return customers;
    }

    public CliMemory() {
        customers = new HashMap<>();
    }

    @Override
    public String toString() {
        return customers.keySet().stream()
                .map(key -> key + "=" + customers.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

}
