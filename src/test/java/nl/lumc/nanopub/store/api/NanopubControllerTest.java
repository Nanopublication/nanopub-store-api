/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.lumc.nanopub.store.api;

import java.net.URI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
//import static com.jayway.restassured.RestAssured.*;
//import static org.hamcrest.Matchers.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import javax.inject.Inject;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author reinout
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-api-context.xml")
public class NanopubControllerTest {

    @Inject
    private WebApplicationContext wac;

//    @Inject
//    private NanopubController controller;

    //see http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/testing.html#spring-mvc-test-framework
    private MockMvc mockMvc;

    public NanopubControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//        this.mockMvc = MockMvcBuilders.standaloneSetup(new NanopubController()).build();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of mintUri method, of class NanopubController.
     */
    @Test
    public void testMintUri() {
        System.out.println("mintUri");
        String seed = "";
        NanopubController instance = new NanopubController();
        URI expResult = null;
        URI result = instance.mintUri(seed);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of storeNanopub method, of class NanopubController.
     */
    @Test
    public void testStoreNanopub() throws Exception {
        System.out.println("storeNanopub");
//        expect().body("value", equalTo("Thanks!")).when().put("http://localhost:7080/nanopub-store-api/store?nanopub=foobar");
        this.mockMvc.perform(put("/nanopub/store/nanopub={foo}", "foobar")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$..value").value("Thanks!"));

                //("http://localhost:7080/nanopub-store-api/store?nanopub={id}", "foobar").thenReturn().body().jsonPath().getString("value"));
    }

    /**
     * Test of getNanopublication method, of class NanopubController.
     */
    @Test
    public void testGetNanopublication() {
        System.out.println("getNanopublication");
        String id = "";
        NanopubController instance = new NanopubController();
        Object expResult = null;
        Object result = instance.getNanopublication(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
