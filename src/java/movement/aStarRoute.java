// Internal action code for project rover-AI

package movement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
    	
    	// get limits of maxDelta --> will be used to set limit of dX and dY
    	int maxDelta = (int)((NumberTerm) args[4]).solve();
    	
    	// get agent name
    	String agentName = args[5].toString();
    	
    	// Create an array for passing to the SingleObject class function
    	int[] me_to_base = new int[2];
    	int[] tile_to_base = new int[2];
    	me_to_base[0] = start_dx; 
    	me_to_base[1] = start_dy;
    	tile_to_base[0] = end_dx; 
    	tile_to_base[1] = end_dy;

    	// now calculate the best route to take from start to end points
    	List<List<Integer>> agentPath = object.calcAStarRoute(agentName, me_to_base, tile_to_base);

    	// create list of listterms to pass back to ASL code
    	ListTermImpl aStarMoves = new ListTermImpl();
    	
    	// check if a result was returned
    	if (agentPath.size() > 0) {
    		
    		// some coords recieved, move to them
	        for (List<Integer> cmd : agentPath) {
	        	// init inner listterm
	        	ListTermImpl innerList = new ListTermImpl();
	
	        	// convert ints to terms
	        	NumberTermImpl dxNumTerm = new NumberTermImpl(cmd.get(0));
	        	NumberTermImpl dyNumTerm = new NumberTermImpl(cmd.get(1));
	
	        	// add to inner list
	        	innerList.add(dxNumTerm);
	        	innerList.add(dyNumTerm);
	        	
	        	/*
	        	System.out.print(dxNumTerm);
	        	System.out.print(", ");
	            System.out.println(dyNumTerm);
	            */
	            
	            // add to total list
	            aStarMoves.add(innerList);
	        }
    	}
    	/*
    	// else return 1 move of random values (to stop agents getting stuck)
    	else {
        	// init inner listterm
        	ListTermImpl innerList = new ListTermImpl();
        	
        	// set random scan area to DX, DY
    		int randX = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
    		int randY = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
        	
        	// convert ints to terms
        	NumberTermImpl dxNumTerm = new NumberTermImpl(randX);
        	NumberTermImpl dyNumTerm = new NumberTermImpl(randY);

        	// add to inner list
        	innerList.add(dxNumTerm);
        	innerList.add(dyNumTerm);
        	
            // add to total list
            aStarMoves.add(innerList);
    	}
        */
        // everything ok, so returns true
        return un.unifies(aStarMoves, args[6]);
    }
}