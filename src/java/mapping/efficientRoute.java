// Internal action code for project rover-AI

package mapping;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class efficientRoute extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

    	//Get the only object available
    	mapSingleton object = mapSingleton.getInstance();
        
        // get limits of maxDelta --> will be used to set limit of dX and dY
    	int dx = (int)((NumberTerm) args[0]).solve();
    	int dy = (int)((NumberTerm) args[1]).solve();
    	
    	// call updateMap function and update map with passed values
    	int dx_eff = object.mapAdjust(dx);
    	int dy_eff = object.mapAdjust(dy);
    	
        // return optimised route as dx, dy at args 2,3
    	return un.unifies(new NumberTermImpl(dx_eff), args[2]) && un.unifies(new NumberTermImpl(dy_eff), args[3]);
    }
}
