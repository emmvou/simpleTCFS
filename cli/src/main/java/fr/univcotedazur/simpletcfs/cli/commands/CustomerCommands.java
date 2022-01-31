package fr.univcotedazur.simpletcfs.cli.commands;

import fr.univcotedazur.simpletcfs.cli.CliMemory;
import fr.univcotedazur.simpletcfs.cli.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestTemplate;

@ShellComponent
public class CustomerCommands {

    public static final String BASE_URI = "/customers";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private CliMemory cliMemory;

    @ShellMethod("Register a customer in the CoD backend (register CUSTOMER_NAME CREDIT_CARD_NUMBER)")
    public Customer register(String name, String creditCard) {
        Customer res = restTemplate.postForObject(BASE_URI+"/register",new Customer(name,creditCard),Customer.class);
        cliMemory.getCustomers().put(res.getName(),res);
        return res;
    }

    @ShellMethod("List all customers")
    public String customers() {
        return cliMemory.getCustomers().toString();
    }

}
