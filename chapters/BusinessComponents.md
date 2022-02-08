# Business Components

  * Author: Philippe Collet

We focus here on the implementation of a first component, dedicated to handle customer's carts, enables to show the main mechanisms of the business component layer.

## Provided Interfaces

The component is very basic, still we apply interface segregation with two interfaces:

  * `CartModifier`: operations to modify a given customer's cart, like adding or removing cookies. We decide to provide a general purpose `update`method:


```java
  public interface CartModifier {

    int update(Customer retrieveCustomer, Item it) throws NegativeQuantityException;

}
```

  * `CartProcessor`: operations for retrieving the contents of the cart and validating the cart to process the associated order;

```java
public interface CartProcessor {

    Set<Item> contents(Customer c);

    double price(Customer c);

    Order validate(Customer c) throws PaymentException, EmptyCartException;

}
```

Nothing special is needed on these interfaces. They are plain Java. They are simply going to be implemented by the component to be provided by it.

## Business Component and Required Interfaces

The `Cart` component is a Java class that implements both interfaces while being annotated with `@Component`. A `@Service` annotation can also be used, it has the same semantics in Spring, but it is often used to define the service layer of a component-based system. In our case, the service layer will be implemented by `RESTController` components (see the dedicated chapter), so using `@Component` is fine.
This annotation will enable the Spring container to create all found components (by default as singleton) when initializing the web app.


Required interfaces for implementing customers' carts are `Payment` to process the cart for payment and `InMemoryDatabase`, a kind of mocked implementation for the pure volatile release of TCF. The full version, using a DB and SpringData/JPA, will not use it.
Both interfaces are used in the delcaration for two attributes of the component, with the `@Autowired` annotation. This annotation will enable the Spring container to inject the reference to the single component implementing this interface when initializing the web app. If any `@Autowired` attribute cannot be injected, the Spring container will raise an exception and stop, before any functional calls through interfaces can be triggered.

```java
@Component
public class Cart implements CartModifier, CartProcessor {

    @Autowired
    private Payment cashier;

    @Autowired
    private InMemoryDatabase memory;
```

## Business Logic

The `update` method implementation is checking the consistency of the request, e.g., that someone is not removing too much cookies from the cart, and is throwing an exception if needed. It notably reuses the `contents` method to get the set of Item from the given Customer.

One interesting implementation is the `validate` method. It first checks that the cart is not empty (or throws EmptyCartException), then delegates the payment to the `cashier` through the required interface, gets an `Order` object from it (or a PaymentException), clears the content of the cart and return the `Order`. Here the `Payment` interface enables the logic in this component to be restricted to its own responsibility: *I check for the cart, someone else handles payment and I get back a created Order I just have to forward back.* I don't care about which component is actually serving the `Payment`.

**This is really the essence of component-based software.**

```java
    @Override
    public Order validate(Customer c) throws PaymentException, EmptyCartException {
        if (contents(c).isEmpty())
            throw new EmptyCartException(c.getName());
        Order res = cashier.payOrder(c, contents(c));
        contents(c).clear();
        return res;
    }
```




