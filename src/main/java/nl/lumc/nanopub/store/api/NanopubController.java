package nl.lumc.nanopub.store.api;

import ch.tkuhn.hashuri.rdf.TransformNanopub;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;

import org.nanopub.MalformedNanopubException;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LiteralImpl;

/**
 *
 * @author Eelke, Mark, Reinout, Rajaram
 * @since 05-11-2013
 * @version 0.2
 */
@Controller
@RequestMapping("/nanopubs")
public class NanopubController {    

    private static final Logger logger
            = getLogger(NanopubController.class);
    
    @Inject
    private NanopubDao nanopubDao;

    /**
     * Stores a nanopublication
     * @param contentType Currently only application/x-trig is supported
     * @param nanopub A nanopublication as String
     * @param response required to set HTTP response status
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("Stores a nanopublication")
    public @ResponseBody
    ResponseWrapper storeNanopub(
            @RequestHeader(value = "Content-Type") String contentType, // needs to be removed from Swagger api
            // Swagger always sends "application/json", so from the interface the string needs quotes, no quotes needed from another REST client
            @ApiParam(required = true, value = "The RDF content of the nanopublication to be published")
            @RequestBody(required = true) String nanopub,
            final HttpServletRequest request,
            final HttpServletResponse response) {        
        
        if(! contentType.contains("application/x-trig")) {			
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.setHeader("Content-Type", "text/plain");
            ResponseWrapper responseContent = new ResponseWrapper();
            responseContent.setValue("Currently only application/x-trig is supported");
            
            return(responseContent);
        }
        
        Nanopub np;
        Nanopub npHashed;
        
        try {
            //np = new NanopubImpl(nanopub, RDFFormat.TRIG);        	
            String baseUri = new URIImpl(request.getRequestURL().toString()).getNamespace();
            np = new NanopubImpl(nanopub, RDFFormat.TRIG, baseUri);
            
            if(nanopubPublished(np)) {			
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                response.setHeader("Content-Type", "text/plain");
                ResponseWrapper responseContent = new ResponseWrapper();
                responseContent.setValue("Could not store nanopub. "
                        + "This nanopublication is already published");

                return(responseContent);        
            }           
            //nanopubDao.storeNanopub(np);
            // Hashed nanopublication
            npHashed = TransformNanopub.transform(np, np.getUri().toString());
            
            System.out.println("Nanopub hash uri = "
                    +npHashed.getUri().toString());
            nanopubDao.storeNanopub(npHashed);             
                        
        } catch (NanopubDaoException | MalformedNanopubException | OpenRDFException | IOException e) {            
            logger.warn("Could not store nanopub", e);
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.setHeader("Content-Type", "text/plain");
            ResponseWrapper responseContent = new ResponseWrapper();
            responseContent.setValue(e.getMessage());
            
            return(responseContent);
        }
        
        ResponseWrapper responseContent = new ResponseWrapper();
        responseContent.setValue("Thanks for " + nanopub + " of type "
                + contentType);
        
        return responseContent;        
    }

    /**
     * Retrieves a list of all nanopub URIs in the store.
     * @param response required to set HTTP response status
     * @return a List of URIs.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation("Retrieves a list of all nanopub URIs in the store.")    
    public @ResponseBody
    List<URI> listNanopubs(@RequestParam(value = "url", required = false) final String url, final HttpServletResponse response) {
        List<URI> list = new ArrayList<URI>();
   
    	logger.info("url is given as: " + url);
    	if( url == null ) { // return all nanopubs
    		try {
    			list = nanopubDao.listNanopubs();
    			response.setStatus(HttpServletResponse.SC_OK);
    		} catch (NanopubDaoException e) {
    			logger.warn("Could not list nanopubs", e);
    			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    		}     
    	} else { // return specified nanopub
    		Nanopub res = fetchNanopub(response, url);
    		if( res != null ) {
    			list.add(res.getUri());
    		}
    	}
	    	
	    return list;        
    }
    
    
    /**
     * Retrieves a single nanopub
     * @param id The identifier of the required nanopublication
     * @param response required to set HTTP response status
     * @return a Nanopub object
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation("Retrieves a single nanopub")
    public @ResponseBody
    Nanopub retrieveNanopub(
            @ApiParam(required = true,
                    value = "The identifier of the required nanopublication")
            @PathVariable final String id,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        
        logger.debug("retrieving nanopublication with id '{}'", id);
        
        return fetchNanopub(response, request.getRequestURL().toString());
    }
    
    
    private Nanopub fetchNanopub(final HttpServletResponse response, final String url) {
    	Nanopub result = null;
        
		try {
			URI uri = new URIImpl(url);
			result = this.nanopubDao.retrieveNanopub(uri);
			
			if (result == null)
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (NanopubDaoException e) {
            logger.warn("Could not retrieve nanopub", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (IllegalArgumentException e) {
            logger.warn("Invalid nanopub URI", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	     
        return result;   
    }
    
    private boolean nanopubPublished (Nanopub np) {
        
        URI publishedPredicate = new URIImpl
        ("http://swan.mindinformatics.org/ontologies/1.2/pav/publishedOn");
        
        for (Statement st :np.getPubinfo()) {
            if (st.getPredicate().equals(publishedPredicate)) {
                return true;
            }
        }        
        return false;        
    }
}
