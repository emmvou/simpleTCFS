package fr.univcotedazur.simpletcfs.controllers;

import fr.univcotedazur.simpletcfs.CatalogExplorator;
import fr.univcotedazur.simpletcfs.entities.Cookies;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerCareController.class) // start only the specified MVC front controller and no other Spring components nor the server
public class CustomerCareWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogExplorator mockedCat; // the real Catalog component is not created, we have to mock it

    @Test
    void recipesFullStackTest() throws Exception {
        when(mockedCat.listPreMadeRecipes())
                .thenReturn(Set.of(Cookies.CHOCOLALALA,Cookies.DARK_TEMPTATION)); // only 2 of the 3 enum values

        mockMvc.perform(get("/customer/recipes"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$", hasItem("CHOCOLALALA")))
                    .andExpect(jsonPath("$", hasItem("DARK_TEMPTATION")));
        }

}
