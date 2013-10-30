package nl.lumc.nanopub.store.dao;

import java.net.URI;
import java.util.List;

import ch.tkuhn.nanopub.Nanopub;


public interface NanopubDao {
	
	public URI storeNanopub(Nanopub nanopub);
	
	public Nanopub retrieveNanopub(URI uri);
	
	public List<URI> listNanopubs();

}
