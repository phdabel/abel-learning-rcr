package experiment.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;


public class Experiment extends BasicDBObject {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DBCollection experiments = Connection.getInstance().getCollection("Experiments");
	private int id;
	private String name;
	private String date;
	
	public Experiment(int id, String name, String date)
	{
		
		this.setId(id);
		this.setName(name);
		this.setDate(date);
		
		
		this.postData();
		
		
		
	}
	
	public DBCollection getExperiment(){
		return this.experiments;
	}
	
	private void postData(){
		
		put("_id", id);
		this.experiments.ensureIndex(new BasicDBObject("_id", id));
		put("name", name);
		put("date", date);
		if(experiments.find(this).count() == 0){
			experiments.insert(this);
		}
		
	}
	
	private Object getData(String field){
		return experiments.find(this).next().get(field);
	}
	
	public int getId() {
		
		return (int)this.getData("_id");
		
	}
	
	public String getName() {
		return (String)this.getData("name");
	}
	
	public String getDate() {
		return (String)this.getData("date");
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	

}
