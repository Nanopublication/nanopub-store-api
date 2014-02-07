package nl.lumc.nanopub.store.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *  
 * @author Eelke van der Horst
 * @author Mark Thompson 
 * @author Kees Burger
 * @author Rajaram Kaliyaperumal
 * @author Reinout van Schouwen
 * 
 * @since 10-10-2013
 * @version 0.1
 */

@Controller
public class HomeController {
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String home() {
		return "home";
	}
}
