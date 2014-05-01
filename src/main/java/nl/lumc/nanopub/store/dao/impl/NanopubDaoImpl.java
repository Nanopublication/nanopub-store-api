package nl.lumc.nanopub.store.dao.impl;

import static org.nanopub.NanopubUtils.propagateToHandler;
import static nl.lumc.nanopub.store.api.utils.NanopubStoreConstants.STORE_MAPPING_CONTEXT;

import java.util.ArrayList;
import java.util.List;

import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;

import org.nanopub.MalformedNanopubException;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.XMLSchema;
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
	public String storeNanopub(Nanopub nanopub) throws NanopubDaoException {
		String key = nanopub.getUri().getLocalName();                
                URI nanopubUri = nanopub.getUri();
		
		if (hasNanopub(key))
		{
			throw new NanopubDaoException("Nanopub (URI) already exists!"); 
		}
		
		RepositoryConnection connection = null;
		
		try {
			connection = this.repository.getConnection();
			RDFHandler handler = new RDFInserter(connection);
			propagateToHandler(nanopub, handler);
                        storeMappingStatement(nanopubUri, key); 
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
		
		return key;
	}
	

	@Override
	public Nanopub retrieveNanopub(String key) throws NanopubDaoException {
		Nanopub nanopub = null;
                
                URI uri = retrieveNanopubURI(key);
		
		if (hasNanopub(key))
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
	public boolean hasNanopub(String key) throws NanopubDaoException {
		boolean result = false;
		RepositoryConnection connection = null;
                Literal object = new LiteralImpl(key, XMLSchema.STRING);
                
		
		try {
			connection = this.repository.getConnection();
			result = connection.hasStatement(null, DC.IDENTIFIER, object, false, STORE_MAPPING_CONTEXT);                        
                        
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
			RepositoryResult<Statement> resultSet = connection.getStatements(null, DC.IDENTIFIER, null, false, STORE_MAPPING_CONTEXT);
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
					throw new NanopubDaoException("Error closing connection (after listing nanopublications)!", e);
				}
			}
		}
		
		return result;
	}
        
        
	public void storeMappingStatement(URI nanopubUri, String key) throws NanopubDaoException {
		
		RepositoryConnection connection = null;
		
		try {
                    
                     Literal object = new LiteralImpl(key, XMLSchema.STRING); 
        
                     Statement statement = new StatementImpl(nanopubUri, 
                             DC.IDENTIFIER, object);              
			
                     connection = this.repository.getConnection();		
                     connection.add(statement, STORE_MAPPING_CONTEXT);
                        
		}
		catch (RepositoryException e)
		{
			throw new NanopubDaoException("Error storing uri to key mapping statement", e);
		}
		finally
		{
			if (connection != null)
			{
				try {
					connection.close();
				} catch (RepositoryException e) {
					throw new NanopubDaoException("Error closing connection (storing uri to key mapping statement)!", e);
				}
			}
		}
	}
        
        
        private URI retrieveNanopubURI(String key) throws NanopubDaoException {
            
            URI nanopubURI = null;
            RepositoryConnection connection = null;
            Literal object = new LiteralImpl(key, XMLSchema.STRING);
			
                try {
				
                    connection = this.repository.getConnection();
			
                    RepositoryResult<Statement> result = connection.
                            getStatements(null, DC.IDENTIFIER, object, 
                                    true, STORE_MAPPING_CONTEXT);
                    
                    while (result.hasNext()) {
                        
                        Statement stm = result.next();
                        nanopubURI = (URI)stm.getSubject();
                        
                    }
			
                } catch (RepositoryException e) {
				
                    throw new NanopubDaoException("Error retrieving nanopub uri", e);
			
                }
		
		
		return nanopubURI;
	}

}
