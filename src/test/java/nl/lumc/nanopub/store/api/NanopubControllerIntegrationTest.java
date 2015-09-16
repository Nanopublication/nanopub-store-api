/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.lumc.nanopub.store.api;

import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_STORED_NANOPUB_NAME;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_STORED_URI;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.addNanopub;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.getNanopubAsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import nl.lumc.nanopub.store.dao.NanopubDaoException;
import org.junit.Ignore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanopub.MalformedNanopubException;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Model;
import org.openrdf.model.util.ModelUtil;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 *
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal
 * @author Reinout van Schouwen
 * 
 * @since 02-12-2013
 * @version 0.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:test-api-context.xml")
@Ignore
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
    public void testStoreNanopubWithoutBaseUri() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        MockHttpServletRequest request;
        MockHttpServletResponse response; 
        
        String nanopub = getNanopubAsString("example_without_base","trig");        
        String contentType = "application/x-trig";       
        
        request = new MockHttpServletRequest();
        request.setContentType(contentType);
        response = new MockHttpServletResponse();
        
        request.setMethod("POST");
        request.setRequestURI("/nanopubs/");
        request.setContent(nanopub.getBytes());
        request.addParameter("copy", "false");
        Object handler;
        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);      
        
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
        assertNotNull(response.getHeaderValue("Location"));
        assertNotNull(response.getHeaderValue("Content-Type"));    
    }
    
    @DirtiesContext
    @Test
    public void testStoreNanopubWithBaseUri() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        MockHttpServletRequest request;
        MockHttpServletResponse response; 
        
        String nanopub = getNanopubAsString("example_with_base","trig");        
        String contentType = "application/x-trig";       
        
        request = new MockHttpServletRequest();
        request.setContentType(contentType);
        response = new MockHttpServletResponse();
        
        request.setMethod("POST");
        request.setRequestURI("/nanopubs/");
        request.setContent(nanopub.getBytes());
        request.addParameter("copy", "false");
        Object handler;
        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);      
        
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
        assertNotNull(response.getHeaderValue("Location"));
        assertNotNull(response.getHeaderValue("Content-Type"));    
    } 
    
    
    @DirtiesContext
    @Test
    public void testStoreNanopubNQuads() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        MockHttpServletRequest request;
        MockHttpServletResponse response; 
        
        String nanopub = getNanopubAsString("example","nq");        
        String contentType = "application/n-quads";
        
        request = new MockHttpServletRequest();
        request.setContentType(contentType);
        response = new MockHttpServletResponse();
        
        request.setMethod("POST");
        request.setRequestURI("/nanopubs/");
        request.setContent(nanopub.getBytes());
        request.addParameter("copy", "false");
        Object handler;
        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);      
        
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
        assertNotNull(response.getHeaderValue("Location"));
        assertNotNull(response.getHeaderValue("Content-Type"));    
    } 
    
    
     @DirtiesContext
    @Test    
    public void testCopyNanopub() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        MockHttpServletRequest request;
        MockHttpServletResponse response; 
        
        String nanopub = getNanopubAsString("example_foreign","trig");        
        String contentType = "application/x-trig";       
        
        request = new MockHttpServletRequest();
        request.setContentType(contentType);
        response = new MockHttpServletResponse();
        
        request.setMethod("POST");
        request.setRequestURI("/nanopubs/");
        request.setContent(nanopub.getBytes());
        request.addParameter("copy", "true");
        Object handler;
        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);      
        
        assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
        assertNotNull(response.getHeaderValue("Location"));
        assertNotNull(response.getHeaderValue("Content-Type"));    
    } 
    
    @DirtiesContext
    @Test    
    public void testCopyInvalidKeyNanopub() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException, Exception {
        
        MockHttpServletRequest request;
        MockHttpServletResponse response; 
        
        String nanopub = getNanopubAsString("example_foreign_invalid_key","trig");        
        String contentType = "application/x-trig";       
        
        request = new MockHttpServletRequest();
        request.setContentType(contentType);
        response = new MockHttpServletResponse();
        
        request.setMethod("POST");
        request.setRequestURI("/nanopubs/");
        request.setContent(nanopub.getBytes());
        request.addParameter("copy", "true");
        Object handler;
        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);      
        
        assertEquals(HttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
    } 
    
    
    @DirtiesContext
    @Test
    public void testListZeroNanopubs() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/nanopubs");
        
        MockHttpServletResponse response = new MockHttpServletResponse();       
        
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        
        assertEquals("[]", response.getContentAsString());
    }  
    
    
    @DirtiesContext
    @Test
    public void testListNanopubs() throws Exception {
    	addNanopub(this.repository, EXAMPLE_STORED_NANOPUB_NAME);
    	
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/nanopubs");
        
        MockHttpServletResponse response = new MockHttpServletResponse();       
       
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        
        assertEquals("[\"" + EXAMPLE_STORED_URI + "\"]", 
        		response.getContentAsString());
    }    
    
    @DirtiesContext
    @Test
	public void testRetrieveNanopub() throws Exception {
    	String expectedContent = getNanopubAsString("example_stored","trig");

    	addNanopub(this.repository, EXAMPLE_STORED_NANOPUB_NAME);
    	
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setServerPort(8080);
        request.setRequestURI("/nanopub-store-api/nanopubs/RAI9hDzzF6TSvwAOwwZkRB-hq_d9OzrURvwia0FtuIPHc");
        request.setContextPath("/nanopub-store-api");
        request.addHeader("accept", "application/x-trig");
        
        MockHttpServletResponse response = new MockHttpServletResponse();       
       
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        
        InputStream isExpected = new ByteArrayInputStream(expectedContent.getBytes());
        Model modelExpected = Rio.parse(isExpected, "", RDFFormat.TRIG);
        
        String result = response.getContentAsString();
        InputStream isActual = new ByteArrayInputStream(result.getBytes());
        Model modelActual = Rio.parse(isActual, "", RDFFormat.TRIG);

//        System.out.println("\nresult: " + result);
        assertTrue(ModelUtil.equals(modelExpected, modelActual));
	}
    
}
