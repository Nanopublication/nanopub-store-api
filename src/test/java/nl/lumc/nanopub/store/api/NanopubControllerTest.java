/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.lumc.nanopub.store.api;


import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;



/**
 *
 * @author Reinout, Rajaram, Eelke, Mark
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
      //mockMvc = webAppContextSetup(this.wac).build();
        mockMvc = standaloneSetup(controller).build();
    }
    

    @Test
    public void testStoreNanopubURLMapping() throws Exception {
        mockMvc.perform(get("/nanopubs/1")).andExpect(status().isOk()); //.param("np", "bla bla")); // /nanopubs/?np=bla%20bla
    }
    
    
    @Test
    public void testStoreNanopubURLMappingInvalid() throws Exception {
        mockMvc.perform(get("/nanopub_invalid_url"))
                .andExpect(status().isNotFound());
    }
    
  
    @Test
    public void testStoreNanopubResponse() {
        
        String nanopub = "bla bla";
        String contentType = "application/xtrig";
        ResponseWrapper expected = new ResponseWrapper();        
        
        expected.setValue("Thanks for " + nanopub + " of type " + contentType);        
        ResponseWrapper actual = controller.storeNanopub(contentType, nanopub);       
        Assert.assertEquals(expected.getValue(), actual.getValue());
    }
  
    
    @Test
    public void testRetrieveNanopubsListURLMapping() throws Exception {
        mockMvc.perform(get("/nanopubs")).andDo(print()); //.param("np", "bla bla")); // /nanopubs/?np=bla%20bla          
    }
  

    @Test
    public void testRetrieveNanopubsList() throws Exception {
//        ResponseWrapper expected = new ResponseWrapper();
//        expected.setValue("Thanks!");        
        
        List<URI> result = controller.listNanopubs();
        assertNotNull(result);	
    }
    
}
