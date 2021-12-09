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
    	
    	//Get resource type in Java format (may be "None")
        String resourceType = args[3].toString().replace("\"", "");

    	// resources currently carrying
    	int carryingResources = (int)((NumberTerm) args[4]).solve();
    	int maxResources = (int)((NumberTerm) args[5]).solve();
    	
    	// get limits of maxDelta --> will be used to set limit of dX and dY
    	int maxDelta = (int)((NumberTerm) args[6]).solve();
    	
    	// Create an array for passing to the SingleObject class function
    	Integer[] me_to_base = new Integer[2];

    	me_to_base[0] = dx; 
    	me_to_base[1] = dy;
    	
    	
    	// ------------------- DECIDE ON WHERE TO GO NEXT ------------------- //
    	int dirDX = 0;
    	int dirDY = 0;
    	
    	// if agent does not have a full cargo then it can look for a new tile to go to
    	if (carryingResources < maxResources | maxResources == 0) {
	    	// find all relative distances to my resource type
	    	int[][] allMyResourceList = object.myOnMapResources(resourceType, me_to_base);
	    	
	    	// loop through all resource locations in list, if there are any, these should
	    	// take priority over scanning new areas. Calculate the smallest vector distance
	    	// i.e. closest resource
	    	int i = 0; 
	    	Double result = 0.;
	    	Double vectorDist = Double.POSITIVE_INFINITY;
	    	while(allMyResourceList[i][0] != 0 && allMyResourceList[i][1] != 0) {
	    		
	    		// get vector distance = sqrt(x^2 + y^2)
	    		result = Math.pow(( Math.pow(allMyResourceList[i][0],2) + 
	    							Math.pow(allMyResourceList[i][1],2) ), 0.5);
	
	    		// if result distance is smaller than vector distance, it is closer i.e. go there
	    		if (result < vectorDist) {
	    			
	    			// set vectorDist to result
	    			vectorDist = result;
	    			
	    			// now set the intended direction to the resource coordinates
	    			dirDX = allMyResourceList[i][0];
	    			dirDY = allMyResourceList[i][1];
	    		}
	    		i += 1; // increment up list
	    	}
	    	
	    	// if there are no resources on the map, then get a new area to scan for
	    	if (dirDX == 0 && dirDY == 0) {
	    		// look for a new area to scan
	        	int[] scanLocCoords = object.scanNewArea(me_to_base, scan_range);
	        	
	        	// If scanLocCoords[2] is not 1 i.e. the map still has places to scan, then search it!
	        	// If not, check if agent is carrying resources, if yes, deposit them. If not, pass 
	        	// a random move within threshold...
	        	if (scanLocCoords[2] < 1) {
	        		dirDX = scanLocCoords[0];
	        		dirDY = scanLocCoords[1];
	        		
	        	}
	        	else {
	        		if (carryingResources > 0){
	        			// resources to depot (this was passed as the input)
	            		dirDX = -999;
	            		dirDY = -999;
	        		}
	        		else {
	        			// set random scan area to DX, DY
	            		dirDX = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
	            		dirDY = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
	        		}
	        	}
	    	}
    	}
    	// else agent has full capacity with resources, it must go to base!
    	else {
    		System.out.println("------------------------->>>> RTB!");
    		dirDX = dx;
        	dirDY = dy;
        	System.out.format("%d, %d", dx, dy);
        	System.out.println("");
    	}

        // return the best x and y given circumstances
        return un.unifies(new NumberTermImpl(dirDX), args[7]) && 
        		un.unifies(new NumberTermImpl(dirDY), args[8]);
    }
}
