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
	private List<BasicDBObject> position;
	
	public Agent(){}
	
	public Agent(Run run, Integer entityID, String agentType, Integer resources){
		
		this.setRun(run);
		this.setExperiment(run.getExperiment());
		this.setEntityID(entityID);
		this.setAgentType(agentType);
		this.setResources(resources);
		this.setPosition(new ArrayList<BasicDBObject>());
		
		this.postData();
		
	}
	
	private void postData(){
		
		put("id", entityID);
		this.agents.ensureIndex(new BasicDBObject("id", entityID));
		put("run_id", run.getId());
		this.agents.ensureIndex(new BasicDBObject("run_id", run.getId()));
		put("experiment_id", experiment.getId());
		this.agents.ensureIndex(new BasicDBObject("experiment_id", experiment.getId()));
		put("agentType", agentType);
		put("resources", resources);
		put("positions", this.getPosition());
		agents.insert(this);
	
		
	}
	
	public void appendPosition(int position){
		
		agents.update(new BasicDBObject("id",this.entityID),
				new BasicDBObject("$push",
						new BasicDBObject("positions",position)),true,true);
		
	}
	
	/**
	 * adiciona um valor a lista de posições do agente
	 * @param position
	 */
	public void setCurrentPosition(Integer position){
		BasicDBObject pos = new BasicDBObject("position",position); 
		this.append("positions", pos);
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
	
	private Integer getRun(){
		return (int)this.getData("run.id");
	}

	public Integer getExperiment() {
		return (int)this.getData("experiment.id");
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	

	

}
