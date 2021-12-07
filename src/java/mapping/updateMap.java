// Internal action code for project rover-AI

package mapping;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class updateMap extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	//Get the only object available
    	mapSingleton object = mapSingleton.getInstance();
        
        // get limits of maxDelta --> will be used to set limit of dX and dY
        String asset = args[0].toString().replace("\"", "");
    	int dx_me = (int)((NumberTerm) args[1]).solve();
    	int dy_me = (int)((NumberTerm) args[2]).solve();
    	int dx_asset = (int)((NumberTerm) args[3]).solve();
    	int dy_asset = (int)((NumberTerm) args[4]).solve();
    	int quantity = (int)((NumberTerm) args[5]).solve();
    	
    	
    	// Create an array for passing to the SingleObject class function
    	Integer[] me_to_base = new Integer[2];
    	Integer[] me_to_asset = new Integer[2];
    	
    	me_to_base[0] = dx_me; 
    	me_to_base[1] = dy_me;
    	
    	me_to_asset[0] = dx_asset;
    	me_to_asset[1] = dy_asset;

    	// call updateMap function and update map with passed values
        object.updateMap(asset, me_to_base, me_to_asset, quantity);

        // everything ok, so returns true
        return true;
    }
}
