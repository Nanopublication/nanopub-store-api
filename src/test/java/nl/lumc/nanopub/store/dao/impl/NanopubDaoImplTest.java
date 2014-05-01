package nl.lumc.nanopub.store.dao.impl;

import static org.nanopub.NanopubUtils.getStatements;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_NANOPUB_NAME;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_STORED_URI;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.addNanopub;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.addStatements;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.getNanopubFixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_STORED_KEY;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_STORED_NANOPUB_NAME;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.EXAMPLE_STORED_URI;
import static nl.lumc.nanopub.store.utils.NanopublicationFileOperation.addMappingStatements;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.util.ModelUtil;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RepositoryUtil;
import org.openrdf.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import org.nanopub.Nanopub;

/**
 * 
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal
 * @author Reinout van Schouwen
 * 
 * @since 04-11-2013
 * @version 0.2
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:test-api-context.xml")
public class NanopubDaoImplTest {

	@Autowired
	private NanopubDao dao;
	
	@Autowired
	private Repository repository;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	@DirtiesContext
	@Test
	public void testStoreNanopub() throws Exception {
		Nanopub nanopub = getNanopubFixture(EXAMPLE_STORED_NANOPUB_NAME);
		Repository repositoryExpected = new SailRepository(new MemoryStore());
		repositoryExpected.initialize();
                
                List<Statement> expectedStatements = getStatements(nanopub);
		addStatements(repositoryExpected, expectedStatements);
		
                URI storedNPUri = new URIImpl(EXAMPLE_STORED_URI);                
                addMappingStatements(repositoryExpected, storedNPUri, 
                        EXAMPLE_STORED_KEY);
		
		int expectedSize = this.dao.listNanopubs().size() + 1;
		this.dao.storeNanopub(nanopub);

		assertEquals(expectedSize, this.dao.listNanopubs().size());
		assertTrue(RepositoryUtil.equals(this.repository, repositoryExpected));
		
		repositoryExpected.shutDown();
	}
	
	@DirtiesContext
	@Test(expected=NanopubDaoException.class)
	public void testStoreExistingNanopub() throws Exception {
		Nanopub nanopub = getNanopubFixture(EXAMPLE_STORED_NANOPUB_NAME);
		List<Statement> expectedStatements = getStatements(nanopub);
		addStatements(this.repository, expectedStatements);
                
                URI storedNPUri = new URIImpl(EXAMPLE_STORED_URI);                
                addMappingStatements(this.repository, storedNPUri, 
                        EXAMPLE_STORED_KEY);
		
		this.dao.storeNanopub(nanopub);
	}
        
	@DirtiesContext
	@Test
	public void testRetrieveNanopub() throws Exception {

		Nanopub expectedNanopub = getNanopubFixture(EXAMPLE_STORED_NANOPUB_NAME);
		List<Statement> expectedStatements = getStatements(expectedNanopub);
		addStatements(this.repository, expectedStatements);
                
                URI storedNPUri = new URIImpl(EXAMPLE_STORED_URI);                
                addMappingStatements(this.repository, storedNPUri, 
                        EXAMPLE_STORED_KEY);

		Nanopub actualNanopub = this.dao.retrieveNanopub(EXAMPLE_STORED_KEY);
		assertNotNull(actualNanopub);

		List<Statement> actualStatements = getStatements(actualNanopub);

		assertTrue(ModelUtil.equals(expectedStatements, actualStatements));
	}

	@DirtiesContext
	@Test
	public void testHasNanopub() throws Exception {
            
		addNanopub(this.repository, EXAMPLE_STORED_NANOPUB_NAME);

		boolean result = this.dao.hasNanopub(EXAMPLE_STORED_KEY);

		assertTrue(result);
	}

	@DirtiesContext
	@Test
	public void testListZeroNanopubs() throws Exception {
		List<URI> list = this.dao.listNanopubs();

		assertTrue(list.isEmpty());
	}

	@DirtiesContext
	@Test
	public void testListOneNanopub() throws Exception {
		addNanopub(this.repository, EXAMPLE_STORED_NANOPUB_NAME);

		List<URI> list = this.dao.listNanopubs();

		assertFalse(list.isEmpty());
		assertEquals(new URIImpl(EXAMPLE_STORED_URI), list.get(0));
	}

}
