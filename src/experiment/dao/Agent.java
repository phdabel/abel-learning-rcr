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
	
	private Experiment experiment = null;
	private int entityID;
	private String agentType;
	private int resources;
	private List<BasicDBObject> position;
	
	public Agent(){}
	
	public Agent(Experiment experiment, Integer entityID, String agentType, Integer resources){
		
		this.setExperiment(experiment);
		this.setEntityID(entityID);
		this.setAgentType(agentType);
		this.setResources(resources);
		this.setPosition(new ArrayList<BasicDBObject>());
		
		this.postData();
		
	}
	
	private void postData(){
		
		put("_id", entityID);
		put("agentType", agentType);
		put("resources", resources);
		put("positions", this.getPosition());
		agents.insert(this);
		
		BasicDBObject list = new BasicDBObject("agents", this);
		BasicDBObject updateQuery = new BasicDBObject().append("$push", list);
		this.getExperiment().experiments.update(this.getExperiment(), updateQuery);
/**
 * DBObject listItem = new BasicDBObject("scores", new BasicDBObject("type","quiz").append("score",99));
DBObject updateQuery = new BasicDBObject("$push", listItem);
myCol.update(findQuery, updateQuery);
 */
		
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
		return (int) this.getData("_id");
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

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	private Experiment getExperiment(){
		return this.experiment;
	}
	

	

}
