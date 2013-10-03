package experiment.dao;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class Task extends BasicDBObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DBCollection tasks = Connection.getInstance().getCollection("Tasks");
	
	private Run run = null;
	private Experiment experiment = null;
	private int id;
	private String type;
	private Integer totalArea;
	private Integer floor;
	private Integer fieryness;
	private Integer temperature;
	private Integer brokenness;
	private String matter;
	private Integer cost;
	private BasicDBObject primaryKey;
	
	public Task(Run run, int id, String type){
		
		this.setRun(run);
		this.setExperiment(run.getExperiment());
		this.setId(id);
		this.setType(type);
		this.primaryKey = new BasicDBObject("id", id)
		.append("experiment_id", experiment.getId())
		.append("run_id", run.getId());
		this.create();
		
	}
	
	private Object getData(String field){
		return tasks.find(this).next().get(field);
	}
	
	private void create(){
		
		put("id", id);
		put("experiment_id", experiment.getId());
		put("run_id", run.getId());
		put("type", type);
		
		if(tasks.count()==0){
			this.tasks.ensureIndex(new BasicDBObject("id", id));
			this.tasks.ensureIndex(new BasicDBObject("experiment_id", experiment.getId()));
			this.tasks.ensureIndex(new BasicDBObject("run_id", run.getId()));
			this.tasks.ensureIndex(this.primaryKey);
			
		}
		
		if(tasks.find(this.primaryKey).count() == 0){
			
			tasks.insert(this);
		}else{
			DBObject object = tasks.find(this.primaryKey).next();
			
			this.setId((int)object.get("id"));
			this.setTemperature((int)object.get("temperature"));
			this.setFieryness((int)object.get("fieryness"));
			this.setTotalArea((int)object.get("totalArea"));
			this.setFloor((int)object.get("floor"));
			this.setMatter((String)object.get("matter"));
			if(object.containsField("cost")){
				this.setCost((int)object.get("cost"));
			}
			if(object.containsField("type")){
				this.setType((String)object.get("type"));
			}
			
			
		}
	}
	
	//update com inserção de campo
	public WriteResult updateTotalArea(int totalArea)
	{
		return tasks.update(this.primaryKey, 
				new BasicDBObject("$set", 
						new BasicDBObject("totalArea", totalArea)));
	}
	
	public WriteResult updateFloor(int floor)
	{
		return tasks.update(this.primaryKey, 
				new BasicDBObject("$set", 
						new BasicDBObject("floor", floor)));
	}
	
	public WriteResult updateFieryness(int fieryness)
	{
		return tasks.update(this.primaryKey, 
				new BasicDBObject("$set", 
						new BasicDBObject("fieryness", fieryness)));
	}
	
	public WriteResult updateTemperature(int temperature)
	{
		return tasks.update(this.primaryKey, 
				new BasicDBObject("$set", 
						new BasicDBObject("temperature", temperature)));
	}
	
	public WriteResult updateBrokenness(int brokenness)
	{
		return tasks.update(this.primaryKey, 
				new BasicDBObject("$set", 
						new BasicDBObject("brokenness", brokenness)));
	}
	
	public WriteResult updateMatter(int matter)
	{
		return tasks.update(this.primaryKey, 
				new BasicDBObject("$set", 
						new BasicDBObject("matter", matter)));
	}
	
	public WriteResult updateCost(int cost)
	{
		return tasks.update(this.primaryKey, 
				new BasicDBObject("$set", 
						new BasicDBObject("cost", cost)));
	}	
	
	
	public int getId() {
		return (Integer)this.getData("id");
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		if(this.containsField("type")){
			return (String)this.getData("type");
		}else{
			return null;
		}
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getTotalArea() {
		if(this.containsField("totalArea")){
			return (Integer)this.getData("totalArea");
		}else{
			return null;
		}
	}
	public void setTotalArea(Integer totalArea) {
		this.totalArea = totalArea;
	}
	public Integer getFloor() {
		if(this.containsField("floor")){
			return (Integer)this.getData("floor");
		}else{
			return null;
		}
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public Integer getCost() {
		if(this.containsField("cost")){
			return (Integer)this.getData("cost");
		}else{
			return null;
		}
	}
	public void setCost(Integer cost) {
		this.cost = cost;
	}
	public Integer getFieryness() {
		if(this.containsField("fieryness")){
			return (Integer)this.getData("fieryness");
		}else{
			return null;
		}
	}
	public void setFieryness(Integer fieryness) {
		this.fieryness = fieryness;
	}
	public Integer getTemperature() {
		if(this.containsField("temperature")){
			return (Integer)this.getData("temperature");
		}else{
			return null;
		}
	}
	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}
	public Integer getBrokenness() {
		if(this.containsField("brokenness")){
			return (Integer)this.getData("brokenness");
		}else{
			return null;
		}
	}
	public void setBrokenness(Integer brokenness) {
		this.brokenness = brokenness;
	}

	public String getMatter() {
		if(this.containsField("matter")){
			return (String)this.getData("matter");
		}else{
			return null;
		}
	}

	public void setMatter(String matter) {
		this.matter = matter;
	}
	
	public String getRun() {
		if(this.containsField("run_id")){
			return (String)this.getData("run_id");
		}else{
			return null;
		}
	}

	public void setRun(Run run) {
		this.run = run;
	}

	public String getExperiment() {
		if(this.containsField("experiment_id")){
			return (String)this.getData("experiment_id");
		}else{
			return null;
		}
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	

}
