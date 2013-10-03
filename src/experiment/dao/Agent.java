package experiment.dao;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

public class Agent extends BasicDBObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DBCollection agents = Connection.getInstance().getCollection("Agents");
	
	private Run run = null;
	private Experiment experiment = null;
	private int entityID;
	private String agentType;
	private int resources;
	private int hp;
	private int water;
	private List<BasicDBObject> position;
	private BasicDBObject primaryKey;
	
	public Agent(Run run, Integer entityID, String agentType, Integer resources){
		
		this.setRun(run);
		this.setExperiment(run.getExperiment());
		this.setEntityID(entityID);
		this.setAgentType(agentType);
		this.setResources(resources);
		this.setPosition(new ArrayList<BasicDBObject>());
		this.primaryKey = new BasicDBObject("entityID",this.entityID)
		.append("run_id", run.getId())
		.append("experiment_id", experiment.getId());
		this.create();
		
	}
	
	//atualiza o agente inserindo o valor para agua
	//se o campo nao existe, ele é criado para o registro
	public WriteResult updateWater(int water){
		return agents.update(this.primaryKey, 
				new BasicDBObject("$set", 
						new BasicDBObject("water", water)));
	}
	
	public WriteResult updateHP(int hp){
		return agents.update(
				this.primaryKey, 
				new BasicDBObject("$set", new BasicDBObject("hp", hp)));
	}
	
	private void create(){
		
		put("id", entityID);
		this.agents.ensureIndex(new BasicDBObject("id", entityID));
		put("run_id", run.getId());
		this.agents.ensureIndex(new BasicDBObject("run_id", run.getId()));
		put("experiment_id", experiment.getId());
		this.agents.ensureIndex(new BasicDBObject("experiment_id", experiment.getId()));
		put("agentType", agentType);
		put("resources", resources);
		put("positions", this.getPosition());
		this.agents.ensureIndex(this.primaryKey);
		agents.insert(this);
	}
	
	// insere item na lista
	// positions = [ 12, 2, 34, 4]
	//
	public void appendPosition(int position){
		
		agents.update(this.primaryKey,
				new BasicDBObject("$push",
						new BasicDBObject("positions",position)),true,true);
		
	}
	
	private Object getData(String field){
		return agents.find(this).next().get(field);
	}

	public int getEntityID() {
		return (int) this.getData("id");
	}

	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	public String getAgentType() {
		return (String)this.getData("agentType");
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public int getResources() {
		return (int)this.getData("resources");
	}

	public void setResources(int resources) {
		this.resources = resources;
	}

	public List<BasicDBObject> getPosition() {
		return position;
	}

	public void setPosition(List<BasicDBObject> position) {
		this.position = position;
	}

	public void setRun(Run run) {
		this.run = run;
	}
	
	private int getRun(){
		return (int)this.getData("run_id");
	}

	public Integer getExperiment() {
		return (int)this.getData("experiment_id");
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public int getHp() {
		return (int)this.getData("hp");
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getWater() {
		return (int)this.getData("water");
	}

	public void setWater(int water) {
		this.water = water;
	}
	

	

}
