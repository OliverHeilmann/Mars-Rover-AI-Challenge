// Internal action code for project rover-AI

package movement;

import java.util.ArrayList;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import mapping.mapSingleton;

public class aStarRoute extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        
    	//Get the only object available
    	mapSingleton object = mapSingleton.getInstance();
    	
    	// get input args
        int start_dx = (int)((NumberTerm) args[0]).solve();
    	int start_dy = (int)((NumberTerm) args[1]).solve();
    	
        int end_dx = (int)((NumberTerm) args[2]).solve();
    	int end_dy = (int)((NumberTerm) args[3]).solve();
    	
    	// Create an array for passing to the SingleObject class function
    	int[] me_to_base = new int[2];
    	int[] tile_to_base = new int[2];
    	me_to_base[0] = start_dx; 
    	me_to_base[1] = start_dy;
    	tile_to_base[0] = end_dx; 
    	tile_to_base[1] = end_dy;

    	// now calculate the best route to take from start to end points
    	ArrayList<int[]> agentPath = object.calcAStarRoute(me_to_base, tile_to_base);
    	
    	//System.out.println(agentPath);
    	
        // everything ok, so returns true
        return true;
    }
}
