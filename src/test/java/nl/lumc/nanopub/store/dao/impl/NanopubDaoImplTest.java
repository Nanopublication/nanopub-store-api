package nl.lumc.nanopub.store.dao.impl;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;

@Ignore
public class NanopubDaoImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNanopubDaoImpl() {
		fail("Not yet implemented");
	}

	@Test
	public void testStoreNanopub() {
		fail("Not yet implemented");
	}

	@Test
	public void testRetrieveNanopub() {
		fail("Not yet implemented");
	}

	@Test
	public void testListNanopubs() {
		fail("Not yet implemented");
	}
	
	
	private Nanopub getNanopubFixture() {		
            InputStream stream = this.getClass().getResourceAsStream("../../example.trig.rdf");
            Nanopub nanopub = new NanopubImpl(in, format);            
            
            return nanopub;
	}

}
