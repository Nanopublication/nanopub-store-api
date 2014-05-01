/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.lumc.nanopub.store.api.utils;

import java.util.Properties;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal 
 * 
 * @since 29-04-2014
 * @version 0.1
 */
public class NanopubStoreConstants {    
    /**
     * <p>
     * publishedOn predicate(from pav ontology).
     * </p>
     */
    public static final URI PUBLISHED_ON_PREDICATE = new URIImpl
        ("http://swan.mindinformatics.org/ontologies/1.2/pav/publishedOn");
    
    /**
     * <p>
     * Context for nanopubURi to key mapping triple. 
     * </p>
     */
     public static final Resource STORE_MAPPING_CONTEXT = new URIImpl
        ("http://rdf.biosemantics.org/nanopub-store-api/mappings");
    
}
