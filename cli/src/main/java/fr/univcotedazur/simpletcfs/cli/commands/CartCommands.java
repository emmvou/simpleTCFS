package fr.univcotedazur.simpletcfs.cli.commands;

import fr.univcotedazur.simpletcfs.cli.CliMemory;
import fr.univcotedazur.simpletcfs.cli.model.CartElement;
import fr.univcotedazur.simpletcfs.cli.model.CookieEnum;
import fr.univcotedazur.simpletcfs.cli.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@ShellComponent
public class CartCommands {

    public static final String BASE_URI = "/customers";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private CliMemory cliMemory;

    @ShellMethod("Show cart content of customer (showcart CUSTOMER_NAME)")
    public Set<CartElement> showCart(String name) {
        // Spring-shell is catching exception (could be the case if name is not from a valid customer
        return restTemplate.getForObject(getUriForCustomer(name),Set.class);
    }

    @ShellMethod("Add cookie to cart of customer (addtocart CUSTOMER_NAME COOKIE_NAME QUANTITY)")
    public CartElement addToCart(String name, CookieEnum cookie, int quantity) {
        // Spring-shell is catching exception (could be the case if name is not from a valid customer
        return restTemplate.postForObject(getUriForCustomer(name),new CartElement(cookie,quantity),CartElement.class);
    }

    private String getUriForCustomer (String name) {
        return BASE_URI+"/"+cliMemory.getCustomers().get(name).getId()+"/cart";
    }

}
