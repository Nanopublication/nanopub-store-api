/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.lumc.nanopub.store.api;

import ch.tkuhn.nanopub.MalformedNanopubException;
import java.io.IOException;
import static java.util.Collections.singletonList;
import java.util.List;
import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDaoException;
import nl.lumc.nanopub.store.utils.NanopublicationFileOperation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.when;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
    
    private NanopublicationFileOperation npFileOperation = 
            new NanopublicationFileOperation();
    
    @Test
    public void testStoreNanopubResponse() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException {
        
        String nanopub = npFileOperation.getNanopub("../example.trig.rdf");
        
        String contentType = "application/x-trig";
        ResponseWrapper expected = new ResponseWrapper();
        MockHttpServletRequest httpRequest = new MockHttpServletRequest
        ("POST","/");
        //httpRequest.
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        
        expected.setValue("Thanks for " + nanopub + " of type " + contentType);        
        ResponseWrapper actual = controller.storeNanopub(contentType, 
                nanopub, httpResponse);       
        assertEquals(expected.getValue(), actual.getValue()); 
        
        
    }
    
    
    @Test
    public void testRetrieveNanopubsList() throws NanopubDaoException {
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();        
        URI uri = new 
        URIImpl("http://rdf.biosemantics.org/nanopubs/"
                + "cpm/gene_disease_associations/000001");            
        
        List<URI> result = controller.listNanopubs(httpResponse);
        assertNotNull(result);	
        assertEquals(result.get(0), uri);
    }  
    
    
    
    
}
