
package EngineOpenLibrary;

import java.util.List;

public class ISBN {
   	private List authors;
   	private Cover cover;
   	private Identifiers identifiers;
   	private String key;
   	private Number number_of_pages;
   	private String publish_date;
   	private List publishers;
   	private List subjects;
   	private String subtitle;
   	private String title;
   	private String url;
   	private String weight;

 	public List getAuthors(){
		return this.authors;
	}
	public void setAuthors(List authors){
		this.authors = authors;
	}
 	public Cover getCover(){
		return this.cover;
	}
	public void setCover(Cover cover){
		this.cover = cover;
	}
 	public Identifiers getIdentifiers(){
		return this.identifiers;
	}
	public void setIdentifiers(Identifiers identifiers){
		this.identifiers = identifiers;
	}
 	public String getKey(){
		return this.key;
	}
	public void setKey(String key){
		this.key = key;
	}
 	public Number getNumber_of_pages(){
		return this.number_of_pages;
	}
	public void setNumber_of_pages(Number number_of_pages){
		this.number_of_pages = number_of_pages;
	}
 	public String getPublish_date(){
		return this.publish_date;
	}
	public void setPublish_date(String publish_date){
		this.publish_date = publish_date;
	}
 	public List getPublishers(){
		return this.publishers;
	}
	public void setPublishers(List publishers){
		this.publishers = publishers;
	}
 	public List getSubjects(){
		return this.subjects;
	}
	public void setSubjects(List subjects){
		this.subjects = subjects;
	}
 	public String getSubtitle(){
		return this.subtitle;
	}
	public void setSubtitle(String subtitle){
		this.subtitle = subtitle;
	}
 	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title = title;
	}
 	public String getUrl(){
		return this.url;
	}
	public void setUrl(String url){
		this.url = url;
	}
 	public String getWeight(){
		return this.weight;
	}
	public void setWeight(String weight){
		this.weight = weight;
	}
}
