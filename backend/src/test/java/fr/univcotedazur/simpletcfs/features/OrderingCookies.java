package fr.univcotedazur.simpletcfs.features;

import fr.univcotedazur.simpletcfs.CartModifier;
import fr.univcotedazur.simpletcfs.CartProcessor;
import fr.univcotedazur.simpletcfs.CustomerFinder;
import fr.univcotedazur.simpletcfs.CustomerRegistration;
import fr.univcotedazur.simpletcfs.components.InMemoryDatabase;
import fr.univcotedazur.simpletcfs.entities.Cookies;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.exceptions.AlreadyExistingCustomerException;
import fr.univcotedazur.simpletcfs.exceptions.NegativeQuantityException;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
@SpringBootTest
public class OrderingCookies {

    @Autowired
    private CartModifier cart;

    @Autowired
    private CartProcessor processor;

    @Autowired
    private InMemoryDatabase memory;

    @Autowired
    private CustomerRegistration registry;

    @Autowired
    private CustomerFinder finder;

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

}
