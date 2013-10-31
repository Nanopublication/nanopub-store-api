package nl.lumc.nanopub.store.dao;

import java.net.URI;
import java.util.List;

import ch.tkuhn.nanopub.Nanopub;

/**
 * 
 * @author Eelke, Mark, Reinout, Rajaram
 * @since 30-10-2013
 * @version 0.1
 */
public interface NanopubDao {	
	
    public URI storeNanopub(Nanopub nanopub);	
	
    public Nanopub retrieveNanopub(URI uri);	
	
    public List<URI> listNanopubs();
}
