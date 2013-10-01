package experiment.dao;


import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;


public class Experiment extends BasicDBObject {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DBCollection experiments = Connection.getInstance().getCollection("Experiments");
	private int id;
	private String name;
	private List<Agent> agents;
	private List<Task> tasks;
	private List<State> states;
	
	
	public Experiment(int id, String name)
	{
		
		this.setId(id);
		this.setName(name);
		this.setAgents(new ArrayList<Agent>());
		this.setTasks(new ArrayList<Task>());
		this.setStates(new ArrayList<State>());

		this.postData();
		
		
		
	}
	
	private WriteResult postData(){
		
		put("_id", id);
		put("name", name);
		put("agents", this.getAgents());
		put("tasks", this.getTasks());
		put("states", this.getStates());
		return experiments.insert(this);
	
	}
	
	private Object getData(String field){
		return experiments.find(this).next().get(field);
	}
	
	public int getId() {
		
		return (int)this.getData("_id");
		
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return (String)this.getData("name");
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Agent> getAgents() {
		return agents;
	}
	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	public List<State> getStates() {
		return states;
	}
	public void setStates(List<State> states) {
		this.states = states;
	}
	

}
