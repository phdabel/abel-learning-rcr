package experiment.dao;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class Allocation extends BasicDBObject {

	private int id;
	private Agent agent;
	private List<Task> tasks;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Agent getAgent() {
		return agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
}
