# On Testing

  * Author: Philippe Collet

We focus here on several kinds of test that can be done in the Spring stack. It must be noted that some kinds of test can used to implement integration testing or end to end testing depending on which components are assembled, mocked, and even deployed.

## Basic Testing: the `Catalog` Component

We focus here on the implementation of a first very simple component that caontains the catalog of cookie recipes. The implementation is really straightforward with only the implementation of two methods, one to list all recipes, the other one to find recipes matching a given string.
As a result, testing the two methods is rather simple:

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

This code is purely functional, assuming a `catalogExplorator` (the interface, no one cares about the concrete implementation). But as the Catalog implementation is going to be a component, its lifecycle is handled by the Spring container.

The test setup is also straightforward as the `Catalog`component has no required interface. You only need to annotate the Test class with `@SpringBootTest` so that everything is setup by the SpringBoot test container:

* A specific test container is started. By default, it will find all components like the main container.
* All other specific wirings with JUnit 5, Mockito, etc. are done by the Spring test container.

Then the only additional setup is to inject (with `@Autowired`) the component under test in the class.

```java
@SpringBootTest
class CatalogTest {

    @Autowired
    Catalog catalog;
```




## Testing the `Cart` Component

We now focus on the implementation of a first component, dedicated to handle customer's carts, enables to show the main mechanisms of the business component layer.

Some explanations on the Cart component implementation can be found in the [Business Components](BusinessComponents.md) chapter.

The previously implemented component should ensure the four following properties: (i) the cart of a given customer is empty by default, (ii) adding multiple items results in a cart containing such items, (iii) one can remove cookies from a cart and finally (iii) one can modify the already existing quantity for a given item. Considering a given Cart named cart, the test implementation is also straightforward.

TEST


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




Annotation, autowiring the tested interfaces. Could have been only one of the two if we separate the functionalities (the update from CartModifier for example).

```java
@SpringBootTest
class CartTest {

    @Autowired
    private CartModifier cart;

    @Autowired
    private CartProcessor processor;
```

You must remark that the Cart is never initialized. This is how dependency injection works. The container analyzes the @EJB annotation and will bind your local variable to an instance a component that respect this interface, at runtime. It is not your responsibility anymore to instantiate objects when they implement EJBs.

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

## Some other testing scenarios

* Integration testing between the backend and the external bank service.
* End to end testing of the whole system (CLI+backend+bank).