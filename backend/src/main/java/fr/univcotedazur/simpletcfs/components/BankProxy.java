package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.Bank;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.exceptions.PaymentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BankProxy implements Bank {

    @Value("${bank.host.baseurl}")
    String bankBaseUrl;

    static String BANK_ROUTE = "/todo";

    @Override
    public boolean pay(Customer customer, double value) throws PaymentException {
        // TODO RESTTemplate call to external bank
        return true;
    }
}