package adobe.summit.lasvegas.core;

import com.adobe.granite.xss.XSSAPI;

public class NewsPageModel {
	
	private String name;
	private String path;
	private String imagePath;
	private String introText;
	private String title;
	private boolean first;
	
	public boolean isFirst() {
		return first;
	}


	public void setFirst(boolean first) {
		this.first = first;
	}
	private XSSAPI xssapi;
	
	public NewsPageModel(XSSAPI xssapi) {
		this.xssapi = xssapi;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getIntroText() {
		if ( introText != null) {
			return xssapi.filterHTML(introText);
		}
		return introText;
	}
	public void setIntroText(String introText) {
		this.introText = introText;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

}
