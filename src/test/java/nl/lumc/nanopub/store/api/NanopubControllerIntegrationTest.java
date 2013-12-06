/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.lumc.nanopub.store.api;

import ch.tkuhn.nanopub.MalformedNanopubException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDaoException;
import nl.lumc.nanopub.store.utils.NanopublicationFileOperation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.util.RepositoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

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
    
    private final NanopublicationFileOperation npFileOperation = 
            new NanopublicationFileOperation();
    
    
    @DirtiesContext
    @Test
    public void testStoreNanopubResponse() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        String nanopub = NanopublicationFileOperation.getNanopubAsString("example");        
        
        String contentType = "application/x-trig";
        ResponseWrapper expected = new ResponseWrapper();
       
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();        
        
        expected.setValue("Thanks for " + nanopub + " of type " + contentType);        
        ResponseWrapper actual = controller.storeNanopub(contentType, 
                nanopub, httpResponse);       
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
        
        String nanopub = NanopublicationFileOperation.getNanopubAsString("example");        
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
    	NanopublicationFileOperation.addNanopub(this.repository, NanopublicationFileOperation.EXAMPLE_NANOPUB_NAME);
    	
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();        
        URI uri = NanopublicationFileOperation.EXAMPLE_NANOPUB_URI;            
        
        List<URI> result = controller.listNanopubs(uri.stringValue(), httpResponse);
        assertNotNull(result);	
        assertEquals(result.get(0), uri);
    }  
    
    
    
    
}
