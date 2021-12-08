// Internal action code for project rover-AI

package memory;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;
import mapping.mapSingleton;

public class logDeposit extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	
    	//Get the only object available
    	mapSingleton object = mapSingleton.getInstance();
        
    	//Get resource type in Java format
        String resourceType = args[0].toString().replace("\"", "");
    	
    	// now log information in singleton
        int resourceCount = object.resourceDepositLog(resourceType);
        
        // return the value of resources in depot
    	return un.unifies(new NumberTermImpl(resourceCount), args[1]);
    }
}
