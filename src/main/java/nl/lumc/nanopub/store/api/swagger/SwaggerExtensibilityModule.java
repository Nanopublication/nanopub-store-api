package nl.lumc.nanopub.store.api.swagger;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mangofactory.swagger.configuration.ExtensibilityModule;
import com.mangofactory.swagger.models.AlternateTypeProcessingRule;
import com.mangofactory.swagger.models.TypeProcessingRule;

@Component("extensibilityModule")
public class SwaggerExtensibilityModule extends ExtensibilityModule {
	@Override
	protected void customizeExcludedResources(final List<String> excludedResources) {
		// prevents a "/home-controller" entry without any methods from showing up in the swagger ui.
		excludedResources.add("/home-controller");
	}
	
	@Override
	protected void customizeTypeProcessingRules(final List<TypeProcessingRule> typeProcessingRules) {
		typeProcessingRules.add(new AlternateTypeProcessingRule(URI.class, String.class));
	}
}
