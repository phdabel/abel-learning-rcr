package agent;

import static rescuecore2.misc.Handy.objectsToIDs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import message.MessageType;
import message.MyMessage;
import message.Serializer;

import rescuecore2.Constants;
import rescuecore2.log.Logger;
import rescuecore2.messages.Command;

import rescuecore2.worldmodel.EntityID;
import rescuecore2.standard.components.StandardAgent;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.kernel.comms.ChannelCommunicationModel;
import rescuecore2.standard.kernel.comms.StandardCommunicationModel;
import rescuecore2.standard.messages.AKSpeak;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.Road;

import sample.SampleSearch;
/**
Abstract base class for MyAgent.
@param <E> The subclass of StandardEntity this agent wants to control.
*/
public abstract class LearningAbstractAgent<E extends StandardEntity> extends StandardAgent<E> {
	
	private static final int RANDOM_WALK_LENGTH = 50;
	protected Double tau = 0.5;
	protected Double alpha = 0.5;

    private static final String SAY_COMMUNICATION_MODEL = StandardCommunicationModel.class.getName();
    private static final String SPEAK_COMMUNICATION_MODEL = ChannelCommunicationModel.class.getName();
    
    private int communicationChannel;
    /**
       The search algorithm.
       Default from Sample module
       take all the world and makes a bread first search
    */
    protected SampleSearch search;

    /**
       Whether to use AKSpeak messages or not.
    */
    protected boolean useSpeak;
    
    /**
     *  lista de agentes
     */
    protected List<EntityID> coleagues = new ArrayList<EntityID>();
    
    /**
     * lista de tarefas
     */
    protected Map<EntityID, Integer> tasks = new HashMap<EntityID, Integer>();
    
    /**
     *  utilidade das tarefas
     */
    protected Map<EntityID, Double> utility = new HashMap<EntityID, Double>(); 

    protected Map<EntityID, Integer> allocationTable = new HashMap<EntityID, Integer>();
    
    /**
       Cache of building IDs.
    */
    protected List<EntityID> buildingIDs;

    /**
       Cache of road IDs.
    */
    protected List<EntityID> roadIDs;

    /**
       Cache of refuge IDs.
    */
    protected List<EntityID> refugeIDs;

    //private Map<EntityID, Set<EntityID>> neighbours;
    protected boolean	channelComm;
    
    private ArrayList<MyMessage> receivedMessages = new ArrayList<MyMessage>();
    private Map<EntityID, Set<EntityID>> neighbours;
    
    /*
     * Constructor of MyAbstractAgent
     */
    protected LearningAbstractAgent(){}
    
    /**
     * Connects the agent to the simulation
     */
    @Override
    protected void postConnect() {
        super.postConnect();
        //creates arrays list for buildings, roads and refuges of the world model
        
        buildingIDs = new ArrayList<EntityID>();
        roadIDs = new ArrayList<EntityID>();
        refugeIDs = new ArrayList<EntityID>();
        
        
        //assign values to buildings, roads and refuges according to model
        for (StandardEntity next : model) {
            if (next instanceof Building) {
                buildingIDs.add(next.getID());
            }
            if (next instanceof Road) {
                roadIDs.add(next.getID());
            }
            if (next instanceof Refuge) {
                refugeIDs.add(next.getID());
            }
      
        }
         
        /**
         * sets communication via radio
         */
        boolean speakComm = config.getValue(Constants.COMMUNICATION_MODEL_KEY).equals(ChannelCommunicationModel.class.getName());

        int numChannels = this.config.getIntValue("comms.channels.count");
        
        if((speakComm) && (numChannels > 1)){
        	this.channelComm = true;
        }else{
        	this.channelComm = false;
        }
        
        /*
         *  Instantiate a new SampleSearch
         *  Sample Search creates a graph for the world model
         *  and implements a bread first search for use as well.
         */
        search = new SampleSearch(model);

        neighbours = search.getGraph();
        useSpeak = config.getValue(Constants.COMMUNICATION_MODEL_KEY).equals(SPEAK_COMMUNICATION_MODEL);
        Logger.debug("Modelo de Comunicação: " + config.getValue(Constants.COMMUNICATION_MODEL_KEY));
        Logger.debug(useSpeak ? "Usando modelo SPEAK" : "Usando modelo SAY");
    }
    
   
    
    protected Double euclidianDistance(int x, int x0, int y, int y0)
    {
    	Double norma = 0.0;
    	norma = (double) (((x - x0)^2)+(y - y0)^2);
    	Double result = Math.sqrt(norma);
		return result;
    	
    }
        
    /**
     * 
     * @param targetPosition EntityID do alvo
     * @param range distancia maxima
     * @return retorna objetos dentro de um raio informado
     */
    protected Collection<StandardEntity> tasksInRange(EntityID targetPosition, int range)
    {
    	Collection<StandardEntity> targets = model.getObjectsInRange(targetPosition, range);
    	return targets;
    }
    
    /**
     * 
     * @param sourcePosition - location of the agent
     * @param targetPosition - location of the target 
     * @param range
     * @return
     */
    protected List<EntityID> planPathToTask(EntityID sourcePosition, EntityID targetPosition, int range) {
        
        Collection<StandardEntity> targets = model.getObjectsInRange(targetPosition, range);
        if (targets.isEmpty()) {
            return null;
        }
        List<EntityID> shortestPath = new ArrayList<EntityID>();
        for(EntityID t : objectsToIDs(targets))
        {
        	List<EntityID> path = search.breadthFirstSearch(sourcePosition, t);
        	//List<EntityID> path = this.getDijkstraPath(sourcePosition, t);
        	if(shortestPath.isEmpty())
        	{
        		shortestPath = path;
        	}else{
        		if(path.size() < shortestPath.size())
        		{
        			shortestPath = path;
        		}
        	}
        }
        return shortestPath;
    }
    
    
	
	protected void sendMessage(int time, int channel, MyMessage message) {
        byte[] speak = null;
        try {
            speak = Serializer.serialize(message.toString());
            sendSpeak(time, channel, speak);
//            System.out.println("Mensagem de enviada com sucesso");
            Logger.debug("Mensagem enviado com sucesso");
        } catch (IOException e) {
        	e.printStackTrace();
            Logger.error("IoException ao gerar mensagem de " + e.getMessage());
//            System.out.println("Erro ao enviar a mensagem   ");
        }
    }

    protected void heardMessage(Collection<Command> heard) {
    	this.getReceivedMessage().clear();
        for (Command next : heard) {
            if (next instanceof AKSpeak) {
                byte[] msg = ((AKSpeak) next).getContent();
                try {
                    Object object = Serializer.deserialize(msg);
                    if (object instanceof String) {
                    	String message = (String)object;
                    	
                        String[] s = message.split("\\|");
                    	                    	
                    	switch(s[0])
                    	{
                    		case "building":
                    			//EntityID buildingID, EntityID position, Integer fieryness
                    			MyMessage building = new MyMessage(
                    					new EntityID(Integer.parseInt(s[1])),
                    					new EntityID(Integer.parseInt(s[2])),
                    					Integer.parseInt(s[3])
                    					);
                    			this.getReceivedMessage().add(building);
                    			break;
                    		case "extinguish":
                    			MyMessage extinguish = new MyMessage(
                    					new EntityID(Integer.parseInt(s[1])),
                    					new EntityID(Integer.parseInt(s[2]))
                    					);
                    			this.getReceivedMessage().add(extinguish);
                    			break;
                    		case "release":
                    			MyMessage release = new MyMessage(
                    					new EntityID(Integer.parseInt(s[1])));
                    			this.getReceivedMessage().add(release);
                    			break;
                    		case "announcement":
                    			MyMessage announce = new MyMessage();
                    			announce.setType(MessageType.ANNOUNCE_AGENT);
                    			announce.setAgentID(new EntityID(Integer.parseInt(s[1])));
                    			announce.setPosition(new EntityID(Integer.parseInt(s[2])));
                    			this.getReceivedMessage().add(announce);
                    			break;
                    	}
                    }else{
                    	                    	
                    	System.out.println("Mensagem desconhecida "+object.toString());
                    }
                } catch (IOException e) {
                    Logger.error("Não entendi a mensagem!" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    Logger.error("Mensagem veio com classe que não conheço.");
                }
            }
        }
    }
    
       
  	public ArrayList<MyMessage> getReceivedMessage() {
		return receivedMessages;
	}

	public void setReceivedMessage(ArrayList<MyMessage> receivedMessage) {
		this.receivedMessages = receivedMessage;
	}

	public int getCommunicationChannel() {
		return communicationChannel;
	}

	public void setCommunicationChannel(int communicationChannel) {
		this.communicationChannel = communicationChannel;
	}
	
	protected abstract void stopCurrentTask();

	protected List<EntityID> randomWalk() {
        List<EntityID> result = new ArrayList<EntityID>(RANDOM_WALK_LENGTH);
        Set<EntityID> seen = new HashSet<EntityID>();
        EntityID current = ((Human)me()).getPosition();
        for (int i = 0; i < RANDOM_WALK_LENGTH; ++i) {
            result.add(current);
            seen.add(current);
            List<EntityID> possible = new ArrayList<EntityID>(neighbours.get(current));
            Collections.shuffle(possible, random);
            boolean found = false;
            for (EntityID next : possible) {
                if (seen.contains(next)) {
                    continue;
                }
                current = next;
                found = true;
                break;
            }
            if (!found) {
                // We reached a dead-end.
                break;
            }
        }
        return result;
    }

}
