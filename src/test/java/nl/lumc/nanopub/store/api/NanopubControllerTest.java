package nl.lumc.nanopub.store.api;


import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_NANOPUB_NAME;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_NANOPUB_URI;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_NOBASE_NANOPUB_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;
import nl.lumc.nanopub.store.utils.NanopublicationFileOperation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nanopub.MalformedNanopubException;
import org.nanopub.Nanopub;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
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
        this.nanopub = NanopublicationFileOperation.getNanopubAsString(EXAMPLE_NANOPUB_NAME);     
    }
    

    @DirtiesContext
    @Test 
    public void testStoreNanopubURLMapping() throws Exception {
        Nanopub nanopub = NanopublicationFileOperation.
                getNanopubFixture(EXAMPLE_NANOPUB_NAME);
        
        when(nanopubDao.retrieveNanopub(any(URI.class))).
                thenReturn(nanopub);
        
        this.mockMvc.perform(get(EXAMPLE_NANOPUB_URI.toString())).
                andExpect(status().isOk());
        
        
    }
    
    @DirtiesContext
    @Test    
    public void testStoreLocalNanopubURLMapping() throws Exception {
        Nanopub nanopub = NanopublicationFileOperation.
                getNanopubFixture(EXAMPLE_NANOPUB_NAME);
        
        when(nanopubDao.retrieveNanopub(any(URI.class))).
                thenReturn(nanopub);
        
        this.mockMvc.perform(get(EXAMPLE_NANOPUB_URI.toString())).
                andExpect(status().isOk());
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
    @Ignore
    public void testStoreNanopubResponse() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException {        
        
        URI uri = new URIImpl("http://mydomain.com/nanopubs/1");        
        when(nanopubDao.storeNanopub(any(Nanopub.class))).thenReturn(uri);
        
        String contentType = "application/x-trig";
        ResponseWrapper expected = new ResponseWrapper();     
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRequestURI(EXAMPLE_NANOPUB_URI.stringValue());
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        
        expected.setValue("Thanks for " + nanopub + " of type " + contentType);        
        String actual = controller.storeNanopub(nanopub, httpRequest, httpResponse);       
        assertEquals(expected.getValue(), actual);
    }
 
    
    @DirtiesContext
    @Test     
    public void testStoreNanopubResponseLegalContentType() {
        
        String contentType = "application/x-trig";
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setContentType(contentType);
        //httpRequest.setRequestURI(EXAMPLE_NANOPUB_URI.stringValue());
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();       
        
        controller.storeNanopub(nanopub, httpRequest, httpResponse);
        
        assertEquals(httpResponse.getStatus(), 
                HttpServletResponse.SC_CREATED);
    }
    
    @DirtiesContext
    @Test    
    public void testStoreNanopubResponseIllegalContentType() {
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        String nanopubUnsupported = "bla bla";

        ResponseWrapper expected = new ResponseWrapper();
        expected.setValue("Currently only application/x-trig is supported");
        
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setContentType("application/unsupportedMimeType");
        httpRequest.setRequestURI(EXAMPLE_NANOPUB_URI.stringValue());
        String actual = controller.storeNanopub(nanopubUnsupported, httpRequest, httpResponse);
        
        assertEquals(httpResponse.getStatus(), 
                HttpServletResponse.SC_NOT_ACCEPTABLE);
        assertEquals(expected.getValue(), actual);
    }
    

    
    
    
    
    @Test
	public void testListNanopubsController() throws Exception {
    	List<URI> expectedResponse = Arrays.asList(EXAMPLE_NANOPUB_URI);
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();   
        
    	when(nanopubDao.listNanopubs()).thenReturn(expectedResponse);    

		List<String> actualResponse = controller.listNanopubs(httpResponse);
		
		Mockito.verify(nanopubDao).listNanopubs();
		
		assertEquals(Arrays.asList(EXAMPLE_NANOPUB_URI.stringValue()), actualResponse);
	}
    
    
    
    @Test
	public void testListZeroNanopubs() throws Exception {
    	
		this.mockMvc.perform(get("/nanopubs")).andExpect(status().isOk()).andExpect(content().string("[]"));
	}
    
    
    @Test
	public void testListOneNanopub() throws Exception {
    	List<URI> expectedResponse = Arrays.asList(EXAMPLE_NANOPUB_URI);
    	
    	when(nanopubDao.listNanopubs()).thenReturn(expectedResponse);
    	
		this.mockMvc.perform(get("/nanopubs")).andExpect(status().isOk()).andExpect(content().string("[\"" + EXAMPLE_NANOPUB_URI.stringValue() + "\"]"));
	}
    
    
    
   
}
