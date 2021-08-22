package fr.univcotedazur.simpletcfs.controllers;

import fr.univcotedazur.simpletcfs.CatalogExplorator;
import fr.univcotedazur.simpletcfs.entities.Cookies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("customer/")
public class CustomerCareController {

    @Autowired
    CatalogExplorator catalogExp;

    @GetMapping("recipes")
    public Set<Cookies> listAllRecipes() {
        return catalogExp.listPreMadeRecipes();
    }

}

