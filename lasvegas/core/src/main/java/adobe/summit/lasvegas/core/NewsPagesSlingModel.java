package adobe.summit.lasvegas.core;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;


@Model(adaptables=Resource.class)
public class NewsPagesSlingModel {
	
	@Inject @Optional
	List<Resource> par; // select all Resources under the "par" node
	
	public Resource textResource;
	
	public Resource imageResource;
	
	@PostConstruct
	protected void init() {
		if ( par != null ) {
			// looping through all resource and check the first image and text component
			par.forEach( resource -> selectResource(resource));
		}
	}
	
	private void selectResource(Resource r) {
		if ( textResource == null && r.getResourceType().endsWith("text")) {
			textResource = r;
		} else if ( imageResource == null && r.getResourceType().endsWith("image") ) {
			imageResource = r;
		}
	}
	
}
