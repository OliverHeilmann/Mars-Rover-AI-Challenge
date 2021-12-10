// Internal action code for project rover-AI

package mapping;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class resetCloneMap extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	
    	//Get the only object available
    	mapSingleton object = mapSingleton.getInstance();
        
    	// reset cloneMap to equal wholeMap and reset cloneIncr to 0
    	object.resetCloneMap();
    	
    	// everything ok, so returns true
        return true;
    }
}
