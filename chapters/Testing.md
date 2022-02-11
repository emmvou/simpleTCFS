# On Testing

  * Author: Philippe Collet

We focus here on several kinds of tests that can be done in the Spring stack. It must be noted that some of them can be used to implement integration testing or end to end testing depending on which components are assembled, mocked, and even deployed.

## Basic Testing: the `Catalog` Component

We focus here on the implementation of a first very simple component that contains the catalog of cookie recipes. The implementation is really straightforward with only two methods, one to list all recipes, the other one to find recipes matching a given string.
As a result, writing the functiona parts of the tests for the two methods is rather simple (see [CatalogTest](../backend/src/test/java/fr/univcotedazur/simpletcfs/components/CatalogTest.java)):

```java
    @Test
    void listPreMadeRecipesTest() {
        Set<Cookies> premade = catalog.listPreMadeRecipes();
        assertEquals(3, premade.size());
    }

    @Test
    void exploreCatalogueTest() {
        assertEquals(0, catalog.exploreCatalogue("unknown").size());
        assertEquals(2, catalog.exploreCatalogue(".*CHOCO.*").size());
        assertEquals(1, catalog.exploreCatalogue(Cookies.DARK_TEMPTATION.name()).size());
    }
```

This code is purely functional, assuming a `catalogExplorator` (the interface, no one cares about the concrete implementation). However, as the Catalog implementation is going to be a component, its lifecycle is going to be handled by the Spring container. It is not your responsibility anymore to instantiate components when in Spring.

The test setup is also straightforward as the `Catalog`component has no required interface. You only need to annotate the Test class with `@SpringBootTest` so that everything is setup by the SpringBoot test container:

* A specific test container is started. By default, it will find all components like the main container.
* All other specific wirings with JUnit 5, Mockito, etc. are done by the Spring test container. If a framework is not directly supported, it is likely to provide an extension annotation that you will have to add on the class.

Then the only additional setup is to inject (with `@Autowired`) the component under test in the class. As a result, it really looks like an average Spring implementation of a component.

```java
@SpringBootTest
class CatalogTest {

    @Autowired
    Catalog catalog;
```


## Testing the `Cart` Component

Let's now focus on the implementation of a more complex component, dedicated to handle customer's carts. 
Some explanations on the Cart component implementation can be found in the [Business Components](BusinessComponents.md) chapter.

The previously implemented component should ensure the four following properties: (i) the cart of a given customer is empty by default, (ii) adding multiple items results in a cart containing such items, (iii) one can remove cookies from a cart and finally (iii) one can modify the already existing quantity for a given item. Considering a reference on each of two interfaces, `cart` for the `CartModifier` and `processor`for the `CartProcessor`, it is again quite simple to write some tests to cover the functionalities (see [CartTest](../backend/src/test/java/fr/univcotedazur/simpletcfs/components/CartTest.java)).

```java
    @Test
    public void addItems() throws NegativeQuantityException {
        cart.update(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.update(john, new Item(Cookies.DARK_TEMPTATION, 3));
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 2), new Item(Cookies.DARK_TEMPTATION, 3));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void removeItems() throws NegativeQuantityException {
        cart.update(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.update(john, new Item(Cookies.CHOCOLALALA, -2));
        assertEquals(0,processor.contents(john).size());
        cart.update(john, new Item(Cookies.CHOCOLALALA, 6));
        cart.update(john, new Item(Cookies.CHOCOLALALA, -5));
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 1));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void removeTooMuchItems() throws NegativeQuantityException {
        cart.update(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.update(john, new Item(Cookies.DARK_TEMPTATION, 3));
        Assertions.assertThrows( NegativeQuantityException.class, () -> {
            cart.update(john, new Item(Cookies.CHOCOLALALA, -3));
        });
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 2), new Item(Cookies.DARK_TEMPTATION, 3));
        assertEquals(oracle, processor.contents(john));
    }

    @Test
    public void modifyQuantities() throws NegativeQuantityException {
        cart.update(john, new Item(Cookies.CHOCOLALALA, 2));
        cart.update(john, new Item(Cookies.DARK_TEMPTATION, 3));
        cart.update(john, new Item(Cookies.CHOCOLALALA, 3));
        Set<Item> oracle = Set.of(new Item(Cookies.CHOCOLALALA, 5), new Item(Cookies.DARK_TEMPTATION, 3));
        assertEquals(oracle, processor.contents(john));
    }
```

We can then start to configure our test class just like in the `CatalogTest`. We annotate the class and we inject the two interfaces:

```java
@SpringBootTest
class CartTest {

    @Autowired
    private CartModifier cart;

    @Autowired
    private CartProcessor processor;
```

But wait, we also need to setup a proper environment (note that we are not mocking anything yet, check the next section for that). We can use `@BeforeEach` from JUnit 5, create a customer John, through the appropriate interfaces (`CustomerRegistration` for registration, `CustomerFinder` to get the instance). To ensure test independance, we are also going to flush all the data in memory through the `InMemoryDatabase`. So we just have to inject all those interfaces and to declare a Customer attribute for the object we use as input data.


```java
    @Autowired
    private InMemoryDatabase memory;

    @Autowired
    private CustomerRegistration registry;

    @Autowired
    private CustomerFinder finder;

    private Customer john;

    @BeforeEach
    void setUp() throws AlreadyExistingCustomerException {
        memory.flush();
        registry.register("John", "credit card number");
        john = finder.findByName("John").get();
    }
```

## Mocking

In the previous test, the Cart component was tested through its two provided interfaces, but it has also required interfaces. Actually, the Spring test container was behaving like the normal one, looking for dependencies (`@Autowire`) recursively. So the `Cashier` component was created, injected through its interface `Payment` inside `Cart`, and so on for the `BankProxy` created and connected to `Cashier`.

Now let's test the `Cashier`component, which provides the `Payment` interface with a single method `Order payOrder(Customer customer, Set<Item> items) throws PaymentException;`. It looks easy, we should write a test to get the Order if the payment is going well, and another one in case the payment is rejected with the method throwing `PaymentException` (see [CashierTest](../backend/src/test/java/fr/univcotedazur/simpletcfs/components/CashierTest.java)).

```java
    @Test
    public void processToPayment() throws Exception {
        // paying order
        Order order = cashier.payOrder(john, items);
        assertNotNull(order);
        assertEquals(john, order.getCustomer());
        assertEquals(items, order.getItems());
        double price = (3 * Cookies.CHOCOLALALA.getPrice()) + (2 * Cookies.DARK_TEMPTATION.getPrice());
        assertEquals(price, order.getPrice(), 0.0);
        assertEquals(2,order.getItems().size());
    }

    @Test
    public void identifyPaymentError() {
        Assertions.assertThrows( PaymentException.class, () -> {
            cashier.payOrder(pat, items);
        });
    }
```

The main issue here is that the `Cashier` reuses the `BankProxy`, which itself is calling the external bank system. It is clearly a use case for mocking. Here the easiest way to write the test is to mock the required interface (and the component that should have been implemeting this interface). In our case, this is the `Bank` interface, so it will be declared as an attribute with the `@MockBean` annotation instead of the `@Autowired`. `@MockBean` fait appel à Mockito bien intégré avec le framework de test Spring (et donc le container de test Spring):

```java
@SpringBootTest
class CashierTest {

    @Autowired
    private InMemoryDatabase memory;

    @Autowired
    private Payment cashier;

    @MockBean
    private Bank bankMock;

    // Test context
    private Set<Item> items;
    Customer john;
    Customer pat;
```

Consequently, it enables one to write a test setup with Mockito directives (e.g. `when`and `thenReturn`) other the mocked interface. In our case, the mock is a bit smart, accepting (with true) the payment if the payer is John, and rejecting it if the payer is Pat.

```java
    @BeforeEach
    public void setUpContext() throws Exception {
        memory.flush();
        items = new HashSet<>();
        items.add(new Item(Cookies.CHOCOLALALA, 3));
        items.add(new Item(Cookies.DARK_TEMPTATION, 2));
        // Customers
        john = new Customer("john", "1234-896983");  // ends with the secret YES Card number
        pat  = new Customer("pat", "1234-567890");   // should be rejected by the payment service
        // Mocking the bank proxy
        when(bankMock.pay(eq(john), anyDouble())).thenReturn(true);
        when(bankMock.pay(eq(pat),  anyDouble())).thenReturn(false);
    }
```

## Testing a RestController with the full backend

More to come...

## Testing a RestController in isolation

More to come...

## Testing a Rest client

More to come...

## BDD in Spring

More to come...

## Some other testing scenarios that could be implemented

* Integration testing between the backend and the external bank service.
* End to end testing of the whole system (CLI+backend+bank).