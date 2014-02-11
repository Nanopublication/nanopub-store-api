/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.lumc.nanopub.store.api.json;

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
public class ResponseWrapperURI extends ResponseWrapper {
    private String nanopublicationURI; 

    /**
     * @return the nanopublicationURI
     */
    public String getNanopublicationURI() {
        return nanopublicationURI;
    }

    /**
     * @param nanopublicationURI the nanopublicationURI to set
     */
    public void setNanopublicationURI(String nanopublicationURI) {
        this.nanopublicationURI = nanopublicationURI;
    }
    
    
    @Override
    public void setValue(String value) {
        super.setValue(value);
    }
    
    @Override
    public String getValue() {
        return super.getValue();
    }
    
}
