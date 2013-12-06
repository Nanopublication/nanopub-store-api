package nl.lumc.nanopub.store.api;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.utils.NanopublicationFileOperation;
import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.impl.URIImpl;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import org.openrdf.model.URI;
import static org.junit.Assert.assertEquals;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;



/**
 *
 * @author Reinout, Rajaram, Eelke, Mark
 * @since 25-10-2013
 * @version 0.3
 */
public class NanopubControllerTest {

    @Mock
    private NanopubDao nanopubDao;
    
    private String nanopub;    
    
    @InjectMocks
    private NanopubController controller; 

    
    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        this.nanopub = NanopublicationFileOperation.getNanopubAsString("example");       
    }
    

    @DirtiesContext
    @Test
    @Ignore
    public void testStoreNanopubURLMapping() throws Exception {
        Nanopub nanopub = NanopublicationFileOperation.getNanopubFixture("example");
        
        when(nanopubDao.retrieveNanopub(EXAMPLE_NANOPUB_URI)).thenReturn(nanopub);
        
        this.mockMvc.perform(get(EXAMPLE_NANOPUB_URI.toString())).andExpect(status().isOk()); //.param("np", "bla bla")); // /nanopubs/?np=bla%20bla
        
    }
    
    @DirtiesContext
    @Test
    public void testRetrieveNanopubsListURLMapping() throws Exception {
    	this.mockMvc.perform(get("/nanopubs/")).andExpect(status().isOk());     
    }
    
    
    @DirtiesContext
    @Test
    public void testStoreNanopubURLMappingInvalid() throws Exception {
    	this.mockMvc.perform(get("/nanopub_invalid_url"))
                .andExpect(status().isNotFound());
    }
    
    
    @DirtiesContext
    @Test
    public void testStoreNanopubResponse() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException {        
        
        URI uri = new URIImpl("http://mydomain.com/nanopubs/1");        
        when(nanopubDao.storeNanopub(any(Nanopub.class))).thenReturn(uri);
        
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
    public void testStoreNanopubResponseLegalContentType() {
        
        String contentType = "application/x-trig";
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();       
        
        
        controller.storeNanopub(contentType, nanopub, httpResponse);
        
        assertEquals(httpResponse.getStatus(), 
                HttpServletResponse.SC_OK);
    }
    
    @DirtiesContext
    @Test
    public void testStoreNanopubResponseIllegalContentType() {
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        String nanopubUnsupported = "bla bla";
        String contentType = "application/unsupportedMimeType";
        ResponseWrapper expected = new ResponseWrapper();
        expected.setValue("Currently only application/x-trig is supported");
        
        ResponseWrapper actual = controller.storeNanopub(contentType, 
                nanopubUnsupported, httpResponse);
        
        assertEquals(httpResponse.getStatus(), 
                HttpServletResponse.SC_NOT_ACCEPTABLE);
        assertEquals(expected.getValue(), actual.getValue());
    }
    

     
    
}
