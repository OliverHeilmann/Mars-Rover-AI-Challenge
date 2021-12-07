// Internal action code for project rover-AI

package mapping;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class initMap extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

    	//Get the only object available
    	mapSingleton object = mapSingleton.getInstance();
        
        int width = (int)((NumberTerm) args[0]).solve();
    	int height = (int)((NumberTerm) args[1]).solve();
    	int scanRange = (int)((NumberTerm) args[2]).solve();
    	String agentName = args[3].toString();
    	
    	// initialise matrix map size (first agent does it)
        if (agentName.equals("agent_") || agentName.equals("agent_1")) {
            // create matrix of game dims
        	object.init(width, height);
        }

        // everything ok, so returns true
        return true;
    }
}
