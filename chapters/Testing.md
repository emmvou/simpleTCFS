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


## Running different types of test with maven

By default, maven use its *surefire* plugin to run tests. This plugin is especially built for running unit tests, as it will diretly fail if any test fails. This is a good property for preventing the build to be made (the goal *package* will typically fail).
However, when you implement integration tests, you usually want to:

   * isolate them from unit tests (e.g. to run them only on a CI server),
   * use built packages (that have passed unit tests) to put some of them together to setup a context for some integration tests, and cleaning up this context if some tests fail.

The *failsafe* plugin is made for that! From the [FAQ](https://maven.apache.org/surefire/maven-failsafe-plugin/faq.html#surefire-v-failsafe):

   * *maven-surefire-plugin* is designed for running unit tests and if any of the tests fail then it will fail the build immediately.
   * *maven-failsafe-plugin* is designed for running integration tests, and decouples failing the build if there are test failures from actually running the tests.

First, we have to include the last version of both plugins (note that in the code, these versions are set through properties, not to be duplicated). We use version *3.0.0-M5` as it is the last stable version that handles correctly SpringBoot, Cucumber 7.X with the new complete integration with JUnit 5 (see [BDD in Spring](#bdd-in-spring) below).

```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0-M5</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>3.0.0-M5</version>
...
                <executions>
                    <execution>
                        <goals>
                            <goal>integration-test</goal>
                            <goal>verify</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

It must be noted that *surefire* will, by default, find tests with the following names and run them during the `test` phase (i.e. just before `package`):  
   
   * `"**/Test*.java"` - includes all of its subdirectories and all Java filenames that start with "Test".
   * `"**/*Test.java"` - includes all of its subdirectories and all Java filenames that end with "Test".
   * `"**/*Tests.java"` - includes all of its subdirectories and all Java filenames that end with "Tests".
   * `"**/*TestCase.java"` - includes all of its subdirectories and all Java filenames that end with "TestCase".`


On its side, *failsafe* is integrated in the `verify`phase, and will run integration tests that follow, by default, the following patterns:

   * `"**/IT*.java"` - includes all of its subdirectories and all Java filenames that start with "IT".
   * `"**/*IT.java"` - includes all of its subdirectories and all Java filenames that end with "IT".
   * `"**/*ITCase.java"` - includes all of its subdirectories and all Java filenames that end with "ITCase".

With this setup, a classic packaging command:

    mvn clean package

will run unit tests, while a `verify` command:

    mvn clean verify
    
will first run unit tests through *surefire*, and then the integration tests through *failsafe*. In our case, it will run a test of the full backend through a controller and a set of Cucumber tests (see below for details).

If one wants to separate integration tests (e.g., in a CI) the following command will only run them:

    mvn clean verify '-Dtest=!*' -DfailIfNoTests=false

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

## BDD in Spring

Behavioral-Driven Development (BDD) bridges the gap between scenarios, which could be very close, in the Gherkin syntax, to acceptance criteria, and tests. This enables to mechanize tests that follows use cases or acceptance criteria from a user story.

We consider here the use case "Adding cookies to a cart" that is more or less the one used for [testing the Cart Component](#testing-the-cart-component):

  1. Considering a customer that exists in the system;
  2. The customer add some cookies to her cart
  3. The cart is updated (and remove duplicates, if any).

### Setting-up Cucumber

The _de facto_ standard to implements BDD in the Java ecosystem is the [Cucumber](https://cucumber.io/) framework. It bounds a requirements engineering language ([Gherkin](https://cucumber.io/docs/gherkin/)] to JUnit tests, using plain regular expressions.

For TCF, we need to use a version of Cucumber that is Spring-compliant, as the code to test lives inside an application container. Hopefully, the last version of Cucumber seems to be more and more well supported both by SpringBoot and IntelliJ (for generating steps, double-checking that gherkin phrases are covered by steps, etc.).

We just have to add the following dependencies in the POM file (versions are using properties in the code):

```xml
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <version>7.2.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit-platform-engine</artifactId>
            <version>7.2.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-suite</artifactId>
            <version>1.8.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-spring</artifactId>
            <version>7.2.3</version>
            <scope>test</scope>
        </dependency>
```

Note that we are not using any more the *cucumber-junit* artifact, but the *cucumber-junit-platform-engine* with the *junit-platform-suite* that enable a full support for JUnit 5 and its `@Suite@` annotation for the setup (see below).

### Modelling use cases or scenarios as Features

The Use case _Adding cookies to a cart_ is modelled as a `Feature`, and described using ([Gherkin](https://cucumber.io/docs/gherkin/)], a requirement language based on the _Given, When, Then_ paradigm. We create a file named `OrderingCookies.feature`, where we describe an instance of this very scenario:

```gherkin
Feature: Ordering Cookies

  This feature support the way a Customer can order cookies through the TCF system

  Background:
    Given a customer named "Maurice" with credit card "1234896983"
    
  Scenario: Modifying the number of cookies inside an order
    When "Maurice" orders 2 x "CHOCOLALALA"
    And "Maurice" orders 3 x "DARK_TEMPTATION"
    And "Maurice" orders 3 x "CHOCOLALALA"
    And "Maurice" asks for his cart contents
    Then there are 2 items inside the cart
    And the cart contains the following item: 5 x "CHOCOLALALA"
    And the cart contains the following item: 3 x "DARK_TEMPTATION"
```

A `Scenario` contains several steps. A `Given` one represents the context of the scenario, a `When` one the interaction with the SuT (_system under test_) and a `Then` is an assertion expected from the SuT. The `Background` section is a sub-scenario that is common to all the others, and executed before their contents.

To implement the behaviour of each steps, we can rely on a testing frameork, so  JUnit. We create a test class named `OrderingCookies`, where each step is implemented as a method. The matching that binds a step to a test method is reified as classical regular expressions (e.g. `(\\d+) for an integer`) or as specific Cucumber expression (e.g. {string} for a string between double quotes).  Method parameters correspond to each matched expression, one after another.

*Note that for easing the configuration process, the feature file and the implementation step class are placed in the same hierarchy, one inside `resources` and the other inside `test/java`.

Setting up or cleaning the context is possible through specific Cucumber annotation (e.g. `@BeforeAll`, `@Before`, `@BeforeStep`, `@After`...). Be careful as most of them have the same name as JUnit ones, but they must be imported from the `io.cucumber.java` package.

```java
@CucumberContextConfiguration
@SpringBootTest
public class OrderingCookies {

    @Autowired
    private CartModifier cart;
...
    private Customer customer;
    private Set<Item> cartContents;

    @Before
    public void settingUpContext() {
        memory.flush();
    }

    @Given("a customer named {string} with credit card {string}")
    public void aCustomerNamedWithCreditCard(String customerName, String creditCard) throws AlreadyExistingCustomerException {
        registry.register(customerName, creditCard);
    }

    @When("{string} asks for his cart contents")
    public void customerAsksForHisCartContents(String customerName) {
        customer = finder.findByName(customerName).get();
        cartContents = processor.contents(customer);
    }

    @Then("^there (?:is|are) (\\d+) items? inside the cart$") // Regular Expressions, not Cucumber expression
    // Note that you cannot mix Cucumber expression such as {int} with regular expressions
    public void thereAreItemsInsideTheCart(int nbItems) {
        assertEquals(nbItems, cartContents.size());
    }

    @When("{string} orders {int} x {string}")
    public void customerOrders(String customerName, int howMany, String recipe) throws NegativeQuantityException {
        customer = finder.findByName(customerName).get();
        Cookies cookie = Cookies.valueOf(recipe);
        cart.update(customer, new Item(cookie, howMany));
    }

    @And("the cart contains the following item: {int} x {string}")
    public void theCartContainsTheFollowingItem(int howMany, String recipe) {
        Item expected = new Item(Cookies.valueOf(recipe), howMany);
        assertTrue(cartContents.contains(expected));
    }

    @And("{string} decides not to buy {int} x {string}")
    public void customerDecidesNotToBuy(String customerName, int howMany, String recipe) throws NegativeQuantityException {
        customer = finder.findByName(customerName).get();
        Cookies cookie = Cookies.valueOf(recipe);
        cart.update(customer, new Item(cookie, -howMany));
    }

    @Then("the price of {string}'s cart is equals to {double}")
    public void thePriceOfSebSCartIsEqualsTo(String customerName, double expectedPrice) {
        customer = finder.findByName(customerName).get();
        assertEquals(expectedPrice, processor.price(customer), 0.01);
    }
```

### Cucumber-Junit-Spring Setup and execution

In Java, the Cucumber framework relies on JUnit, and some specific setup is also necessary. One additional class with enable the configuration of the JUnit 5 runner with a Cucumber specific plugin, and some options (typically the location of the feature files) can be specified:

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("fr/univcotedazur/simpletcfs/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "fr.univcotedazur.simpletcfs.features")
public class RunCucumberIT {
}
```

This class is only a hook with some configuration options:

   * It is named `RunCucumberIT` so that it will be detected by the *failsafe* plugin as an integration test (i.e. the class name ends with `IT`) and thus only run within a `mvn verify` command.
   * It uses the `@Suite` annotation from the JUnit 5 platform engine, which is the proper way to link Cucumber 7.X and JUnit 5. Take care not to use anymore the old ways, such as `@RunWith(Cucumber.class` that was using JUnit 4 or `@Cucumber` that was supported in Cucumber 6.


## Testing a RestController in isolation

As a RestController is supposed to only handle interoperability and delegate messages to the business components, a first approach is to unit test it. In Spring, it is possible to start only the Spring MVC container in test mode, with a REST controller being configured and no other Spring components nor the server itsef.
The `@WebMvcTest` annotation does this by disabling the full auto-configuration mode.
The class passed as parameter set up the controller to be created in this testing environment (here `RecipeController`).
On the configuration side, one must note that we add here the `@AutoConfigureWebClient` to avoid error about a missing `RestTemplateBuilder`. This may happen due to conflict between auto-configuration and test configuration in Spring.

Then it is quite easy to reuse the mock support (with `@MockBean`, as shown in the [Mocking](#mocking) section. For the RecipeController, we have to mock the `CatalogExplorator` interface and return a set of Cookies. One can note that in our case, we only return a set of 2 recipes while the real implementation has three of them (to show that we are indeed mocking the recipes here).

The last step consists in injection a component of type `MockMvc` and in using it to perform a call to hit the API, thus the controller under test, and then verify the status response codes and response content. 
This is done through `MockMvcRequestBuilders`, `MockMvcResultMatchers`, and `MockMvcResultHandlers`, statically imported in our case.
we thus perfom a `get` and we verify the `status()` and the content of the JSON payload through `jsonPath` methods.

```java
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class) 
@AutoConfigureWebClient
public class RecipeWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogExplorator mockedCat; // the real Catalog component is not created, we have to mock it

    @Test
    void recipesRestTest() throws Exception {
        when(mockedCat.listPreMadeRecipes())
                .thenReturn(Set.of(Cookies.CHOCOLALALA,Cookies.DARK_TEMPTATION)); // only 2 of the 3 enum values

        mockMvc.perform(get(RecipeController.BASE_URI)
                        .contentType(APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$", hasItem("CHOCOLALALA")))
                    .andExpect(jsonPath("$", hasItem("DARK_TEMPTATION")));
        }

}
```

## Testing a RestController with the full backend

If we can test a REST controller is isolation, we can also setup a full backend with the MVC container and the business container up. We can then implement a kind of integration test.
To do so, we declare a classic `@SpringBootTest` and add a `@AutoConfigureMockMvc` so that the whole backend is started.

The configuration of the test environement is completed by setting a value to the `webEnvironment` variable passed to `@SpringBootTest`. It can take several values:

   * `WebEnvironment.RANDOM_PORT` starts an embedded server with a random port, which is useful to avoir conflict in test environments while being closer to the real application deployment.
   * `WebEnvironment.DEFINED_PORT` starts an embedded server with a fixed port, usually used in some specific constraints are to be applied on ports.
   * `WebEnvironment.MOCK` is the default. It loads a web application context and provides a mock web environment. It does not load a real http server, just mocks the entire web server behavior. You gain isolation but it is weaker in terms of integration.
   * `WebEnvironment.NONE` loads the business part but does not provide any web environment (mocked or not).

Then the test can be written using the same principle as with the isolated test, injecting a `MockMvc`, performing a call to the controller (here `get`), and checking the result. Here the check ensures that the JSON contains the 3 recipes of the real implementation.

One must note that the naming of the test class, ending with *IT*, makes it match with the patterns of the *failsafe* plugin we use [to run integration tests](#running-different-types-of-test-with-maven).


```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc 
public class RecipeWebAutoConfigureIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void recipesFullStackTest() throws Exception {
        mockMvc.perform(get(RecipeController.BASE_URI)
                        .contentType(APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$", hasItem("CHOCOLALALA")))
                    .andExpect(jsonPath("$", hasItem("DARK_TEMPTATION")))
                    .andExpect(jsonPath("$", hasItem("SOO_CHOCOLATE")));
        }

}
```


## Testing a Rest client

It is also possible to test a REST Client in Spring. To do so, we use the annotation `@RestClientTest` passing as paramter the component implementation class that uses a `RestTemplate` to make REST calls.
This annotation disables full auto-configuration and only applies configuration relevant to REST client tests, e.g. Jackson support. It also provides a `MockRestServiceServer` instances that can be injected in the test.

When the test is run, only the `recipeCommands` component is created. Then in the test code, we use the `expect` method to specify that a GET call to the mocked server on the "/recipes" route should respond with success and with a specified JSON payload. Next, we call `client.recipes()`, the method being tested that makes the REST call, and checks whether the result being transformed in objects is equals to our set of Cookies enum.

Note that this example is in the [RecipeCommandsTest](../cli/src/test/java/fr/univcotedazur/simpletcfs/cli/commands/recipeCommandsTest.java) within the *cli* project.

```java
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(RecipeCommands.class)
public class RecipeCommandsTest {

     @Autowired
     private RecipeCommands client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void recipesSetTest() {

        server
                .expect(requestTo("/recipes"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[\"CHOCOLALALA\",\"DARK_TEMPTATION\",\"SOO_CHOCOLATE\"]", MediaType.APPLICATION_JSON));

        assertEquals(EnumSet.allOf(CookieEnum.class), client.recipes());
    }

}
```

## Some other testing scenarios that could be implemented

* Integration testing between the backend and the external bank service.
* End to end testing of the whole system (CLI+backend+bank).