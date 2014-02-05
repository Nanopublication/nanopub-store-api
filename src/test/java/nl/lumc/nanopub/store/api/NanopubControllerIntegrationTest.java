/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.lumc.nanopub.store.api;

import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_NANOPUB_NAME;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_NANOPUB_URI;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.addNanopub;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.getNanopubAsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDaoException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanopub.MalformedNanopubException;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Rajaram, Eelke, Mark
 * @since 02-12-2013
 * @version 0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:test-api-context.xml")
public class NanopubControllerIntegrationTest {
    
    @Autowired
    private NanopubController controller;
    
    @Autowired
    private Repository repository; 
    
    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    
    
    @DirtiesContext
    @Test
    public void testStoreNanopubResponse() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        String nanopub = getNanopubAsString("example");        
        
        String contentType = "application/x-trig";
        ResponseWrapper expected = new ResponseWrapper();
       
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRequestURI(EXAMPLE_NANOPUB_URI.stringValue());
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();        
        
        expected.setValue("Thanks for " + nanopub + " of type " + contentType);        
        ResponseWrapper actual = controller.storeNanopub(nanopub, httpRequest, httpResponse);       
        assertEquals(expected.getValue(), actual.getValue()); 
    }
    
    
    @DirtiesContext
    @Test
    public void testStoreNanopubResponse2() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        MockHttpServletRequest request;
        MockHttpServletResponse response; 
        ResponseWrapper expected = new ResponseWrapper();
        ObjectMapper mapper = new ObjectMapper();
        
        String nanopub = getNanopubAsString("example_without_base");        
        String contentType = "application/x-trig";
        
        expected.setValue("Thanks for " + nanopub + " of type " + contentType);
        
        String expectedJSON = mapper.writeValueAsString(expected);
        
        request = new MockHttpServletRequest();
        request.setContentType(contentType);
        response = new MockHttpServletResponse();
        
        request.setMethod("POST");
        request.setRequestURI("/nanopubs/");
        request.setContent(nanopub.getBytes());        
        Object handler;
        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);            
        assertEquals(expectedJSON, response.getContentAsString()); 
    
    }
    
    
    @DirtiesContext
    @Test
    public void testStoreNanopubResponse3() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        MockHttpServletRequest request;
        MockHttpServletResponse response; 
        ResponseWrapper expected = new ResponseWrapper();
        ObjectMapper mapper = new ObjectMapper();
        
        String nanopub = getNanopubAsString("example_published_nanopub");        
        String contentType = "application/x-trig";
        
        expected.setValue("Thanks for " + nanopub + " of type " + contentType);
        
        String expectedJSON = mapper.writeValueAsString(expected);
        
        request = new MockHttpServletRequest();
        request.setContentType(contentType);
        response = new MockHttpServletResponse();
        
        request.setMethod("POST");
        request.setRequestURI("/nanopubs/");
        request.setContent(nanopub.getBytes());        
        Object handler;
        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);            
        assertEquals(expectedJSON, response.getContentAsString()); 
    
    }
    
    
    @DirtiesContext
    @Test
    public void testRetrieveNanopubsList() throws NanopubDaoException {
    	addNanopub(this.repository, EXAMPLE_NANOPUB_NAME);
    	
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();        
        String uri = EXAMPLE_NANOPUB_URI.stringValue();            
        
        List<String> result = controller.listNanopubs(httpResponse);
        assertNotNull(result);	
        assertEquals(result.get(0), uri);
    }  
    
    
    
    
}
