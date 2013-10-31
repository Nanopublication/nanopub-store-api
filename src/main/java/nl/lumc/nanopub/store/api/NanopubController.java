package nl.lumc.nanopub.store.api;

import java.io.IOException;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private NanopubDao nanopubDao;

    /**
     *
     * @param contentType
     * @param nanopub
     * @param response
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
        
        if (!"application/x-trig".equals(contentType)) {            
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // what should be the response body?		
        }
        
        Nanopub np;
        
        try {
            np = new NanopubImpl(nanopub, RDFFormat.TRIG);
            this.getNanopubDao().storeNanopub(np);
            System.out.println(nanopub);            
        } catch (NanopubDaoException | MalformedNanopubException | OpenRDFException | IOException e) {            
            logger.warn("Could not store nanopub", e);
        }
        
        ResponseWrapper responseContent = new ResponseWrapper();
        responseContent.setValue("Thanks for " + nanopub + " of type "
                + contentType);
        
        return responseContent;        
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation("Retrieves a list of all nanopub URIs in the store.")    
    public @ResponseBody
    List<URI> listNanopubs() {

        // TODO create cool implementation        
        List<URI> response = Collections.emptyList();
        try {
            response = getNanopubDao().listNanopubs();
        } catch (NanopubDaoException e) {
            logger.warn("Could not list nanopubs", e);
        }        
        return response;        
    }

    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation("Retrieves a single nanopub")
    public @ResponseBody
    Object retrieveNanopub(
            @ApiParam(required = true,
                    value = "The identifier of the required nanopublication")
            @PathVariable final String id) {
        
        logger.debug("retrieving nanopublication with id '{}'", id);
	// TODO create cool implementation		
        
        return "This is a nanopub with the id " + id;        
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
