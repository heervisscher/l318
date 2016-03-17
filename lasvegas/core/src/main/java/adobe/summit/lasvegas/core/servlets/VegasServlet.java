package adobe.summit.lasvegas.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.Node;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
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
// http://localhost:4510/bin/vegas?path=/content/aemcodingerrors/en
//

@Component(metatype=true)
@SlingServlet(paths="/bin/vegas", methods="GET", name="Las Vegas servlet", metatype=true, generateComponent=false)
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

		if (configuredProps != null)
		LOGGER.info("props" + configuredProps.length);
	}
	
	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		String path = request.getParameter("path");
		PageManager pm = request.getResourceResolver().adaptTo(PageManager.class);
		Page page = pm.getPage(path);
		
		if ( page != null && isEnabled()) {
			JSONObject json = new JSONObject();
			PrintWriter pw = response.getWriter();
			Node pageNode = page.getContentResource().adaptTo(Node.class);
			for (int i=0; i < configuredProps.length; i++) {
				try {
					json.put(configuredProps[i], getPropValue(pageNode, configuredProps[i]) );
				} catch (JSONException e) {
					
				}
			}
			pw.print(json.toString());
			pw.close();
			
		}
	}
	
	private String getPropValue( Node node, String propName) {
		try {
			return node.getProperty(propName).getString();
		} catch(Exception e) {
			return "";
		}
	}

	/**
	 * This servlet is enabled for author instances
	 * @return
	 */
	private boolean isEnabled() {
		Set<String> runModes = slingSettingsService.getRunModes();
		Iterator<String> runmodesIt = runModes.iterator();
		
		while (runmodesIt.hasNext()) {
			String runMode = runmodesIt.next();
			if ( runMode.equals("author")) {
				return true;
			}
		}
		return false;
	}
	
	
}
