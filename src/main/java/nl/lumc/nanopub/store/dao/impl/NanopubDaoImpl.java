package nl.lumc.nanopub.store.dao.impl;

import java.util.ArrayList;
import java.util.List;

import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandler;

import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;
import ch.tkuhn.nanopub.NanopubUtils;
import org.openrdf.rio.RDFHandlerException;


public class NanopubDaoImpl implements NanopubDao {
	
	private final Repository repository;
	
	
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
			NanopubUtils.propagateToHandler(nanopub, handler);
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
		
		Nanopub nanopub;
		try {
			nanopub = new NanopubImpl(this.repository, uri);
		} catch (RepositoryException | MalformedNanopubException e) {
			throw new NanopubDaoException("Error retrieving nanopub", e);
		}
		
		return nanopub;
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
				result.add((URI) stmt.getSubject());
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
					throw new NanopubDaoException("Error closing connection (after listing nanopublications!", e);
				}
			}
		}
		
		return result;
	}

}
