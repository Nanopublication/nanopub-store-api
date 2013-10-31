package nl.lumc.nanopub.store.dao.impl;

import org.openrdf.model.URI;
import java.util.List;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandler;
import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubUtils;
import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;


public class NanopubDaoImpl implements NanopubDao {
	
	private Repository repository;
	
	
	public NanopubDaoImpl(Repository repository)
	{
		this.repository = repository;
	}
	

	@Override
	public URI storeNanopub(Nanopub nanopub) throws NanopubDaoException {
		URI result = nanopub.getUri();
		
		RepositoryConnection connection = null;
		
		try {
			connection = this.repository.getConnection();
			RDFHandler handler = new RDFInserter(connection);
			//NanopubUtils.propagateToHandler(nanopub, handler);
		} catch (Exception e) {
			throw new NanopubDaoException("Error storing nanopublication!", e);
		}
		finally
		{
			if (connection != null)
			{
				try {
					connection.close();
				} catch (RepositoryException e) {
					throw new NanopubDaoException("Error closing repository connection (after storing nanopublication)!", e);
				}
			}
		}
		
		return result;
	}
	

	@Override
	public Nanopub retrieveNanopub(URI uri) throws NanopubDaoException {
		Nanopub nanopub = null;
		
		RepositoryConnection connection = null;
		
		try {
			connection = this.repository.getConnection();
			RDFHandler handler = new RDFInserter(connection);
		//	NanopubUtils.propagateToHandler(nanopub, handler);
		} catch (Exception e) {
			throw new NanopubDaoException("Error storing nanopublication!", e);
		}
		finally
		{
			if (connection != null)
			{
				try {
					connection.close();
				} catch (RepositoryException e) {
					throw new NanopubDaoException("Error closing repository connection (after storing nanopublication)!", e);
				}
			}
		}
		
		return nanopub;
	}
	

	@Override
	public List<URI> listNanopubs() throws NanopubDaoException {
		// TODO Auto-generated method stub
		return null;
	}

}
