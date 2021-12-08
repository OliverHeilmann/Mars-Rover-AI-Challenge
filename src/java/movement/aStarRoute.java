// Internal action code for project rover-AI

package movement;

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
    	
    	int[] intList = null;
    	//object.main( intList, intList );
    	
    	// print obstacle map
    	object.showObstacleMap();
    	
        // everything ok, so returns true
        return true;
    }
}
