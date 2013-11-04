/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.lumc.nanopub.store.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import nl.lumc.nanopub.store.api.NanopubController;
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
    public  String getNanopub(String fileLocation) {
        URL fileURL = this.getClass().getResource(fileLocation);
        String content = null;
        try {
            content = FileOperation.readFile(fileURL.getPath(),
             StandardCharsets.UTF_8);
        } catch (IOException ex) {
            logger.warn("NanopublicationFileOperation failed ",ex);
        }        
        return content;
    }
    
}
