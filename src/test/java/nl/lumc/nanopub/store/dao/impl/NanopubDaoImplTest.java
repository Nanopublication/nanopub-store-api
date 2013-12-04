package nl.lumc.nanopub.store.dao.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import static ch.tkuhn.nanopub.NanopubUtils.getStatements;
import nl.lumc.nanopub.store.utils.NanopublicationFileOperation;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sun.corba.se.pept.transport.Connection;

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
	
	public static URI NANOPUB1_URI = new URIImpl
            ("http://rdf.biosemantics.org/nanopubs/cpm/"
            + "gene_disease_associations/000001");
    
    @Autowired
    private NanopubDao dao;
    @Autowired 
    private Repository repository;
    
    private NanopublicationFileOperation npFileOperation = new 
            NanopublicationFileOperation();

    @Before
    public void setUp() throws Exception {		
	
    }

    @After
    public void tearDown() throws Exception {
		
    }
    
    @DirtiesContext
    @Test
    public void testStoreNanopub() throws NanopubDaoException, 
    MalformedNanopubException, OpenRDFException, IOException {		
          
        Nanopub nanopub = npFileOperation.getNanopubFixture(".."
                + "/example.trig.rdf");        
        int expectedSize = dao.listNanopubs().size() + 1;                
        dao.storeNanopub(nanopub); 
		
        assertEquals(expectedSize, dao.listNanopubs().size());
         
    }
    
    @DirtiesContext
    @Test
    public void testRetrieveNanopub() throws NanopubDaoException, 
    MalformedNanopubException, OpenRDFException, IOException {
    	
        Nanopub expectedNanopub = npFileOperation.getNanopubFixture(".."
                + "/example.trig.rdf");
		
        List<Statement> expectedStatements = getStatements(expectedNanopub);
        addStatements(this.repository, expectedStatements);        
        
        Nanopub actualNanopub = dao.retrieveNanopub(NANOPUB1_URI);		
        assertNotNull(actualNanopub);		
        
        List<Statement> actualStatements = getStatements(actualNanopub);		
		
        assertTrue(ModelUtil.equals(expectedStatements, actualStatements));	
    }
    
    
    @DirtiesContext
    @Test
	public void testHasNanopub() throws Exception {
		addStatements(this.repository, "../example.trig.rdf");
		
		this.dao.hasNanopub(NANOPUB1_URI);
	}

    
    @DirtiesContext
    @Test
    public void testListNanopubs() throws NanopubDaoException {
        String expectedUri = "http://rdf.biosemantics.org/nanopubs/cpm"
                + "/gene_disease_associations/000001";
		
        List<URI> list = dao.listNanopubs();
        
        assertFalse(list.isEmpty());		
        assertEquals(expectedUri, list.get(0).stringValue());	
    }
    
    
    private static boolean addStatements(Repository repo, String filename) 
    {
		boolean result = false;
    	RepositoryConnection connection = null;
    	
    	try {
			connection = repo.getConnection();
			connection.add(new File(filename), filename, RDFFormat.TRIG);
			result = true;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally
    	{
    		if (connection != null)
    		{
    			try {
					connection.close();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}	
    	}		
		
		return result;
	}
    
    
    private static boolean addStatements(Repository repo, Collection<Statement> stmts)
    {
    	boolean result = false;
    	RepositoryConnection connection = null;
    	
    	try {
			connection = repo.getConnection();
			connection.add(stmts);
			result = true;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally
    	{
    		if (connection != null)
    		{
    			try {
					connection.close();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	
    	return result;
    }
	
}
