package nl.lumc.nanopub.store.dao.impl;

import ch.tkuhn.nanopub.MalformedNanopubException;
import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;
import java.io.IOException;
import java.util.List;
import nl.lumc.nanopub.store.dao.NanopubDaoException;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

@Ignore
public class NanopubDaoImplTest {
    
    private NanopubDaoImpl nanopubDaoImpl;
    
    private Repository repository;
	
    @Before	
    public void setUp() throws Exception {
        
        repository = null;        
        nanopubDaoImpl = new NanopubDaoImpl(repository);
	
    }	
    	
    @Test	
    public void testStoreNanopub() throws MalformedNanopubException,     
            NanopubDaoException, OpenRDFException, IOException {        
        
        URI uri = nanopubDaoImpl.storeNanopub(getNanopubFixture());        
        assertNotNull(uri);	    
    }
	
    @Test	
    public void testRetrieveNanopub() throws MalformedNanopubException, 
            OpenRDFException, IOException, NanopubDaoException {
        
        URI uri = getNanopubFixture().getUri();
        Nanopub np = nanopubDaoImpl.retrieveNanopub(uri);
        assertNotNull(np);
    }
	
    @Test	
    public void testListNanopubs() throws NanopubDaoException {        
        
        List<URI> uris = nanopubDaoImpl.listNanopubs();
        assertNotNull(uris);
        
    }
    
    private Nanopub getNanopubFixture() throws MalformedNanopubException,                 
            OpenRDFException, IOException {            
            
        InputStream stream = this.getClass().                    
                getResourceAsStream("../../example.trig.rdf");            
        Nanopub nanopub = new NanopubImpl(stream, RDFFormat.TRIG);
            
        return nanopub;	
    }

}
