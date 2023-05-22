package demo.rest;

import java.util.ArrayList;
import java.util.List;

public class Note {

	private String title;
	private String content;
	private List<String> tags;
	
	private Note() {
		this.tags = new ArrayList<String>();
	}
	
	public Note(String title, String content) {
		this();
		this.title = title;
		this.content = content;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
}
