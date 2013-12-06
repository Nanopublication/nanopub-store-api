package nl.lumc.nanopub.store.utils;

import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;
import static ch.tkuhn.nanopub.NanopubUtils.getStatements;
import static com.google.common.io.Files.readLines;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import com.google.common.io.Files;

import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author Rajaram, Eelke, Mark, Reinout
 * @serial 05-11-2013
 * @version 0.1
 */
public class NanopublicationFileOperation {
    
    private static final org.slf4j.Logger LOGGER
            = getLogger(NanopublicationFileOperation.class);
	public static URI EXAMPLE_NANOPUB_URI = 
			new URIImpl("http://rdf.biosemantics.org/nanopubs/000001");
	public static final String EXAMPLE_NANOPUB_NAME = "example";
    
    /**
     * <P>
     * To get the content of the file stored in the test resources package.
     * </P>
     * @param name The name of the test nanopub fixture.
     * @return  File content as a string object. 
     */
    public static String getNanopubAsString(String fileLocation)  {
        URL fileURL = NanopublicationFileOperation.class.getResource("../" + fileLocation + ".trig");
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
        InputStream stream = NanopublicationFileOperation.class.getResourceAsStream(relpath);
        Nanopub nanopub = new NanopubImpl(stream, RDFFormat.TRIG);
        
        return nanopub;	
    }


	public static boolean addStatements(Repository repo, Collection<Statement> stmts) {
		boolean result = false;
		RepositoryConnection connection = null;
	
		try {
			connection = repo.getConnection();
			connection.add(stmts);
			result = true;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
		return result;
	}
	
	
	public static boolean addNanopub(Repository repo, String name)
	{
		try {
			Nanopub nanopub = getNanopubFixture(name);

			List<Statement> statements = getStatements(nanopub);
			addStatements(repo, statements);
			
		} catch (MalformedNanopubException | OpenRDFException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}

		
		return true;
	}
    
}
