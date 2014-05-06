package nl.lumc.nanopub.store.utils;

import static com.google.common.io.Files.readLines;
import static org.nanopub.NanopubUtils.getStatements;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import static nl.lumc.nanopub.store.api.utils.NanopubStoreConstants.STORE_MAPPING_CONTEXT;

import org.nanopub.MalformedNanopubException;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.openrdf.OpenRDFException;
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
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal
 * @author Reinout van Schouwen
 * 
 * @serial 05-11-2013
 * @version 0.2
 */
public class NanopublicationFileOperation {
    
    private static final org.slf4j.Logger LOGGER
            = getLogger(NanopublicationFileOperation.class);
	public static final String EXAMPLE_STORED_URI = 
			"http://localhost:8080/nanopub-store-api/nanopubs/RAI9hDzzF6TSvwAOwwZkRB-hq_d9OzrURvwia0FtuIPHc";
        public static final String EXAMPLE_STORED_KEY = 
			"RAI9hDzzF6TSvwAOwwZkRB-hq_d9OzrURvwia0FtuIPHc";
	public static final String EXAMPLE_NANOPUB_NAME = "example_with_base";
	public static final String EXAMPLE_STORED_NANOPUB_NAME 
	= "example_stored";
	public static final String EXAMPLE_NOBASE_NANOPUB_NAME 
                = "example_without_base";
	/**
     * <P>
     * To get the content of the file stored in the test resources package.
     * </P>
     * @param fileLocation The name of the test nanopub fixture.
     * @return  File content as a string object. 
     */
    public static String getNanopubAsString(String fileLocation, String extension)  {
        URL fileURL = NanopublicationFileOperation.
                class.getResource("../" + fileLocation + "."+extension);
        File npFile;
        String content = "";
        try {
            npFile = new File(fileURL.toURI());
            for (String fileLine : readLines(npFile, StandardCharsets.UTF_8)) {
                content += fileLine;
            }          
        } catch (IOException | URISyntaxException ex) {
            LOGGER.warn("NanopublicationFileOperation failed ",ex);
        
        }        
        return content;
    }
    
    
    public static Nanopub getNanopubFixture(String name) throws 
            MalformedNanopubException, OpenRDFException, IOException {		
        String relpath = "../" + name + ".trig";
        InputStream stream = NanopublicationFileOperation.class.
                getResourceAsStream(relpath);
        Nanopub nanopub = new NanopubImpl(stream, RDFFormat.TRIG, "");
        
        return nanopub;	
    }


	
    public static boolean addStatements(Repository repo,                 
            Collection<Statement> stmts) {		
        boolean result = false;		
        RepositoryConnection connection = null;	
		
        try {			
            connection = repo.getConnection();			
            connection.add(stmts);			
            result = true;
		
        } catch (RepositoryException e) {		
            LOGGER.warn("NanopublicationFileOperation failed ",e);		
        } finally {			
            if (connection != null) {				
                try {					
                    connection.close();				
                } catch (RepositoryException e) {					
                    LOGGER.warn("NanopublicationFileOperation failed ",e);				
                }			
            }		
        }
        return result;	
    }
	
	
	
    public static boolean addNanopub(Repository repo, String name) {		
        try {			
            Nanopub nanopub = getNanopubFixture(name);
			
            List<Statement> statements = getStatements(nanopub);			
            addStatements(repo, statements);	
            
            addMappingStatements(repo, nanopub.getUri(), 
                    nanopub.getUri().getLocalName());
		
        } catch (MalformedNanopubException | OpenRDFException | 
                IOException e) {
			
            LOGGER.warn("NanopublicationFileOperation failed ",e);			
            return false;		
        }
        return true;	
    } 
    
    
    public static void addMappingStatements(Repository repo,                 
            URI nanopubUri, String key) {		
        	
        RepositoryConnection connection = null;        
        Literal object = new LiteralImpl(key, XMLSchema.STRING);         
        Statement statement = new StatementImpl(nanopubUri, 
                DC.IDENTIFIER, object); 
		
        try {			
            connection = repo.getConnection();			
            connection.add(statement, STORE_MAPPING_CONTEXT);         
		
        } catch (RepositoryException e) {		
            LOGGER.warn("NanopublicationFileOperation failed ",e);		
        } finally {			
            if (connection != null) {				
                try {					
                    connection.close();				
                } catch (RepositoryException e) {					
                    LOGGER.warn("NanopublicationFileOperation failed ",e);				
                }			
            }		
        }	
    }
    
    
}
