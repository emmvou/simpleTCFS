package fr.univcotedazur.simpletcfs.controllers;

import fr.univcotedazur.simpletcfs.CustomerRegistration;
import fr.univcotedazur.simpletcfs.Tracker;
import fr.univcotedazur.simpletcfs.controllers.dto.CustomerDTO;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.exceptions.AlreadyExistingCustomerException;

import fr.univcotedazur.simpletcfs.exceptions.CustomerIdNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CustomerDTO> register(@RequestBody CustomerDTO cusdto)  {
        // Note that there is no validation at all on the CustomerDto mapped
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertCustomerToDto(registry.register(cusdto.getName(), cusdto.getCreditCard())));
        } catch (AlreadyExistingCustomerException e) {
            // Note: Returning 409 (Conflict) can also be seen a security/privacy vulnerability, exposing a service for account enumeration
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    private CustomerDTO convertCustomerToDto (Customer customer) { // In more complex cases, we could use ModelMapper
      return new CustomerDTO(customer.getId(),customer.getName(), customer.getCreditCard());
    }

}

