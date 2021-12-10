// Agents want to hang out with each other... Schelling Simulation

/* Initial beliefs and rules */

type(blue).

/* Initial goals */

!scan.

/* PLANS */

	/* movement actions */
+!scan : true <- ?type(Y); .my_name(MyName);
							
							?type(MyType);
							
							//.wait(750);
							
							//.print(MyName, " is ", MyType);
				 			!scan.

				 			
/* resource_found */
// find resources and remember where they are
@resource_found[atomic]
+ resource_found(Type, Num, DX, DY) : true
		<-	.print("!!!!!!!!!!VICTIM HAS FOUND A RESOURCE!!!!!!!!!!");.
		
		
/* obstructed  */
// if obstruction, then log the negative of remaining travel distance
+ obstructed(X_travelled, Y_travelled, X_left, Y_left) : true
		<-	.print("!!!!!!!!!!VICTIM HAS BEEN OBSTRUCTED!!!!!!!!!!");.