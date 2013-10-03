package experiment.dao;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
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
		this.tasks.ensureIndex(new BasicDBObject("id", id));
		put("experiment_id", experiment.getId());
		this.tasks.ensureIndex(new BasicDBObject("experiment_id", experiment.getId()));
		put("run_id", run.getId());
		this.tasks.ensureIndex(new BasicDBObject("run_id", run.getId()));
		this.tasks.ensureIndex(this.primaryKey);
		put("type", type);
		
		tasks.insert(this);
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
		return (String)this.getData("type");
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getTotalArea() {
		return (Integer)this.getData("totalArea");
	}
	public void setTotalArea(Integer totalArea) {
		this.totalArea = totalArea;
	}
	public Integer getFloor() {
		return (Integer)this.getData("floor");
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public Integer getCost() {
		return (Integer)this.getData("cost");
	}
	public void setCost(Integer cost) {
		this.cost = cost;
	}
	public Integer getFieryness() {
		return (Integer)this.getData("fieryness");
	}
	public void setFieryness(Integer fieryness) {
		this.fieryness = fieryness;
	}
	public Integer getTemperature() {
		return (Integer)this.getData("temperature");
	}
	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}
	public Integer getBrokenness() {
		return (Integer)this.getData("brokenness");
	}
	public void setBrokenness(Integer brokenness) {
		this.brokenness = brokenness;
	}

	public String getMatter() {
		return (String)this.getData("matter");
	}

	public void setMatter(String matter) {
		this.matter = matter;
	}
	
	public String getRun() {
		return (String)this.getData("run");
	}

	public void setRun(Run run) {
		this.run = run;
	}

	public String getExperiment() {
		return (String)this.getData("experiment_id");
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	

}
