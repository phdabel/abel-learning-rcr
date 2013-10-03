package experiment.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class State extends BasicDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DBCollection states = Connection.getInstance().getCollection("States");
	
	private int time;
	private int agent_id;
	private int task_id;
	private Experiment experiment_id;
	private Run run_id;
	
	private BasicDBObject primaryKey;
	
	private int waterAgent;
	private int hpAgent;
	private int fieryness;
	private int temperature;
	
	public State(Run run, int time, int agent_id, int task_id, 
			int waterAgent, int hpAgent, int fieryness, int temperature){
		
		this.setRun_id(run);
		this.setExperiment_id(run.getExperiment());
		this.setAgent_id(agent_id);
		this.setTask_id(task_id);
		this.setWaterAgent(waterAgent);
		this.setHpAgent(hpAgent);
		this.setFieryness(fieryness);
		this.setTemperature(temperature);
		
		this.primaryKey = new BasicDBObject("time", time)
		.append("agent_id", agent_id)
		.append("task_id", task_id)
		.append("experiment_id", run.getExperiment().getId())
		.append("run_id", run.getId());
		
		this.create();
		
	}
	
	private Object getData(String field){
		return states.find(this).next().get(field);
	}
	
	private void create(){
		
		put("time", time);
		put("agent_id", agent_id);
		put("task_id", task_id);
		put("experiment_id", experiment_id.getId());
		put("run_id", run_id.getId());
		
		if(states.count()==0){
			this.states.ensureIndex(new BasicDBObject("time", time));
			this.states.ensureIndex(new BasicDBObject("agent_id", agent_id));
			this.states.ensureIndex(new BasicDBObject("run_id", run_id.getId()));
			this.states.ensureIndex(this.primaryKey);
			this.states.ensureIndex(new BasicDBObject("task_id", task_id));
			this.states.ensureIndex(new BasicDBObject("experiment_id", experiment_id.getId()));
		}
		
		put("waterAgent", waterAgent);
		put("hpAgent", hpAgent);
		put("fieryness", fieryness);
		put("temperature", temperature);
		
		if(states.count(this.primaryKey)==0){
			states.insert(this);
		}
	}
	
	public int getTime() {
		return (int)this.getData("time");
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getAgent_id() {
		return (int)this.getData("agent_id");
	}
	public void setAgent_id(int agent_id) {
		this.agent_id = agent_id;
	}
	public int getTask_id() {
		return (int)this.getData("task_id");
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public int getExperiment_id() {
		return (int)this.getData("experiment_id");
	}
	public void setExperiment_id(Experiment experiment_id) {
		this.experiment_id = experiment_id;
	}
	public int getRun_id() {
		return (int)this.getData("run_id");
	}
	public void setRun_id(Run run_id) {
		this.run_id = run_id;
	}
	public int getWaterAgent() {
		return (int)this.getData("waterAgent");
	}
	public void setWaterAgent(int waterAgent) {
		this.waterAgent = waterAgent;
	}
	public int getHpAgent() {
		return (int)this.getData("hpAgent");
	}
	public void setHpAgent(int hpAgent) {
		this.hpAgent = hpAgent;
	}
	public int getFieryness() {
		return (int)this.getData("fieryness");
	}
	public void setFieryness(int fieryness) {
		this.fieryness = fieryness;
	}
	public int getTemperature() {
		return (int)this.getData("temperature");
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	
	
	
}
