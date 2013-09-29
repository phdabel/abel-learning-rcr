package message;

import java.io.Serializable;

import rescuecore2.worldmodel.EntityID;

/**
 *
 * @author rick
 */
public class MyMessage implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MessageType type;
	private EntityID position;
	private Integer fieryness;
	private EntityID agentID;
	private EntityID buildingID;
	
	
	public MyMessage(){
		
	}
	
	/**
	 * criar mensagem do tipo burning building
	 * @param buildingID
	 * @param position
	 * @param fieryness
	 */
	public MyMessage(EntityID buildingID, EntityID position, Integer fieryness){
		this.setType(MessageType.BURNING_BUILDING);
		this.setBuildingID(buildingID);
		this.setPosition(position);
		this.setFieryness(fieryness);
	}
	
	/**
	 * extinguish message
	 * @param agentID
	 * @param BuildingID
	 */
	public MyMessage(EntityID agentID, EntityID BuildingID)
	{
		this.setType(MessageType.AGENT_EXTINGUISH);
		this.setAgentID(agentID);
		this.setBuildingID(BuildingID);
	}
	
	/**
	 * release message
	 * @param agentID
	 */
	public MyMessage(EntityID agentID)
	{
		this.setType(MessageType.AGENT_RELEASE);
		this.setAgentID(agentID);
	}	
	
	public MessageType getType() {
		return type;
	}
	
	public void setType(MessageType type) {
		this.type = type;
	}
	
	public EntityID getPosition() {
		return position;
	}


	public void setPosition(EntityID position) {
		this.position = position;
	}


	public Integer getFieryness() {
		return fieryness;
	}


	public void setFieryness(Integer fieryness) {
		this.fieryness = fieryness;
	}


	public EntityID getAgentID() {
		return agentID;
	}


	public void setAgentID(EntityID agentID) {
		this.agentID = agentID;
	}


	public EntityID getBuildingID() {
		return buildingID;
	}


	public void setBuildingID(EntityID buildingID) {
		this.buildingID = buildingID;
	}
	
	public String toString(){
		String message = null;
		switch(this.type)
		{
		case AGENT_EXTINGUISH:
			message = this.type.toString()
					+"|"+this.getAgentID().getValue()
					+"|"+this.getBuildingID().getValue();
			break;
		case AGENT_RELEASE:
			message = this.type.toString()
					+"|"+this.getAgentID().getValue();
			break;
		case ANNOUNCE_AGENT:
			message = this.type.toString()
				+"|"+this.getAgentID().getValue()
				+"|"+this.getPosition().getValue();
			break;
		case BURNING_BUILDING:
			message = this.type.toString()
					+"|"+this.getBuildingID().getValue()
					+"|"+this.getPosition().getValue()
					+"|"+this.getFieryness().toString();
			break;
		}
		
		return message;
		
	}
	
	
	

}
