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
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RepositoryUtil;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;
import ch.tkuhn.nanopub.NanopubUtils;


public class NanopubDaoImplTest {
	
	private Repository repositoryReference;
	private Repository repositoryActual;


	@Before
	public void setUp() throws Exception {
		repositoryReference = new SailRepository(new MemoryStore());
		repositoryReference.initialize();
		RepositoryConnection connection = repositoryReference.getConnection();
		connection.add(this.getClass().getResourceAsStream("../../example.trig.rdf"), 
				"http://rdf.biosemantics.org/nanopubs/cpm/gene_disease_associations/000001", RDFFormat.TRIG);
		connection.close();
		
		repositoryActual = new SailRepository(new MemoryStore());
		repositoryActual.initialize();
	}

	@After
	public void tearDown() throws Exception {
		repositoryReference.shutDown();
		repositoryActual.shutDown();
	}


	@Test
	public void testStoreNanopub() throws NanopubDaoException, MalformedNanopubException, OpenRDFException, IOException {
		int expectedSize = 1;
		Nanopub nanopub = getNanopubFixture();
		
		NanopubDao dao = new NanopubDaoImpl(repositoryActual);
		
		dao.storeNanopub(nanopub);
		
		assertEquals(expectedSize, dao.listNanopubs().size());
		assertTrue(RepositoryUtil.equals(repositoryReference, repositoryActual));
	}
	

	@Test
	public void testRetrieveNanopub() throws NanopubDaoException, MalformedNanopubException, OpenRDFException, IOException {
		URI uri = new URIImpl("http://rdf.biosemantics.org/nanopubs/cpm/gene_disease_associations/000001");
		Nanopub expectedNanopub = getNanopubFixture();
		List<Statement> expectedStatements = NanopubUtils.getStatements(expectedNanopub);
		
		NanopubDao dao = new NanopubDaoImpl(repositoryReference);
		
		Nanopub actualNanopub = dao.retrieveNanopub(uri);
		List<Statement> actualStatements = NanopubUtils.getStatements(actualNanopub);
		
		assertNotNull(actualNanopub);
		assertTrue(ModelUtil.equals(expectedStatements, actualStatements));
	}


	@Test
	public void testListNanopubs() throws NanopubDaoException {
		String expectedUri = "http://rdf.biosemantics.org/nanopubs/cpm/gene_disease_associations/000001";
		NanopubDao dao = new NanopubDaoImpl(repositoryReference);
		
		List<URI> list = dao.listNanopubs();
		
		assertFalse(list.isEmpty());
		assertEquals(expectedUri, list.get(0).stringValue());		
	}
	
	
	private Nanopub getNanopubFixture() throws MalformedNanopubException, OpenRDFException, IOException {		
        InputStream stream = this.getClass().getResourceAsStream("../../example.trig.rdf");
        Nanopub nanopub = new NanopubImpl(stream, RDFFormat.TRIG);            
        
        return nanopub;
	}
	
	

}
