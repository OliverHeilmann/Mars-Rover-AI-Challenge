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
        	//object.showMap();
        	
            /*
            
            int scan_range = 1; 
            
            int[][] wholeMap;
            // Update wholeMap to be the size of the game
            wholeMap = new int[2*scan_range +1][2*scan_range +1];

            // Make all values of wholeMap = 0
            for (int i = 0; i < wholeMap.length; i++) {
    	        for (int j = 0; j < wholeMap[i].length; j++) {
    	        	wholeMap[i][j] = 0;
    	        }
            } 

    		int i,j,r;
    		int shift = 0;
    		r = scan_range+1;
    		for(i=0; i<=r; i++) //row to make
    		{
    			shift = 0;
    			for(j=1;j<=r-i;j++) { 
    				shift += 1;
    			}
    			for(j=1;j<=2*i-1;j++) {
    				wholeMap[i-1][j-1+shift] = 1;
    			}
    		}
    		for(i=r-1;i>=1;i--)
    		{
    			shift = 0;
    			for(j=1;j<=r-i;j++) {
    				shift += 1;
    			}
    			for(j=1;j<=2*i-1;j++) {
    				wholeMap[2*scan_range+1 - i][j-1+shift] = 1;
    			}
    		}

            // PRINT OUT MAP
    		String matVal;
            for (int ii = 0; ii < wholeMap.length; ii++) {
    	        for (int jj = 0; jj < wholeMap[ii].length; jj++) {
    	        	matVal = Integer.toString(wholeMap[ii][jj]) + ", ";
    	        	System.out.print(matVal);
    	        }
    	        System.out.println("");
            } 
            */
        }

        // everything ok, so returns true
        return true;
    }
}
