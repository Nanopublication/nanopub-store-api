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
 * @author Reinout van Schouwen
 * 
 * @since 25-10-2013
 * @version 0.1
 */
public class ResponseWrapper {
    private String value;

    public ResponseWrapper() {
        
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    } 
    
}
