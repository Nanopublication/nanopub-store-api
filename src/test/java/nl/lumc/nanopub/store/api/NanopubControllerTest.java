/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.lumc.nanopub.store.api;


import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;
import java.io.IOException;

import org.openrdf.model.URI;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;
import nl.lumc.nanopub.store.test.utils.NanopublicationFileOperation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;



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
    
    //@Inject
    private NanopublicationFileOperation npFileOperation = 
            new NanopublicationFileOperation();
    
    private NanopubDao nanopubDao;

    private MockMvc mockMvc;

    @Before
    public void setup() {
      //mockMvc = webAppContextSetup(this.wac).build();
        mockMvc = standaloneSetup(controller).build();
        nanopubDao = mock(NanopubDao.class);        
        controller.setNanopubDao(nanopubDao);
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
    public void testStoreNanopubResponse() throws MalformedNanopubException, OpenRDFException, IOException, NanopubDaoException {
        
        String nanopub = npFileOperation.getNanopub("/nl/lumc/nanopub/store/api"
                + "/example.trig.rdf");
        
        Nanopub np = new NanopubImpl(nanopub, RDFFormat.TRIG);
        URI uri = new URIImpl("http://mydomain.com/nanopubs/1");        
        when(nanopubDao.storeNanopub(np)).thenReturn(uri);
        
        String contentType = "application/xtrig";
        ResponseWrapper expected = new ResponseWrapper();        
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        
        expected.setValue("Thanks for " + nanopub + " of type " + contentType);        
        ResponseWrapper actual = controller.storeNanopub(contentType, 
                nanopub, httpResponse);       
        Assert.assertEquals(expected.getValue(), actual.getValue());
    }
 
    @Test
    public void testStoreNanopubResponseLegalContentType() {
        String nanopub = "bla bla";
        String contentType = "application/x-trig";
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        
        controller.storeNanopub(contentType, nanopub, httpResponse);
        
        Assert.assertEquals(httpResponse.getStatus(), 
                HttpServletResponse.SC_OK);
    }
    
    @Test
    public void testStoreNanopubResponseIllegalContentType() {
        String nanopub = "bla bla";
        String contentType = "application/unsupportedMimeType";
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        
        controller.storeNanopub(contentType, nanopub, httpResponse);
        
        Assert.assertEquals(httpResponse.getStatus(), 
                HttpServletResponse.SC_BAD_REQUEST);
    }  
    
    @Test
    public void testRetrieveNanopubsListURLMapping() throws Exception {
        mockMvc.perform(get("/nanopubs")).andDo(print());     
    }
  

    @Test
    public void testRetrieveNanopubsList() throws NanopubDaoException {
        
        URI uri = new URIImpl("http://mydomain.com/nanopubs/1");
        List<URI> uris = Collections.singletonList(uri);
        when(nanopubDao.listNanopubs()).thenReturn(uris);
        
        List<URI> result = controller.listNanopubs();
        assertNotNull(result);	
    }
    
    
    
}
