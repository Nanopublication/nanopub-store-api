package nl.lumc.nanopub.store.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.util.ModelUtil;
import org.openrdf.rio.RDFFormat;

import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;
import static ch.tkuhn.nanopub.NanopubUtils.getStatements;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Eelke, Mark, Reinout, Rajaram
 * @since 04-11-2013
 * @version 0.2
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:test-api-context.xml")
public class NanopubDaoImplTest {
    
    @Autowired
    private NanopubDao dao;
    private NanopublicationFileOperation npFileOperation = new 
            NanopublicationFileOperation();

    @Before
    public void setUp() throws Exception {		
	
    }

    @After
    public void tearDown() throws Exception {
		
    }
    
    @Test
    public void testStoreNanopub() throws NanopubDaoException, 
    MalformedNanopubException, OpenRDFException, IOException {		
          
        Nanopub nanopub = npFileOperation.getNanopubFixture(".."
                + "/example.trig.rdf");        
        int expectedSize = dao.listNanopubs().size() + 1;                
	dao.storeNanopub(nanopub); 
		
	assertEquals(expectedSize, dao.listNanopubs().size());
         
    }
    
    @Test
    public void testRetrieveNanopub() throws NanopubDaoException, 
    MalformedNanopubException, OpenRDFException, IOException {
		
        URI uri = new URIImpl
                ("http://rdf.biosemantics.org/nanopubs/cpm/"
                + "gene_disease_associations/000001");
		
        Nanopub expectedNanopub = npFileOperation.getNanopubFixture(".."
                + "/example.trig.rdf");
		
        List<Statement> expectedStatements = getStatements(expectedNanopub);
        
        Nanopub actualNanopub = dao.retrieveNanopub(uri);		
        List<Statement> actualStatements = getStatements(actualNanopub);		
		
        assertNotNull(actualNanopub);		
        assertTrue(ModelUtil.equals(expectedStatements, actualStatements));	
    }
    
    @Test
    public void testListNanopubs() throws NanopubDaoException {		
        String expectedUri = "http://rdf.biosemantics.org/nanopubs/cpm"
                + "/gene_disease_associations/000001";
		
        List<URI> list = dao.listNanopubs();
        
        assertFalse(list.isEmpty());		
        assertEquals(expectedUri, list.get(0).stringValue());	
    }	
	
}
