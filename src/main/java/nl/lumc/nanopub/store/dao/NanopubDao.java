package nl.lumc.nanopub.store.dao;

import org.openrdf.model.URI;
import java.util.List;

import org.nanopub.Nanopub;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 * 
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal
 * @author Reinout van Schouwen
 * 
 * @since 30-10-2013
 * @version 0.1
 */
public interface NanopubDao {	
	
	public String storeNanopub(Nanopub nanopub) throws NanopubDaoException;
	
	public Nanopub retrieveNanopub(String key) throws NanopubDaoException;
	
	public boolean hasNanopub(String key) throws NanopubDaoException;
	
	public List<URI> listNanopubs() throws NanopubDaoException;
}
