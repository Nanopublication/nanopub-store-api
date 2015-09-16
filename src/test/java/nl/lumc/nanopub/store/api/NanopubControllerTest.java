package nl.lumc.nanopub.store.api;

import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_NANOPUB_NAME;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_NOBASE_NANOPUB_NAME;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_STORED_URI;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.utils.NanopublicationFileOperation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nanopub.Nanopub;
import org.nanopub.NanopubUtils;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.util.ModelUtil;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal
 * @author Reinout van Schouwen
 * 
 * @since 25-10-2013
 * @version 0.3
 */
//@Ignore
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
        this.nanopub = NanopublicationFileOperation.getNanopubAsString(EXAMPLE_NANOPUB_NAME,"trig");     
    }
    
    @DirtiesContext
    @Test 
    @Ignore
    public void testStoreNanopubURLMapping() throws Exception {
        String content = NanopublicationFileOperation.
                getNanopubAsString(EXAMPLE_NOBASE_NANOPUB_NAME,"trig");
        
        this.mockMvc.perform(post("/nanopubs/").param("copy", "false").content(content).
                contentType(new MediaType("application", "x-trig"))
                ).
                andExpect(status().isCreated());
    }

    
    @DirtiesContext
	@Test
	public void testStoreNanopubURLMappingInvalidMapping() throws Exception {
		this.mockMvc.perform(get("/nanopub_invalid_url"))
	            .andExpect(status().isNotFound());
	}


	@DirtiesContext
    @Test 
    public void testStoreNanopubWithoutContentType() throws Exception {
        String content = NanopublicationFileOperation.
                getNanopubAsString(EXAMPLE_NOBASE_NANOPUB_NAME,"trig");
        
        this.mockMvc.perform(post("/nanopubs/").content(content)).
                andExpect(status().isUnsupportedMediaType());
    }
	
	
	@DirtiesContext
    @Test 
    public void testStoreNanopubWithUnsupportedContentType() throws Exception {
        String content = NanopublicationFileOperation.
                getNanopubAsString(EXAMPLE_NOBASE_NANOPUB_NAME,"trig");
        
        this.mockMvc.perform(post("/nanopubs/").content(content).contentType(MediaType.APPLICATION_XML)).
                andExpect(status().isUnsupportedMediaType());
    }    
    
    
    @DirtiesContext
	@Test
	public void testListNanopubsURLMapping() throws Exception {
		this.mockMvc.perform(get("/nanopubs/")).andExpect(status().isOk());     
	}

    
    @Test
	public void testListZeroNanopubs() throws Exception {
    	
		this.mockMvc.perform(get("/nanopubs")).andExpect(status().isOk()).andExpect(content().string("[]"));
	}
    
    
    @Test
	public void testListOneNanopub() throws Exception {
    	List<URI> expectedResponse = Arrays.asList((URI)new URIImpl(EXAMPLE_STORED_URI));
    	
    	when(nanopubDao.listNanopubs()).thenReturn(expectedResponse);
    	
		this.mockMvc.perform(get("/nanopubs")).andExpect(status().isOk()).andExpect(content().string("[\"" + EXAMPLE_STORED_URI + "\"]"));
	}    
    
    @Test
	public void testRetrieveNanopubURLMapping() throws Exception {
        Nanopub np = NanopublicationFileOperation.getNanopubFixture(EXAMPLE_NANOPUB_NAME);
        when(nanopubDao.retrieveNanopub(Mockito.any(String.class))).thenReturn(np);
        
		this.mockMvc.perform(get("/nanopubs/" + "some-integrity-key").
                        header("accept", "application/x-trig")).andExpect(status().isOk());
	}
    
    
    @Test
	public void testRetrieveNanopubInvalidURI() throws Exception {
		this.mockMvc.perform(get("/nanopubs/non-existant-integrity-key")).andExpect(status().isNotFound());
    }
    
    @Test
	public void testRetrieveNanopub() throws Exception {
    	Nanopub np = NanopublicationFileOperation.getNanopubFixture(EXAMPLE_NANOPUB_NAME);
    	when(nanopubDao.retrieveNanopub(Mockito.any(String.class))).thenReturn(np);

    	String body = this.mockMvc.perform(get
        ("/nanopubs/" + "some-integrity-key").
                header("accept", "application/x-trig"))
    			.andExpect(status().isOk())
    			.andExpect(content().contentType("application/x-trig"))
    			.andReturn().getResponse().getContentAsString();

    	InputStream isActual = new ByteArrayInputStream(body.getBytes());
    	Model modelActual = Rio.parse(isActual, "", RDFFormat.TRIG);

    	assertTrue( ModelUtil.equals(modelActual, NanopubUtils.getStatements(np)) );
    }
}
