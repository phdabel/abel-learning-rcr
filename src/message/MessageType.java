package message;

import java.io.Serializable;

public enum MessageType implements Serializable{
	
	BURNING_BUILDING("Announcement"),
	AGENT_EXTINGUISH("Agent going to extinguish fire"),
	AGENT_RELEASE("Agent releasing building"),
	ANNOUNCE_AGENT("Agent announcement");
	
	String name;
	
	private MessageType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
