package nl.lumc.nanopub.store.api;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import static java.util.Collections.emptyList;
import javax.inject.Inject;
import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;
import static org.slf4j.LoggerFactory.getLogger;

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
        
        if(! "application/x-trig".equals(contentType)) {			
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.setHeader("Content-Type", "text/plain");
            ResponseWrapper responseContent = new ResponseWrapper();
            responseContent.setValue("Currently only application/x-trig is supported");
            
            return(responseContent);
        }
        
        Nanopub np;
        
        try {
        	String baseUri = new URIImpl(request.getRequestURL().toString()).getNamespace();
            np = new NanopubImpl(nanopub, RDFFormat.TRIG, baseUri);
            nanopubDao.storeNanopub(np);            
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
}
