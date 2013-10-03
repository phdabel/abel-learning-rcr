package experiment.dao;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;

import java.net.UnknownHostException;


public class Connection {
	
	private static Connection instance = null;
	private DB database;
	private MongoClient mongoClient;
	
	private Connection(){
		
	}
	
	
	public static Connection getInstance(){
		if(instance == null)
		{
			instance = new Connection(); 
		}
		
		return instance;
	}
	
	public DB getDatabase() throws MongoException
	{
		
		try {
			this.mongoClient = new MongoClient("localhost",27017 );
			this.database = mongoClient.getDB("rescuedb");
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		}
		return this.database;
		
	}
	
	public MongoClient getMongoClient(){
		return this.mongoClient;
	}
	
	public DBCollection getCollection(String name)
	{
		DB db = getInstance().getDatabase();
		
		if(!db.collectionExists(name)){
			db.createCollection(name, new BasicDBObject());	
		}
		return db.getCollection(name);
		
	}


}
