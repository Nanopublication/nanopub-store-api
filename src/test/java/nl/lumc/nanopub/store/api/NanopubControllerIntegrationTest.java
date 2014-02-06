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

import nl.lumc.nanopub.store.dao.NanopubDaoException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanopub.MalformedNanopubException;
import org.openrdf.OpenRDFException;
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

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @author Mark Thompson
 * @author Eelke van der Horst
 * @author Kees Burger
 * @author Reinout van Schouwen
 * 
 * @since 02-12-2013
 * @version 0.2
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
    public void testStoreNanopub() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        MockHttpServletRequest request;
        MockHttpServletResponse response; 
        
        String nanopub = getNanopubAsString("example");        
        String contentType = "application/x-trig";       
        
        request = new MockHttpServletRequest();
        request.setContentType(contentType);
        response = new MockHttpServletResponse();
        
        request.setMethod("POST");
        request.setRequestURI("/nanopubs/");
        request.setContent(nanopub.getBytes());        
        Object handler;
        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);            
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
        assertNotNull(response.getHeaderValue("Location"));
        assertNotNull(response.getHeaderValue("Content-Type"));    
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
