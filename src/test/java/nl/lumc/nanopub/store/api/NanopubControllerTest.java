/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.lumc.nanopub.store.api;


import javax.inject.Inject;
import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;



/**
 *
 * @author reinout, Rajaram
 * @since 25-10-2013
 * @version 0.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:test-api-context.xml")
public class NanopubControllerTest {
    
    @Inject
    private WebApplicationContext wac;
    
    @Inject
    private NanopubController controller;

    private MockMvc mockMvc;

    @Before
    public void setup() {
      mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void storeNanopubURLMappingTest() throws Exception {
        mockMvc.perform(get("/nanopub/store/np").param("np", "bla bla"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void storeNanopubURLMappingParamsMissing() throws Exception {
        mockMvc.perform(get("/nanopub/store"))
                .andExpect(status().isMethodNotAllowed());
    }
  
    @Test
    public void storeNanopubResponseTest () {
        
        ResponseWrapper expected = new ResponseWrapper();
        expected.setValue("Thanks!");
        
        ResponseWrapper result = controller.storeNanopub ("application/xtrig", "bla bla");       
        //assertEquals(expected,  controller.storeNanopub ("bla bla"));
        
    }

    
}
