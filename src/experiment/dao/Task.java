package experiment.dao;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class Task extends BasicDBObject{

	private int id;
	private String type;
	private Integer totalArea;
	private Integer floor;
	private Integer cost;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getTotalArea() {
		return totalArea;
	}
	public void setTotalArea(Integer totalArea) {
		this.totalArea = totalArea;
	}
	public Integer getFloor() {
		return floor;
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public Integer getCost() {
		return cost;
	}
	public void setCost(Integer cost) {
		this.cost = cost;
	}
	

}
