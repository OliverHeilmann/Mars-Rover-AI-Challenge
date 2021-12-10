// Internal action code for project rover-AI

package mapping;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class updateMyPos extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        
    	//Get the only object available
    	mapSingleton object = mapSingleton.getInstance();
        
    	// get input args
        int dx = (int)((NumberTerm) args[0]).solve();
    	int dy = (int)((NumberTerm) args[1]).solve();
    	String agentName = args[2].toString();
    	
    	// Create an array for passing to the SingleObject class function
    	Integer[] me_to_base = new Integer[2];

    	me_to_base[0] = dx; 
    	me_to_base[1] = dy;
    
    	// now update my position on the map
    	object.updateMyLocLogger(agentName, me_to_base);
    	
        // everything ok, so returns true
        return true;
    }
}
