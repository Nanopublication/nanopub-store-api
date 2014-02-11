package nl.lumc.nanopub.store.dao.impl;

import static org.nanopub.NanopubUtils.propagateToHandler;

import java.util.ArrayList;
import java.util.List;

import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;

import org.nanopub.MalformedNanopubException;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

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

public class NanopubDaoImpl implements NanopubDao {
	
	private final Repository repository;
	
	
	public NanopubDaoImpl(Repository repository)
	{
		this.repository = repository;
	}
	

	/**
	 * @param nanopub The nanopublication to store.
	 * @return the URI that can be used to retrieve the nanopublication.
	 * @throws NanopubDaoException if the nanopublication already exists in the store, or 
	 * when there is an error storing the nanopublication in the underlying store.
	 */
	@Override
	public URI storeNanopub(Nanopub nanopub) throws NanopubDaoException {
		URI result = nanopub.getUri();
		
		if (hasNanopub(result))
		{
			throw new NanopubDaoException("Nanopub (URI) already exists!"); 
		}
		
		RepositoryConnection connection = null;
		
		try {
			connection = this.repository.getConnection();
			RDFHandler handler = new RDFInserter(connection);
			propagateToHandler(nanopub, handler);
		} catch (RepositoryException | RDFHandlerException e) {
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
		
		if (hasNanopub(uri))
		{
			try {
				nanopub = new NanopubImpl(this.repository, uri);
			} catch (RepositoryException | MalformedNanopubException e) {
				throw new NanopubDaoException("Error retrieving nanopub", e);
			}
		}
		
		return nanopub;
	}
	

	@Override
	public boolean hasNanopub(URI uri) throws NanopubDaoException {
		boolean result = false;
		RepositoryConnection connection = null;
		
		try {
			connection = this.repository.getConnection();
			result = connection.hasStatement(uri, RDF.TYPE, Nanopub.NANOPUB_TYPE_URI, false, uri);
		}
		catch (RepositoryException e)
		{
			throw new NanopubDaoException("Error looking for nanopublication existence!", e);
		}
		finally
		{
			if (connection != null)
			{
				try {
					connection.close();
				} catch (RepositoryException e) {
					throw new NanopubDaoException("Error closing connection (after finding np existence)!", e);
				}
			}
		}
		
		return result;
	}


	@Override
	public List<URI> listNanopubs() throws NanopubDaoException {
		List<URI> result = new ArrayList<>();
		RepositoryConnection connection = null;
		
		try {
			connection = this.repository.getConnection();
			RepositoryResult<Statement> resultSet = connection.getStatements(null, RDF.TYPE, Nanopub.NANOPUB_TYPE_URI, false);
			resultSet.enableDuplicateFilter();
			
			while (resultSet.hasNext())
			{
				Statement stmt = resultSet.next();
				if (stmt.getSubject() == stmt.getContext()){
					result.add((URI) stmt.getSubject());
				}
			}
		}
		catch (RepositoryException e)
		{
			throw new NanopubDaoException("Error retrieving nanopublication list!", e);
		}
		finally
		{
			if (connection != null)
			{
				try {
					connection.close();
				} catch (RepositoryException e) {
					throw new NanopubDaoException("Error closing connection (after listing nanopublications)!", e);
				}
			}
		}
		
		return result;
	}

}
