package adobe.summit.lasvegas.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

//
// you can call the servlet like this to test it
// http://localhost:4510/content/aemcodingerrors/en/_jcr_content.pageinfo.json
//

@Component(metatype=true, 
	policy=ConfigurationPolicy.REQUIRE,
	description="Description of the servlet, shown in the configuration console",
	label="L318 Las Vegas pageinfo servlet")
@SlingServlet(resourceTypes="wcm/foundation/components/page", 
		selectors="pageinfo", extensions="json",
		methods="GET", generateComponent=false)
public class VegasServlet extends SlingAllMethodsServlet {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(VegasServlet.class);

	@Reference
	private SlingSettingsService slingSettingsService;
	
	@Property(label="Properties to be displayed", value={"jcr:title","jcr:description"})
	private static final String PROPERTY_NAMES = "propertyNames";
	
	private String[] configuredProps;
	
	@Activate
	public void activate(ComponentContext context) {
		configuredProps = (String[])  context.getProperties().get("propertyNames");

		if (configuredProps != null) {
			LOGGER.info("props" + configuredProps.length);
		}
	}
	
	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		PageManager pm = request.getResourceResolver().adaptTo(PageManager.class);
		Page page = pm.getContainingPage(request.getResource());
		
		if ( page != null ) {
			JSONObject json = getJSONForPage(page);
			PrintWriter pw = response.getWriter();
			pw.print(json.toString());
			pw.close();
			
		}
	}

	private JSONObject getJSONForPage(Page page) {
		JSONObject json = new JSONObject();
		ValueMap props = page.getProperties();
		for (int i=0; i < configuredProps.length; i++) {
			try {
				json.put(configuredProps[i], props.get(configuredProps[i], ""));
			} catch (JSONException e) {
				
			}
		}
		return json;
	}
	
}
