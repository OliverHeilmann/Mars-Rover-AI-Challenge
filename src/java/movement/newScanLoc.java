// Internal action code for project rover-AI

package movement;

import java.util.concurrent.ThreadLocalRandom;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import mapping.mapSingleton;

public class newScanLoc extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	// execute the internal action
    	
    	//Get the only object available
    	mapSingleton object = mapSingleton.getInstance();
        
    	// get input args
        int dx = (int)((NumberTerm) args[0]).solve();
    	int dy = (int)((NumberTerm) args[1]).solve();
    	int scan_range = (int)((NumberTerm) args[2]).solve();
    	
    	// get limits of maxDelta --> will be used to set limit of dX and dY
    	int maxDelta = (int)((NumberTerm) args[3]).solve();
    	
    	// Create an array for passing to the SingleObject class function
    	Integer[] me_to_base = new Integer[2];

    	me_to_base[0] = dx; 
    	me_to_base[1] = dy;
    	
    	int[] scanLocCoords = object.scanNewArea(me_to_base, scan_range);
    	
    	// if the whole map is scanned, return random dx, dy within range maxDelta
    	if (scanLocCoords[2] >= 1) {
        	// get random number
            scanLocCoords[0] = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
            scanLocCoords[1] = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
    	}

        // return random x and y
        return un.unifies(new NumberTermImpl(scanLocCoords[0]), args[4]) && 
        		un.unifies(new NumberTermImpl(scanLocCoords[1]), args[5]) && 
        		un.unifies(new NumberTermImpl(scanLocCoords[2]), args[6]);
    }
}
