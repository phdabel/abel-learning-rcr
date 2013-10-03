package agent;

import static rescuecore2.misc.Handy.objectsToIDs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;

import experiment.dao.Agent;
import experiment.dao.Experiment;
import experiment.dao.Run;
import experiment.dao.State;
import experiment.dao.Task;
import message.MessageType;
import message.MyMessage;
import rescuecore2.log.Logger;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;
import sample.DistanceSorter;


public class LearningFireBrigade extends LearningAbstractAgent<FireBrigade> {
	private static final String MAX_WATER_KEY = "fire.tank.maximum";
    private static final String MAX_DISTANCE_KEY = "fire.extinguish.max-distance";
    private static final String MAX_POWER_KEY = "fire.extinguish.max-sum";

    private int maxWater;
    private int maxDistance;
    private int maxPower;
    private int currentTarget = 0;
    private Map<EntityID, Double>probability = new HashMap<EntityID, Double>();
    private Map<EntityID, Integer>walkCost = new HashMap<EntityID, Integer>();
    

    public LearningFireBrigade(Run run, int fb) {
		super();
		//ajusta o experimento para o valor passado como parâmetro no Launch
		this.setRun(run);
		this.agent = new Agent(run, fb, "fireBrigade", 5000);
		//this.agent.setCurrentPosition(me().getPosition().getValue());
		
	}

	@Override
    public String toString() {
        return "Learning Fire Brigade - Abel";
    }

    @Override
    protected void postConnect() {
        super.postConnect();
        model.indexClass(StandardEntityURN.BUILDING, StandardEntityURN.REFUGE);
        maxWater = config.getIntValue(MAX_WATER_KEY);
        maxDistance = config.getIntValue(MAX_DISTANCE_KEY);
        maxPower = config.getIntValue(MAX_POWER_KEY);
        Logger.info("Sample fire brigade connected: max extinguish distance = " + maxDistance + ", max power = " + maxPower + ", max tank = " + maxWater);
    }

    @Override
    protected void think(int time, ChangeSet changed, Collection<Command> heard) {
        if (time == config.getIntValue(kernel.KernelConstants.IGNORE_AGENT_COMMANDS_KEY)) {
            // Subscribe to channel 1
            sendSubscribe(time, 1);
            MyMessage eu = new MyMessage();
            eu.setType(MessageType.ANNOUNCE_AGENT);
            eu.setAgentID(me().getID());
            eu.setPosition(me().getPosition());
           
            this.sendMessage(time, 1, eu);
            
        }
        this.agent.updateHP(me().getHP());
        this.agent.updateWater(me().getWater());
        this.agent.appendPosition(me().getPosition().getValue());
        
        
        /**
         *  recebendo mensagens
         */
        this.heardMessage(heard);
    	for(MyMessage e : this.getReceivedMessage())
    	{
    		
    		switch(e.getType())
    		{
			case AGENT_EXTINGUISH:
				System.out.println("Agente "+e.getAgentID()+" extinguindo fogo em "+e.getBuildingID());
				this.allocationTable.put(e.getAgentID(), e.getBuildingID().getValue());
				
				break;
			case AGENT_RELEASE:
				System.out.println("Agente "+e.getAgentID()+" liberando tarefa");
				this.allocationTable.put(e.getAgentID(), 0);
				
				break;
			case ANNOUNCE_AGENT:
				if(!this.coleagues.contains(e.getAgentID()))
				{
					this.coleagues.add(e.getAgentID());
					this.allocationTable.put(e.getAgentID(), 0);
				}
				break;
			case BURNING_BUILDING:
				if(!this.tasks.containsKey(e.getBuildingID()))
				{
					this.tasks.put(e.getBuildingID(), e.getFieryness());
					this.utility.put(e.getBuildingID(), 0.0);
					this.probability.put(e.getBuildingID(), 0.0);
				}
				break;
			default:
				break;
    		
    		}
    	}
    	
    	/**
    	 * 
    	 * calculo do custo do caminho e custo maximo
    	 */
    	
    	Integer maxWalkCost = 0;
    	if(!this.tasks.isEmpty()){
    		for(EntityID b : this.tasks.keySet())
    		{
    			List<EntityID> path = search.breadthFirstSearch(me().getPosition(), b);
    			if(path != null){
    				Integer walkCostTmp = path.size(); 
    				this.walkCost.put(b, walkCostTmp);
    			
    				if(walkCostTmp > maxWalkCost)
    				{
    					maxWalkCost = walkCostTmp;
    				}
    			}
    		}
    	}
    	
    	/**
    	 * cálculo das probabilidades das ações possíveis
    	 * e retorno da ação de maior probabilidade
    	 * envio da ação que será feita
    	 */
    	if(this.currentTarget == 0){
    	
    		EntityID maxAction = null;
	    	Double maxProbability = 0.0;
	    	for(EntityID b : this.tasks.keySet())
	    	{
	    		Double cost = 0.0;
	    		if(this.walkCost.get(b) != null){
	    			cost = this.walkCost.get(b).doubleValue();
	    			
	    		}
	    		Double p1 = (Double)((1-cost)/maxWalkCost);
	    		Double e = Math.exp(this.utility.get(b)*this.tau);
	    		Double e2 = 0.0;
	    		for(int i = 0; i < this.coleagues.size(); i++)
	    		{
	    			e2 = e2 + e;
	    		}
	    		Double p2 = p1 * (e/e2);
	    		if(p2 > maxProbability)
	    		{
	    			maxProbability = p2;
	    			maxAction = b;
	    		}
	    		if(!this.tasks.isEmpty() && maxAction == null)
	    		{
	    			maxAction = this.tasks.keySet().iterator().next();
	    		}
	    		this.probability.put(b, p2);
	    	}
	    	if(maxAction != null){
	    		this.allocationTable.put(me().getID(), maxAction.getValue());
	    		if(this.currentTarget == 0){    		
	    			MyMessage extinguishMessage = new MyMessage(me().getID(), maxAction);
	    			this.sendMessage(time, 1, extinguishMessage);
	    			this.currentTarget = maxAction.getValue();
	    		}else if(this.currentTarget != maxAction.getValue()){
	    			MyMessage releaseMessage = new MyMessage(me().getID());
	        		this.sendMessage(time, 1, releaseMessage);
	        	
	        		this.currentTarget = maxAction.getValue();
	        		MyMessage extinguishMessage = new MyMessage(me().getID(), maxAction);
	    			this.sendMessage(time, 1, extinguishMessage);
	    		}
	    	
	    		/**
	    	 	* calculo da recompensa
	    	 	*/
	    		Double localReward = 0.0;
	    		Double noMeLocalReward = 0.0;
	    		int x = Collections.frequency(this.allocationTable.values(), maxAction.getValue());
	    		localReward = (x*Math.exp(-x/this.tasks.get(maxAction)));
	    		noMeLocalReward = ((x-1)*Math.exp((-(x-1))/this.tasks.get(maxAction)));
	    		Double reward = localReward - noMeLocalReward;
	    	
	    		/**
	    		 *  atualizacao da utilidade
	    	 	*/
	    		Double utilityTmp = (1 - this.alpha) * this.utility.get(maxAction) + this.alpha * reward;
	    		this.utility.put(maxAction, utilityTmp);
	    	}
    	}else{
    		Building b = (Building)model.getEntity(new EntityID(this.currentTarget));
    		int fieryness = 0;
    		if(b.isFierynessDefined())
    		{
    			fieryness = b.getFieryness();
    		}
    		int temperature = 0;
    		if(b.isTemperatureDefined())
    		{
    			temperature = b.getTemperature();
    		}
    		State s = new State(this.getRun(), time, me().getID().getValue(), 
    				this.currentTarget, me().getWater(), me().getHP(), fieryness, temperature);
    		
    		
    	}
        for (Command next : heard) {
            Logger.debug("Heard " + next);
        }
        FireBrigade me = me();
        // Are we currently filling with water?
        if (me.isWaterDefined() && me.getWater() < maxWater && location() instanceof Refuge) {
            Logger.info("Filling with water at " + location());
            sendRest(time);
            return;
        }
        // Are we out of water?
        if (me.isWaterDefined() && me.getWater() == 0) {
            // Head for a refuge
            List<EntityID> path = search.breadthFirstSearch(me().getPosition(), refugeIDs);
            if (path != null) {
                Logger.info("Moving to refuge");
                sendMove(time, path);
                return;
            }
            else {
                Logger.debug("Couldn't plan a path to a refuge.");
                path = randomWalk();
                Logger.info("Moving randomly");
                sendMove(time, path);
                return;
            }
        }
        
        // Find all buildings that are on fire
        Collection<EntityID> all = getBurningBuildings();
        //sending buildings message
        for (EntityID next : all) {
        	if(!this.tasks.containsKey(next)){
        		Building b = (Building)model.getEntity(next);
        		MyMessage buildingMessage = new MyMessage(b.getID(), b.getID(), b.getFieryness());
        		this.sendMessage(time, 1, buildingMessage);
        	}
        	
        }
        
     // Can we extinguish any right now?
        
        if(this.currentTarget != 0){
        	EntityID maxAction = new EntityID(this.currentTarget);
        	if(model.getDistance(getID(), maxAction) <= maxDistance)
    		{
    			Logger.info("Extinguishing " + maxAction);
            	sendExtinguish(time, maxAction, maxPower);
            	sendSpeak(time, 1, ("Extinguishing " + maxAction).getBytes());
            	this.currentTarget = 0;
            	return;        		
    		}
        }
        // Plan a path to a fire
        if(this.currentTarget != 0){
        	EntityID maxAction = new EntityID(this.currentTarget);
        	List<EntityID> caminho = planPathToFire(maxAction);
        	if(caminho != null)
        	{
        		Logger.info("Moving to target");
        		sendMove(time, caminho);
        		return;
        	}
        }
        
        List<EntityID> path = null;
        Logger.debug("Couldn't plan a path to a fire.");
        path = randomWalk();
        Logger.info("Moving randomly");
        sendMove(time, path);
    }

    @Override
    protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
        return EnumSet.of(StandardEntityURN.FIRE_BRIGADE);
    }

    private Collection<EntityID> getBurningBuildings() {
        Collection<StandardEntity> e = model.getEntitiesOfType(StandardEntityURN.BUILDING);
        List<Building> result = new ArrayList<Building>();
        for (StandardEntity next : e) {
            if (next instanceof Building) {
                Building b = (Building)next;
                if (b.isOnFire()) {
                	/**
                	 *  informações da tarefa
                	 */
                	Task t = new Task(this.getRun(), b.getID().getValue(), "Building");
                	t.updateBrokenness(b.getBrokenness());
                	t.updateFieryness(b.getFieryness());
                	t.updateFloor(b.getFloors());
                	t.updateTemperature(b.getTemperature());
                	t.updateTotalArea(b.getTotalArea());
                	/**
                	 * fim do envio de info da tarefa pro db
                	 */
                	
                    result.add(b);
                }
            }
        }
        // Sort by distance
        Collections.sort(result, new DistanceSorter(location(), model));
        return objectsToIDs(result);
    }

    private List<EntityID> planPathToFire(EntityID target) {
        // Try to get to anything within maxDistance of the target
        Collection<StandardEntity> targets = model.getObjectsInRange(target, maxDistance);
        if (targets.isEmpty()) {
            return null;
        }
        return search.breadthFirstSearch(me().getPosition(), objectsToIDs(targets));
    }

	@Override
	protected void stopCurrentTask() {
		// TODO Auto-generated method stub
		
	}	

}
