package nl.lumc.nanopub.store.api;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;


@Controller
@RequestMapping("/mint-uri")
public class MintUriController {
	private static final Logger logger = LoggerFactory.getLogger(NanopubController.class);

	
	/**
	 * 
	 * @param seed
	 * @return
	 */
	@RequestMapping(value = "/mint-uri", method = RequestMethod.POST)
	@ApiOperation("mints a new uri")
	public @ResponseBody URI mintUri(
			@ApiParam(required = true, value = "seed for the uri")
			@RequestParam final String seed) {
		
		// TODO create cool implementation
		
		return URI.create(seed);
	}
}
