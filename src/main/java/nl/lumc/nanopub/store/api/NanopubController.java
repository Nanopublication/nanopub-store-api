package nl.lumc.nanopub.store.api;

import java.io.IOException;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;

/**
 *
 * @author Eelke, Mark, Reinout, Rajaram
 * @since 25-10-2013
 * @version 0.2
 */
@Controller
@RequestMapping("/nanopubs")
public class NanopubController {    

    private static final Logger logger
            = LoggerFactory.getLogger(NanopubController.class);
    
    @Autowired
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
            np = new NanopubImpl(nanopub, RDFFormat.TRIG);
            this.getNanopubDao().storeNanopub(np);            
        } catch (NanopubDaoException | MalformedNanopubException | OpenRDFException | IOException e) {            
            logger.warn("Could not store nanopub", e);
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
    List<URI> listNanopubs(final HttpServletResponse response) {
   
        List<URI> list = Collections.emptyList();

        try {
            list = getNanopubDao().listNanopubs();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NanopubDaoException e) {
            logger.warn("Could not list nanopubs", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
            final HttpServletResponse response) {
        
        logger.debug("retrieving nanopublication with id '{}'", id);
        Nanopub result = null;
        
		try {
			URI uri = new URIImpl(id);
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

    /**
     * @return the nanopubDao
     */
    public NanopubDao getNanopubDao() {
        return nanopubDao;
    }

    /**
     * @param nanopubDao the nanopubDao to set
     */
    public void setNanopubDao(NanopubDao nanopubDao) {
        this.nanopubDao = nanopubDao;
    }
    
}
