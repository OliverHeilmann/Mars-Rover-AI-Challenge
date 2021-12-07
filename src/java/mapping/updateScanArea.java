// Internal action code for project rover-AI

package mapping;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class updateScanArea extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	
    	//Get the only object available
    	mapSingleton object = mapSingleton.getInstance();
        
    	int scan_range = (int)((NumberTerm) args[0]).solve();
        int dx = (int)((NumberTerm) args[1]).solve();
    	int dy = (int)((NumberTerm) args[2]).solve();
    	
    	// Create an array for passing to the SingleObject class function
    	Integer[] me_to_base = new Integer[2];
    	
    	me_to_base[0] = dx; 
    	me_to_base[1] = dy;

		// create matrix of agent scan coverage area
    	//object.showNeighbourCoords(scan_range, me_to_base);
    	
        // everything ok, so returns true
        return true;
    }
}
