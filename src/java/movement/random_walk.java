// Internal action code for project rover-AI

package movement;

import java.util.concurrent.ThreadLocalRandom;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class random_walk extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	// execute the internal action
    	
    	// get limits of maxDelta --> will be used to set limit of dX and dY
    	int maxDelta = (int)((NumberTerm) args[0]).solve();

    	// get random number
        int randomX = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
        int randomY = ThreadLocalRandom.current().nextInt(-maxDelta, maxDelta);
        int randomCount = ThreadLocalRandom.current().nextInt(1, maxDelta);
        
        //randomX = 0;
        //randomY = -1;

        // return random x and y
        return un.unifies(new NumberTermImpl(randomX), args[1]) && un.unifies(new NumberTermImpl(randomY), args[2]) && un.unifies(new NumberTermImpl(randomCount), args[3]);
    }
}
