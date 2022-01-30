package fr.univcotedazur.simpletcfs.controllers;

import fr.univcotedazur.simpletcfs.*;
import fr.univcotedazur.simpletcfs.controllers.dto.ErrorDTO;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.simpletcfs.exceptions.EmptyCartException;
import fr.univcotedazur.simpletcfs.exceptions.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = CustomerCareController.BASE_URI, produces = APPLICATION_JSON_VALUE)
// referencing the same BASE_URI as Customer care to extend it hierarchically
public class CartController {

    public static final String CART_URI = "/{customerId}/cart";

    @Autowired
    private CartModifier cart;

    @Autowired
    private CartProcessor processor;

    @Autowired
    private CustomerFinder finder;

    @ExceptionHandler({CustomerIdNotFoundException.class})
    public ResponseEntity<ErrorDTO> handleExceptions(CustomerIdNotFoundException e)  {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("Customer not found");
        errorDTO.setDetails(e.getId() + " is not a valid customer Id");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler({EmptyCartException.class})
    public ResponseEntity<ErrorDTO> handleExceptions(EmptyCartException e)  {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("Cart is empty");
        errorDTO.setDetails("from Customer " + e.getName());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }

    @ExceptionHandler({PaymentException.class})
    public ResponseEntity<ErrorDTO> handleExceptions(PaymentException e)  {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("Payment was rejected");
        errorDTO.setDetails("from Customer " + e.getName() + " for amount " + e.getAmount());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @PostMapping(path = CART_URI, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addItemToCustomerCart(@PathVariable("customerId") String customerId, @RequestBody Item it) throws CustomerIdNotFoundException {
        Customer customer = retrieveCustomer(customerId);
        if (it.getQuantity() <= 0) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setError("Item quantity should be positve when using POST (or use PATCH instead)");
            errorDTO.setDetails("on Customer " + customer.getName() + " with quantity " +it.getQuantity());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
        }
        cart.add(customer,it);
        return ResponseEntity.ok(it);
    }

    @PatchMapping(path = CART_URI, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> changeItemToCustomerCart(@PathVariable("customerId") String customerId, @RequestBody Item it) throws CustomerIdNotFoundException {
        Customer customer = retrieveCustomer(customerId);
        if (it.getQuantity() >= 0) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setError("Item quantity should be negative when using PATCH (or use POST instead)");
            errorDTO.setDetails("on Customer " + customer.getName() + " with quantity " +it.getQuantity());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
        }
        // nothing on Cookie from Item not being in the cart
        cart.add(customer,it);
        return ResponseEntity.ok(it);
    }

    @GetMapping(CART_URI)
    public ResponseEntity<Set<Item>> getCustomerCartContents(@PathVariable("customerId") String customerId) throws CustomerIdNotFoundException {
        return ResponseEntity.ok(processor.contents(retrieveCustomer(customerId)));
    }

    @PostMapping(path = CART_URI+"/validate", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validate(@PathVariable("customerId") String customerId) throws CustomerIdNotFoundException, EmptyCartException, PaymentException {
        return ResponseEntity.ok(processor.validate(retrieveCustomer(customerId)));
    }

    private Customer retrieveCustomer(String customerId) throws CustomerIdNotFoundException {
        Optional<Customer> custopt = finder.findById(customerId);
        if (custopt.isEmpty()) {
            throw new CustomerIdNotFoundException(customerId);
        }
        return custopt.get();
    }

}
