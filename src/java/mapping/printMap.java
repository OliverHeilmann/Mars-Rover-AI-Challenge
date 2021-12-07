// Internal action code for project rover-AI

package mapping;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class printMap extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	
    	//Get the only object available
		mapSingleton object = mapSingleton.getInstance();
        
    	//show the message
        String agentName = args[0].toString().replace("\"", "");        
        
        // check if main agent
        if (agentName.equals("agent_") || agentName.equals("agent_1")) {
        	// show singleton map of game table
        	//object.showMap();
        	
        	// show scan range map (coords are x,y to base)
        	//object.showNeighbourMatrix();
        }

        // everything ok, so returns true
        return true;
    }
}
