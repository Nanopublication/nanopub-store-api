package nl.lumc.nanopub.store.api;


import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.utils.NanopublicationFileOperation;
import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.impl.URIImpl;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import org.openrdf.model.URI;
import static java.util.Collections.singletonList;
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
//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
//@ContextConfiguration("classpath:test-api-context.xml")
public class NanopubControllerTest {
    
//    @Inject
//    private WebApplicationContext wac;

    @Mock
    private NanopubDao nanopubDao;    
    
    @InjectMocks
    private NanopubController controller;    
    
    private NanopublicationFileOperation npFileOperation = 
            new NanopublicationFileOperation();

    private MockMvc mockMvc;

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
      //mockMvc = webAppContextSetup(this.wac).build();
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        //this.nanopubDao = mock(NanopubDao.class);        
//        controller.setNanopubDao(nanopubDao); // should be injected by @InjectMocks
    }
    

    @Test
    public void testStoreNanopubURLMapping() throws Exception {
        String id = "1";
        //URI uri = new URIImpl(id);
        //Nanopub nanopub = npFileOperation.getNanopubFixture(".."
         //       + "/example.trig.rdf"); 
        //when(nanopubDao.retrieveNanopub(uri)).thenReturn(nanopub);
        mockMvc.perform(get("/nanopubs/"+id)).andExpect(status().isOk()); //.param("np", "bla bla")); // /nanopubs/?np=bla%20bla
    }
    
    @Test
    public void testRetrieveNanopubsListURLMapping() throws Exception {
        mockMvc.perform(get("/nanopubs/")).andExpect(status().isOk());     
    }
    
    
    @Test
    public void testStoreNanopubURLMappingInvalid() throws Exception {
        mockMvc.perform(get("/nanopub_invalid_url"))
                .andExpect(status().isNotFound());
    }
    
    
    @Test
    public void testStoreNanopubResponse() throws MalformedNanopubException, 
    OpenRDFException, IOException, NanopubDaoException {
        
        String nanopub = npFileOperation.getNanopub("../example.trig.rdf");
                //getNanopub("/nl/lumc/nanopub/store"
                //+ "/example.trig.rdf");
        
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
 
    @Test
    public void testStoreNanopubResponseLegalContentType() {
        String nanopub = "bla bla";
        String contentType = "application/x-trig";
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        
        controller.storeNanopub(contentType, nanopub, httpResponse);
        
        assertEquals(httpResponse.getStatus(), 
                HttpServletResponse.SC_OK);
    }
    
    @Test
    public void testStoreNanopubResponseIllegalContentType() {
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        String nanopub = "bla bla";
        String contentType = "application/unsupportedMimeType";
        ResponseWrapper expected = new ResponseWrapper();
        expected.setValue("Currently only application/x-trig is supported");
        
        ResponseWrapper actual = controller.storeNanopub(contentType, 
                nanopub, httpResponse);
        
        assertEquals(httpResponse.getStatus(), 
                HttpServletResponse.SC_NOT_ACCEPTABLE);
        assertEquals(expected.getValue(), actual.getValue());
    }
    

    @Test
    public void testRetrieveNanopubsList() throws NanopubDaoException {
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();        
        URI uri = new URIImpl("http://mydomain.com/nanopubs/1");
        List<URI> uris = singletonList(uri);
        when(nanopubDao.listNanopubs()).thenReturn(uris);
        
        List<URI> result = controller.listNanopubs(httpResponse);
        assertNotNull(result);	
    }   
    
}
