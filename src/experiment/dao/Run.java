package experiment.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

public class Run extends BasicDBObject{
	
	private static final long serialVersionUID = 1L;
	protected DBCollection runs = Connection.getInstance().getCollection("Runs");
	private int id;
	private Experiment experiment_id;
	private String date;
	private BasicDBObject primaryKey;
	
	public Run(int id, Experiment experiment_id, String date){
		
		this.setId(id);
		this.setExperiment_id(experiment_id);
		this.setDate(date);
		this.primaryKey = new BasicDBObject("id",this.id).append("experiment_id", experiment_id.getId());
		this.create();
		
	}
	
	private WriteResult create(){
		
		put("id", id);
		this.runs.ensureIndex(new BasicDBObject("id", id));
		put("experiment_id", experiment_id.getId());
		this.runs.ensureIndex(new BasicDBObject("experiment_id", experiment_id.getId()));
		runs.ensureIndex(this.primaryKey);
		put("date", date);
		return runs.insert(this);
		
	}
	
	private Object getData(String field){
		return runs.find(this).next().get(field);
	}
	
	public int getId() {
		return (int)this.getData("id");
	}
	
	public Experiment getExperiment(){
		return this.experiment_id;
	}
	
	public int getExperiment_id() {
		return (int)this.getData("experiment_id");
	}
	
	public String getDate() {
		return (String)this.getData("date");
	}
	
	public void setExperiment_id(Experiment experiment_id) {
		this.experiment_id = experiment_id;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	
}
