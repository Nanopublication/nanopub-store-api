/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.lumc.nanopub.store.api.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.nanopub.Nanopub;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.util.GraphUtilException;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.slf4j.Logger;

import com.google.common.base.Objects;

import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal 
 * 
 * @since 05-02-2014
 * @version 0.1
 */
public class NanopublicationChecks {
    
    private static final Logger logger
            = getLogger(NanopublicationChecks.class);
    
    /**
     * <p>
     * Create RDF graph model
     * </p>
     * @param np    Nanopublication 
     * @param base  Nanopublication base URI
     * @param format    RDF file format
     * 
     * @return  RDF graph model 
     */
    public static Model toRDFGraph(String np, String base, RDFFormat format) {
        Model rdfGraph = null;

        try {
            InputStream stream = new ByteArrayInputStream(np.getBytes("UTF-8"));
            rdfGraph = Rio.parse(stream, base, format);        
        } catch (UnsupportedEncodingException  | RDFParseException 
                | UnsupportedRDFormatException ex) {
            logger.warn("Could not create RDF graph", ex);
        } catch (IOException ex) {
             logger.warn("Could not create RDF graph", ex);
        }

            
        return rdfGraph;
    }
    
    /**
     * <p>
     * </p>
     * @param np Nanopublication object
     * @return True if the Nanopublication is published.
     */
    public static boolean isNanopubPublished (Model rdfGraph) {
        boolean hasPublicationDate = false;
        URI pubInfoContext = null;
        try {
            pubInfoContext = GraphUtil.getUniqueObjectURI(rdfGraph, null, 
                    Nanopub.HAS_PUBINFO_URI);
        } catch (GraphUtilException ex) {
           logger.warn("Could not get nanopub publicationInfo graph URI", ex);
        }

        hasPublicationDate = rdfGraph.contains(null, 
                NanopubStoreConstants.PUBLISHED_ON_PREDICATE, null, pubInfoContext);
       
        
        return hasPublicationDate;        
    }
    
    /**
     * <p>
     * Add published on time stamp to the nanopublication.
     * </p>
     * @param rdfGraph
     * @param np Nanopublication object
     */
    public static void addTimeStamp (Model rdfGraph) {
        
        Date date = new Date();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        
        try {
			
            XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().
                    newXMLGregorianCalendar(c);
            
            URI pubInfoContext = null;
            URI npContext = null;
            try {
                pubInfoContext = GraphUtil.getUniqueObjectURI(rdfGraph, null, Nanopub.HAS_PUBINFO_URI);
                npContext = GraphUtil.getUniqueSubjectURI(rdfGraph, RDF.TYPE, Nanopub.NANOPUB_TYPE_URI);

            } catch (GraphUtilException ex) {
                java.util.logging.Logger.getLogger(NanopublicationChecks.class.getName()).log(Level.SEVERE, null, ex);
            }

            Literal object = new LiteralImpl(xmlDate.toXMLFormat(), 
                    XMLSchema.DATETIME);	                
	        
            Statement statement = new ContextStatementImpl(npContext, 
                    NanopubStoreConstants.PUBLISHED_ON_PREDICATE, object, 
                    pubInfoContext);	        
            rdfGraph.add(statement);
	      
        } catch (DatatypeConfigurationException e) {            
            logger.warn("Could not add time stamp to the nanopub", e);		
        }

        
    }
    
    //TODO: Replace this method by RDF statement replacement
    public static Model replaceNanopubGraphUri (Collection<Statement> nanopubRdfGraph, 
            URI fromUri, URI toUri) {
    	Model source = new LinkedHashModel(nanopubRdfGraph);
    	Map<URI, URI> old2newContext = new HashMap<URI, URI>();
    	
      if (Objects.equal(fromUri, toUri))
      {
      	return source;
      }
      
      // Add NP baseUri replacement:
      old2newContext.put(fromUri, toUri);
    	
      // Matches head?
    	if (source.contains(fromUri, RDF.TYPE, Nanopub.NANOPUB_TYPE_URI, fromUri));
    	{
    		for (URI pred : Arrays.asList(Nanopub.HAS_ASSERTION_URI, Nanopub.HAS_PROVENANCE_URI, Nanopub.HAS_PUBINFO_URI))
    		{
	    		// Check the assertion/provenance/pubinfo graph:
	    		Model graph = source.filter(fromUri, pred, null, fromUri);
	    		
	    		for (Statement stmt : graph)
	    		{
	    			// Change in head:
	    			URI oldContext = (URI)stmt.getObject();
	    			URI newContext = new URIImpl(replaceBegin(oldContext.stringValue(), fromUri.stringValue(), toUri.stringValue()));
	    			
	    			old2newContext.put(oldContext, newContext);
	    		}
    		}
    	}
    	
    	Model result = new LinkedHashModel(); 
    	
    	for (Statement stmt : source) 
    	{
				// Change context of subgraph:
    		Resource subj = stmt.getSubject();
    		URI pred = stmt.getPredicate();
    		Value obj = stmt.getObject();
    		Resource cntx = stmt.getContext();
    		
    		if (old2newContext.containsKey(subj))
    		{
    			subj = old2newContext.get(subj);
    		}
    		
    		if (old2newContext.containsKey(obj))
    		{
    			obj = old2newContext.get(obj);
    		}
    		
    		if (old2newContext.containsKey(cntx))
    		{
    			cntx = old2newContext.get(cntx);
    		}
    		
				result.remove(stmt);
				result.add(subj, pred, obj, cntx);
    	}
			
      return result;
    }
    
    
    public static String replaceBegin(String source, String match, String replace)
    {
    	String result = source;
    	
    	if (source.startsWith(match))
    	{
    		result = replace + source.substring(match.length());
    	}
    	
    	return result;
    }
    
    
}
