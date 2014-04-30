/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.lumc.nanopub.store.api.swagger;

import com.mangofactory.swagger.core.SwaggerPathProvider;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;
 
import javax.servlet.ServletContext;

/**
 *
 * @author rajaram
 */
public class ApiPathProvider implements SwaggerPathProvider {
    private SwaggerPathProvider defaultSwaggerPathProvider;
    @Autowired
    private ServletContext servletContext;
 
    private String docsLocation;
 
    public ApiPathProvider(String docsLocation) {
        this.docsLocation = docsLocation;
    }
 
    @Override
    public String getApiResourcePrefix() {
        return defaultSwaggerPathProvider.getApiResourcePrefix();
    }
    
    public String getAppBasePathUrl() {
        
        Properties p = new Properties();
        try {
            p.load(this.getClass().getClassLoader().
                    getResourceAsStream("local.properties"));
        } catch (IOException ex) {
            Logger.getLogger(ApiPathProvider.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
            
        String basePath = p.getProperty("app.docs");
        //System.out.println("basePath: " + basePath);
        
        
        return basePath;
    }
 
    public String getAppBasePath() {
        return UriComponentsBuilder
                .fromHttpUrl(getAppBasePathUrl())
                .path(servletContext.getContextPath())
                .build()
                .toString();
    }
 
    @Override
    public String getSwaggerDocumentationBasePath() {
        return UriComponentsBuilder
                .fromHttpUrl(getAppBasePath())
                .pathSegment("api-docs/")
                .build()
                .toString();
    }
 
    @Override
    public String getRequestMappingEndpoint(String requestMappingPattern) {
        return defaultSwaggerPathProvider.getRequestMappingEndpoint(requestMappingPattern);
    }
 
    public void setDefaultSwaggerPathProvider(SwaggerPathProvider defaultSwaggerPathProvider) {
        this.defaultSwaggerPathProvider = defaultSwaggerPathProvider;
    }
}
