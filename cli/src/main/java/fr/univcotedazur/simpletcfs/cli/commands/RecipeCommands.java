package fr.univcotedazur.simpletcfs.cli.commands;

import fr.univcotedazur.simpletcfs.cli.model.Cookies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ShellComponent
public class RecipeCommands {

    public static final String CMD_URI = "/customers/recipes";

    @Autowired
    RestTemplate restTemplate;

    @ShellMethod("List all available recipes")
    public Set<Cookies> recipes() {
        return new HashSet<Cookies>(Arrays.asList(restTemplate.getForObject(CMD_URI,Cookies[].class)));
    }

}
