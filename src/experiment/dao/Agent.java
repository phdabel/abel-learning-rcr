package experiment.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class Agent extends BasicDBObject {
	
	DBCollection collection = Connection.getInstance().getCollection("Agents");
	
	private int entityID;
	private String agentType;
	private int resources;
	
	public Agent(Integer entityID, String agentType, Integer resources){
		
		
		
	}

	public int getEntityID() {
		return entityID;
	}

	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public int getResources() {
		return resources;
	}

	public void setResources(int resources) {
		this.resources = resources;
	}
	
	

	

}
