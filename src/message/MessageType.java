package message;

import java.io.Serializable;

public enum MessageType implements Serializable{
	
	BURNING_BUILDING("building"),
	AGENT_EXTINGUISH("extinguish"),
	AGENT_RELEASE("release"),
	ANNOUNCE_AGENT("announcement");
	
	String name;
	
	private MessageType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
