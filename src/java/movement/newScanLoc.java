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
    	
    	// get limits of maxDelta --> will be used to set limit of dX and dY
    	int maxDelta = (int)((NumberTerm) args[5]).solve();
    	
    	// Create an array for passing to the SingleObject class function
    	Integer[] me_to_base = new Integer[2];

    	me_to_base[0] = dx; 
    	me_to_base[1] = dy;
    	
    	
    	// ------------------- DECIDE ON WHERE TO GO NEXT ------------------- //
    	int dirDX = 0;
    	int dirDY = 0;

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
    	/*
	   	String matVal = "(" + Integer.toString(dx) + ", " + 
								Integer.toString(dy) + ") || ";

		matVal = matVal + Integer.toString(dirDX) + ", " +
		   					Integer.toString(dirDY);
		System.out.println(matVal);
		*/
    	
    	// look for a new area to scan
    	int[] scanLocCoords = object.scanNewArea(me_to_base, scan_range);
    	
    	// if the whole map is scanned (==1), return random dx, dy within range maxDelta
    	if (scanLocCoords[2] >= 1) {
            scanLocCoords[0] = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
            scanLocCoords[1] = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
    	}

        // return random x and y
        return un.unifies(new NumberTermImpl(scanLocCoords[0]), args[6]) && 
        		un.unifies(new NumberTermImpl(scanLocCoords[1]), args[7]) && 
        		un.unifies(new NumberTermImpl(scanLocCoords[2]), args[8]);
    }
}
