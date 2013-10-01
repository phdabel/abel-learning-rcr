package experiment.dao;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class State extends BasicDBObject {

	private int time;
	private List<Allocation> allocations;
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public List<Allocation> getAllocations() {
		return allocations;
	}
	public void setAllocations(List<Allocation> allocations) {
		this.allocations = allocations;
	}
	
}
