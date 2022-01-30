package fr.univcotedazur.simpletcfs.controllers;

import fr.univcotedazur.simpletcfs.CatalogExplorator;
import fr.univcotedazur.simpletcfs.CustomerRegistration;
import fr.univcotedazur.simpletcfs.controllers.dto.CustomerDto;
import fr.univcotedazur.simpletcfs.entities.Cookies;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.exceptions.AlreadyExistingCustomerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = CustomerCareController.BASE_URI, produces = APPLICATION_JSON_VALUE)
public class CustomerCareController {

    public static final String BASE_URI = "/customers";

    @Autowired
    private CustomerRegistration registry;

    @PostMapping(path = "register", consumes = APPLICATION_JSON_VALUE) // path is a REST CONTROLLER NAME
    public CustomerDto register(@RequestBody CustomerDto cusdto) throws AlreadyExistingCustomerException {
        // Note that there is no validation at all on the CustomerDto mapped
        return convertCustomerToDto(registry.register(cusdto.getName(), cusdto.getCreditCard()));
    }

    private CustomerDto convertCustomerToDto (Customer customer) { // In more complex cases, we could use ModelMapper
      return new CustomerDto(customer.getName(), customer.getCreditCard());
    }

}

