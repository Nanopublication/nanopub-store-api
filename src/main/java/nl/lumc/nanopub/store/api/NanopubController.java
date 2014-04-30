package nl.lumc.nanopub.store.api;

import ch.tkuhn.hashuri.rdf.TransformNanopub;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDao;
import nl.lumc.nanopub.store.dao.NanopubDaoException;

import org.nanopub.MalformedNanopubException;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.nanopub.NanopubUtils;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Level;
import nl.lumc.nanopub.store.api.utils.NanopublicationChecks;
import org.openrdf.model.Model;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.Charset;
import org.apache.commons.io.Charsets;

/**
 *
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal
 * @author Reinout van Schouwen
 * 
 * @since 10-10-2013
 * @version 0.3
 */
@Controller
@RequestMapping("/nanopubs")
@Api(value = "nanopubs",description = "nanopubs")
public class NanopubController {    

    private static final Logger logger
            = getLogger(NanopubController.class);
    
    @Inject
    private NanopubDao nanopubDao;
    

    /**
     * <p>
     * Stores a nanopublication
     * </p>
     * @param contentType Currently only application/x-trig is supported
     * @param nanopub A nanopublication as String
     * @param request required to get request URL
     * @param response required to set HTTP response status
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/x-trig", produces = "application/x-trig")
    @ApiOperation(value= "Stores a nanopublication")
    public @ResponseBody
    String storeNanopub(
            //@RequestHeader(value = "Content-Type") String contentType, // needs to be removed from Swagger api
            // Swagger always sends "application/json", so from the interface the string needs quotes, no quotes needed from another REST client
            @ApiParam(required = true, value = "The RDF content of the nanopublication to be published")
            @RequestBody(required = true) String nanopub,
            final HttpServletRequest request,
            final HttpServletResponse response) {        
        
    	String nanopubStr = storeStringNanopub (nanopub,request,response);
        
        return nanopubStr;
    }

    /**
     * <p>
     * Retrieves a list of all nanopub URIs in the store.
     * </p>
     * @param url
     * @param response required to set HTTP response status
     * @return a List of URIs.
     */
    @RequestMapping(value = "", method = RequestMethod.GET, 
            produces = "application/json")
    @ApiOperation(value = "Retrieves a list of all nanopub URIs in the store.")    
    public @ResponseBody List<String> listNanopubs(final HttpServletResponse response) {        
        List<String> list = new ArrayList<>();
 		
        try {    			
            List<URI> list2 = nanopubDao.listNanopubs();
            
            for (URI uri : list2)
            {
            	list.add(uri.stringValue());
            }
            
            response.setStatus(HttpServletResponse.SC_OK);    		
        
        } catch (NanopubDaoException e) {
			
            logger.warn("Could not list nanopubs", e);
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
        }  
            
        return list;        
    }
    
    
    /**
     * <p>
     * Retrieves a single nanopub
     * </p>
     * @param key The identifier of the required nanopublication
     * @param request
     * @param response required to set HTTP response status
     * @return a Nanopub object
     */
    @RequestMapping(value = "/{key}", method = RequestMethod.GET, consumes = {}
            )
    @ApiOperation(value="Retrieves a single nanopub"
            ,produces = ("application/x-trig, application/n-quads")
    )
    public @ResponseBody
    String retrieveNanopub(
            @ApiParam(required = true,
                    value = "The identifier of the required nanopublication")
            @PathVariable final String key,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        
        logger.debug("retrieving nanopublication with key '{}'", key);
        
        String url = request.getRequestURL().toString();       
        String contentType = request.getHeader("accept");
        
        String nanopubString = fetchNanopub(response, url, contentType);
        
        return nanopubString;
    }
    
    
    private String fetchNanopub(final HttpServletResponse response, 
            final String url, final String contentType) {
    	String result = null;
    			
        try {
            URI uri = new URIImpl(url);			
            Nanopub nanopub = this.nanopubDao.retrieveNanopub(uri);			
			
            if (nanopub == null)	{				
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);			
            }
            else {
		        
                ByteArrayOutputStream npOutStream = new ByteArrayOutputStream();                         
                if (contentType.contentEquals("application/x-trig")) {                            
                    NanopubUtils.writeToStream(nanopub, npOutStream, RDFFormat.TRIG);                            
                    response.setHeader("Content-Type", RDFFormat.TRIG.toString());                        
                }                        
                else if (contentType.contentEquals("application/n-quads")) {                            
                    NanopubUtils.writeToStream(nanopub, npOutStream, RDFFormat.NQUADS);                            
                    response.setHeader("Content-Type", RDFFormat.NQUADS.toString());                        
                }	            
                        
                result = new String(npOutStream.toByteArray(), "UTF-8");
            }
		
        } catch (NanopubDaoException | RDFHandlerException | UnsupportedEncodingException e) {            
            logger.warn("Could not retrieve nanopub", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result = "\n" + e.getMessage();            
        }
        
        return result;
    }
    
    @ApiIgnore
    @RequestMapping(value = "/upload", method = RequestMethod.POST, 
             produces = "application/x-trig")
    @ApiOperation(value= "Stores a nanopublication")
    public @ResponseBody
    String storeNanopub(
            //@RequestHeader(value = "Content-Type") String contentType, // needs to be removed from Swagger api
            // Swagger always sends "application/json", so from the interface the string needs quotes, no quotes needed from another REST client
            @ApiParam(required = true, value = "The RDF content of the nanopublication to be published")
            @RequestBody MultipartFile file,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        
        
        
        String nanopubStr = null;
        try {            
           
            //String fileContent = Files.toString(file, Charsets.UTF_8));
            String fileContent = CharStreams.toString(new InputStreamReader(file.getInputStream(), Charsets.UTF_8));
            nanopubStr = storeStringNanopub (fileContent,request,response);
        } catch (IOException ex) {
            logger.warn("Could not store nanopub", ex);
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.setHeader("Content-Type", "text/plain");
            
        }
        
        
        return nanopubStr;
        
}
    
    
    private String storeStringNanopub (final String nanopub,
            final HttpServletRequest request,
            final HttpServletResponse response) {
    
        String contentType = request.getHeader("Content-Type");
    	
        if(contentType != null && ! contentType.contains("application/x-trig")) {			
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.setHeader("Content-Type", "text/plain");           
            return("Currently only application/x-trig is supported");        
        }
        
        Nanopub npHashed;
        
        try {

            String baseUri = request.getRequestURL().toString();
            if (!baseUri.endsWith("/"))
            {
            	baseUri += "/";
            }
            
            Nanopub npSyntaxCheck = new NanopubImpl(nanopub, RDFFormat.TRIG, baseUri);

            
            Model rdfGraph = NanopublicationChecks.toRDFGraph(nanopub, baseUri, RDFFormat.TRIG);
            
            if(NanopublicationChecks.isNanopubPublished(rdfGraph)) {			
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                response.setHeader("Content-Type", "text/plain");
                return("Could not store nanopub. "
                        + "This nanopublication is already published");
            }
            
            // Adding published time stamp to the nanopublication
            NanopublicationChecks.addTimeStamp(rdfGraph);
            Nanopub np = new NanopubImpl(rdfGraph);
            // Hashed nanopublication
            npHashed = TransformNanopub.transform(np, np.getUri().toString());            
            nanopubDao.storeNanopub(npHashed);  
            
        } catch (NanopubDaoException | MalformedNanopubException | 
                OpenRDFException | IOException e) {           
            logger.warn("Could not store nanopub", e);
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.setHeader("Content-Type", "text/plain");
            return "Could not store nanopub\n" + e.getMessage();
        }
        
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setHeader("Location", npHashed.getUri().toString());
        
//        try {
//			NanopubUtils.writeToStream(npHashed, response.getOutputStream(), RDFFormat.TRIG);
//			response.getOutputStream().flush();
//		} catch (RDFHandlerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        ByteArrayOutputStream npOutStream = new ByteArrayOutputStream();        
        String nanopubStr = "";
        
        try {			
            NanopubUtils.writeToStream(npHashed, npOutStream, RDFFormat.TRIG);			
            nanopubStr = new String(npOutStream.toByteArray(), "UTF-8");            
            response.setHeader("Content-Type", RDFFormat.TRIG.toString());
        } catch (RDFHandlerException | UnsupportedEncodingException e) {
            nanopubStr = "\n"+e.getMessage();
			
        }
        return nanopubStr;
    }
   
    
    
}
