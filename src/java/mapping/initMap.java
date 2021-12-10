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
    	int countOfAgents = (int)((NumberTerm) args[4]).solve();
    	
    	// everyone should add their name to the my location logger (for obstacle avoidance)
    	object.createMyLocLogger(agentName);
    	
    	// initialise matrix map size (first agent does it)
        if ( agentName.equals("collectorHEILMAO_") || agentName.equals("collectorHEILMAO_1") ||
        		agentName.equals("copmHEILMAO_") || agentName.equals("compHEILMAO_1")) { //object.triggerInit == false) {

            // create matrix of game dims
        	object.init(width, height, countOfAgents);
        }

        // everything ok, so returns true
        return true;
    }
}
