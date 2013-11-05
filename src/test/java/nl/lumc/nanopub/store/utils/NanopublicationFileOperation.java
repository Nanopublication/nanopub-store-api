/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.lumc.nanopub.store.utils;

import ch.tkuhn.nanopub.MalformedNanopubException;
import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.openrdf.OpenRDFException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rajaram, Eelke, Mark, Reinout
 * @serial 30-10-2013
 * @version 0.1
 */
public class NanopublicationFileOperation {
    
    private static final org.slf4j.Logger logger
            = LoggerFactory.getLogger(NanopublicationFileOperation.class);
    
    /**
     * <P>
     * To get the content of the file stored in the test resources package.
     * </P>
     * @param fileLocation  Location of the file. (E.g /validNanopub/file.trig)
     * @return  File content as a string object. 
     */
    public  String getNanopub(String fileLocation)  {
        URL fileURL = this.getClass().getResource(fileLocation);
        File npFile;
        String content = null;
        try {
            npFile = new File(fileURL.toURI());
            content = Files.readFirstLine(npFile, StandardCharsets.UTF_8);            
        } catch (IOException | URISyntaxException ex) {
            logger.warn("NanopublicationFileOperation failed ",ex);
        
        }        
        return content;
    }
    
    public Nanopub getNanopubFixture(String path) throws 
            MalformedNanopubException, OpenRDFException, IOException {		
        
        InputStream stream = this.getClass().
                getResourceAsStream(path);
        Nanopub nanopub = new NanopubImpl(stream, RDFFormat.TRIG); 
        return nanopub;	
    }
    
}
