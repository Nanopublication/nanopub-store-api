/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.lumc.nanopub.store.api.utils;

import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.nanopub.Nanopub;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @author Mark Thompson
 * @author Eelke van der Horstatement
 * 
 * @since 05-02-2014
 * @version 0.1
 */
public class NanopublicationChecks {
    
    private static final Logger logger
            = getLogger(NanopublicationChecks.class);
    
    
    /**
     * 
     * @param np Nanopublication object
     * @return True if the Nanopublication is published.
     */
    public static boolean nanopubPublished (Nanopub np) {
        
        URI publishedPredicate = new URIImpl
        ("http://swan.mindinformatics.org/ontologies/1.2/pav/publishedOn");
        
        for (Statement st :np.getPubinfo()) {
            if (st.getPredicate().equals(publishedPredicate)) {
                return true;
            }
        }        
        return false;        
    }
    
    /**
     * <p>
     * Add published on time stamp to the nanopublication.
     * </p>
     * @param np Nanopublication object
     */
    public static void addTimeStamp (Nanopub np) {
        
        Date date = new Date();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        
        try {
			
            XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().
                    newXMLGregorianCalendar(c);
	    	        
            URI graph = np.getProvenanceUri();	        
            URI nanopub = np.getUri();	        
            URI predicate = new URIImpl("http://swan.mindinformatics.org/"
                    + "ontologies/1.2/pav/publishedOn");
            
            Literal object = new LiteralImpl(xmlDate.toXMLFormat(), 
                    XMLSchema.DATETIME);	                
	        
            Statement statement = new ContextStatementImpl(nanopub, predicate, 
                    object, graph);	        
            np.getPubinfo().add(statement);		
	      
        } catch (DatatypeConfigurationException e) {            
            logger.warn("Could not add time stamp to the nanopub", e);		
        }

        
    }
}
