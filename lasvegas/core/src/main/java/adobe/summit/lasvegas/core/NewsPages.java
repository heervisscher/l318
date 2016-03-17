package adobe.summit.lasvegas.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.oak.commons.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.api.SlingRepository;

import com.adobe.cq.sightly.WCMUse;
import com.adobe.granite.xss.XSSAPI;
import com.day.cq.wcm.api.Page;

public class NewsPages extends WCMUse {
	
	
	@Reference
	private SlingRepository repo;
	
	@Reference 
	private XSSAPI xssAPI;
	
	
	private Session session;

	@Override
	public void activate() throws Exception {
		if ( repo == null) {
			repo = getSlingScriptHelper().getService(SlingRepository.class);
		}
		if ( repo != null ) {
			session = repo.loginAdministrative(null);
		}
		xssAPI = getSlingScriptHelper().getService(XSSAPI.class);
	}
	
	/**
	 * Method to build and return a list of NewsPageModel, this will be iterated to list
	 * the subpages with the right properties
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<NewsPageModel> getNewsPages() throws Exception {
		List<NewsPageModel> returnvalue = new ArrayList<NewsPageModel>();
        Iterator<Page> pageIterator =  getCurrentPage().listChildren();
        boolean first = true;
        
        // Looking through the subpages
        while(pageIterator.hasNext()){
            Page page = pageIterator.next();
            Node pageNode = session.getNode(page.getPath());
            if ( pageNode != null && pageNode.hasNode("jcr:content")) {
            	Node contentNode = pageNode.getNode("jcr:content");
            	if ( contentNode != null) {
            		// check for hide in nav
            		if ( contentNode.hasProperty("hideInNav")) {
            			if ("true".equals(contentNode.getProperty("hideInNav"))) {
            				// skipping pages with hide in Nav
            				continue;
            			}
            		}
            		NewsPageModel model = new NewsPageModel(xssAPI);
            		model.setFirst(first);
            		if ( first) {
            			first = false;
            		}
            		model.setPath(pageNode.getPath());
            		model.setTitle(contentNode.getProperty("jcr:title").getString());
            		if ( contentNode.hasNode("par")) {
            			NodeIterator parNodes = contentNode.getNode("par").getNodes();
            			while ( parNodes.hasNext()) {
            				
            				Node parNode = parNodes.nextNode();
            				if ( parNode.getProperty("sling:resourceType").getString().endsWith("/text")) {
            					if ( model.getIntroText() == null ) {
            						model.setIntroText(parNode.getProperty("text").getString());
            					}
            				} else if ( parNode.getProperty("sling:resourceType").getString().endsWith("/image") ) {
            					if ( model.getImagePath() == null) {
            						model.setImagePath(parNode.getProperty("fileReference").getString());	
            					}
            				}
            			}
            		}
            		returnvalue.add(model);
            	}
            }
        }
		
		return returnvalue;
	}

}
