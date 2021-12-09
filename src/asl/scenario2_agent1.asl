/* ------------- Initial Beliefs ------------- */

init. // delete this belief when init is completed
randomwalk_max(4). // define the maximum random dX,dY number which can be generated
carrying(0). // create a carrying capacity belief
resourceType("None"). //start with belief that can carry any resource


/* ------------- Initial Goals ------------- */

! init.


/* ------------- PLANS -------------*/

/* init */
// init phase, add things here which must be run only once
+! init[source(Ag)] : Ag == self & init
		<-	.print("Initialise Stage")

			// clear movement log & reset to zero movement
			rover.ia.clear_movement_log;
			
			// get map size
			rover.ia.get_map_size(Width,Height);
			
			// setup singleton empty map of scene
			rover.ia.check_config(_,Scanrange,_);
			.my_name(Me);
			mapping.initMap(Width, Height, Scanrange, Me);
			
			// make a random move with range N
			?randomwalk_max(N);
			movement.random_walk(N, X, Y, C);
			
			// do the moves to intended location (with A*, not simple movement)
		   	// startDX, startDY, endDX, endDY, randomThresh
			!aStarMovement(X, Y, N);
			
			/*
			// log before taking move (for obstructed belief) 
		   	mapping.efficientRoute(X, Y, Xeff, Yeff);
		   	rover.ia.log_movement(Xeff, Yeff);
		   	move(Xeff, Yeff);
			*/
			
			// now can start scanning and moving
			!scan_move;.
			
// plan failure
-! init : true	<-	.print("!!!!!!!! init failed");.


/* aStarMovement */
// use the input starting and ending points to calculate the A* movement based on map information
+! aStarMovement(Xend, Yend, N)[source(Ag)] : Ag == self
		<-	.print("Moving with A* optimisation");
			
			// get starting coordinates
			rover.ia.get_distance_from_base(Xstart, Ystart);
		
			// get A* route between current position and intended, unify with AstarList
			movement.aStarRoute(Xstart, Ystart, Xend, Yend, N, AstarList);

			// now loop through list and do the A* moves!
			for ( .member(Move, AstarList) ){
				// unpack values from move
				.nth(0,Move,XaStar);
				.nth(1,Move,YaStar);
				
				// log move, then actually move
				rover.ia.log_movement(XaStar, YaStar);
				move(XaStar, YaStar)
			};.

// plan failure
-! aStarMovement : true	<-	.print("!!!!!!!! aStarMovement failed");.			


/* deposit_remaining_resources */
// if there is nothing else to do, go back to the base and deposit resources
+! deposit_remaining_resources[source(Ag)] : Ag == self
		<- .print("Depositing remaining resources at base");
		
			// return to base, log and then clear memory (efficient route already passed)
			rover.ia.get_distance_from_base(DX, DY);
			
			// do the moves to intended location (with A*, not simple movement)
		   	// startDX, startDY, endDX, endDY, randomThresh
			!aStarMovement(DX, DY, N);
			/*
			mapping.efficientRoute(DX, DY, DXeff, DYeff);
		   	rover.ia.log_movement(DXeff, DYeff);
		   	move(DXeff, DYeff);
		   	*/
			rover.ia.clear_movement_log;
		
			// Deposit xNum of type 'Type'
			?carrying(ToDeposit);
			for ( .range(J, 1, ToDeposit) ) {
				deposit(Type);
				
				// add to java singleton memory
				memory.logDeposit(Type, DepotCount);
				
				-+carrying(ToDeposit - J);
				.print("Carrying ", Carrying - I, " ", Type, " || ", Type, " Deposit Count: ", DepotCount);
			}
			
			// now joint back to main loop
			!scan_move;.

// plan failure
-! deposit_remaining_resources : true	<-	.print("!!!!!!!! init failed");.


/* scan_move */
// perform scan then move plan
+! scan_move[source(Ag)] : Ag == self
		<-	.print("Agent making a move...");
		
			// remember where the scan was made
			rover.ia.get_distance_from_base(Xrem, Yrem);
			-+whereScanWas(Xrem, Yrem);

			// scan with pre-defined range
			rover.ia.check_config(_,Scanrange,_);
			
			// update map with scan area
			mapping.updateScanArea(Scanrange, Xrem, Yrem);
			
			// now actaully scan
		   	scan(Scanrange);
		   	
		   	// print map now (only main agent prints map to console)
		   	.my_name(Me);
		   	mapping.printMap(Me);
		   	
			// go to the best scan location (will return collect resources coords under some conditions)
			?carrying(Num);
			?randomwalk_max(N);
			?resourceType(Type);
			movement.newScanLoc(Xrem, Yrem, Scanrange, Type, Num, N, X, Y);
			
			.print("--> ASL || ",X, ", ", Y);
			
			// if dir = -999 then this means agent should RTB and deposit resources	   	
		   	if (X == -999 & Y == -999){
		   		.print("Nothing left to do, will RTB to deposit");
		   		
		   		// drop any remaining desires if there were some
		   		.drop_all_desires;
		   		
		   		// we are depositing resources to base (new plan)
		   		!deposit_remaining_resources;
		   	}
		   	
		   	// do the moves to intended location (with A* movement)
			!aStarMovement(X, Y, N); // endDX, endDY, randomThresh
			
			// log before taking move (for obstructed belief) 
		   	//mapping.efficientRoute(X, Y, Xeff, Yeff);
		   	//rover.ia.log_movement(Xeff, Yeff);
		   	//move(Xeff, Yeff);

			// loop back to start
		   	!scan_move;.
		   	
// plan failure
-! scan_move : true <-	.print("!!!!!!!! scan_move failed");.


/* collect_resource */
// collect resource then return to base
//@collect_resource[atomic]
+! collect_resource(Type, Num, X, Y)[source(Ag)] : Ag == self
		<-	.print("Collecting ", Type);
		
			// move to location (if at resource and can carry more, move to it directly)
			?whereScanWas(Xscan, Yscan);
			rover.ia.get_distance_from_base(Xbase, Ybase);

			// efficient route optimisation (continue collecting if capacity > 0)
			mapping.efficientRoute(Xbase + X -Xscan, Ybase + Y -Yscan, Xeff, Yeff);
		   	rover.ia.log_movement(Xeff, Yeff);
		   	move(Xeff, Yeff);
			
			// Collect xNum of type 'Type'
			rover.ia.check_config(MaxCapacity,_,_);
			?carrying(Carrying);
			for ( .range(I, 1, Num) ) {
				if (I <= (MaxCapacity-Carrying)){
					collect(Type);
					-+carrying(Carrying + I);
					.print("Carrying ", Carrying + I, " ", Type);
					
					// set tile to empty if collected all the resources on it
					if (Carrying + I >= Num){
						.print("All resources collected on this tile, setting to empty...")
						rover.ia.get_distance_from_base(Xcurr, Ycurr);
						mapping.setTileEmpty(Xcurr, Ycurr);
					}
				}
			}
			// must set rover resource type to collected type
			-+resourceType(Type);
			
			// go back to base if capacity is full, else continue exploring
			?carrying(Amount);
			if (Amount >= MaxCapacity){
				//return to base
				!deposit_resource(Type, Num);
			}.
			
// plan failure
-! collect_resource(Type, Num, X, Y) : true
		<-	.print("!!!!!!!! collect_resource failed");
			//-resource_found(Type, Num, DX, DY);
			
			// make a random move to get out of the way of failure (range = +-2)
			//movement.random_walk(2, X, Y, C);
			.		


/* shuttle_resource */
// now that we know multiple resources are on tile, shuttle them back to base
+! shuttle_resource(Type, Num)[source(Ag)] : Ag == self
		<- .print("Shuttling remaining ", Type);
		
			// Collect xNum of type 'Type'
			rover.ia.check_config(MaxCapacity,_,_);
			?carrying(Carrying);
			for ( .range(I, 1, Num) ) {
				if (I <= (MaxCapacity-Carrying)){
					collect(Type);
					-+carrying(Carrying + I);
					.print("Carrying ", Carrying + I, " ", Type);
					
					// set tile to empty if collected all the resources on it
					if (Carrying + I >= Num){
						.print("All resources collected on this tile, setting to empty...")
						rover.ia.get_distance_from_base(Xcurr, Ycurr);
						mapping.setTileEmpty(Xcurr, Ycurr);
					}
				}
			}
			// must set rover resource type to collected type
			-+resourceType(Type);
			
			// return to base, log and then clear memory (efficient route already passed)
			rover.ia.get_distance_from_base(DX, DY);
			mapping.efficientRoute(DX, DY, DXeff, DYeff);
		   	rover.ia.log_movement(DXeff, DYeff);
		   	move(DXeff, DYeff);
			rover.ia.clear_movement_log;
			
			// Deposit xNum of type 'Type'
			?carrying(ToDeposit);
			for ( .range(J, 1, ToDeposit) ) {
				deposit(Type);
				
				// add to java singleton memory
				memory.logDeposit(Type, DepotCount);
				
				-+carrying(ToDeposit - J);
				.print("Carrying ", Carrying - I, " ", Type, " || ", Type, " Deposit Count: ", DepotCount);
			}
			
			// now move back to where initial scan was
			Remaining = Num - ToDeposit;
			.print("--------------> Shuttle Remaining: ", Remaining);
			if (Remaining > 0 ){
				.print("---> Collecting the rest of the resources");
				rover.ia.log_movement(-DXeff, -DYeff);
				move(-DXeff, -DYeff);
				
				// run shuttle plan
				!shuttle_resource(Type, Remaining);
			}
			else {
				?whereScanWas(X, Y);
				mapping.efficientRoute(X, Y, Xeff, Yeff);
				rover.ia.log_movement(-Xeff, -Yeff);
				move(-Xeff, -Yeff);
				.print("---> Now back at scan position");
			}.

// plan failure
-! shuttle_resource(Type, Num, X, Y) : true
		<- .print("!!!!!!!! shuttle_resource failed");.
		

/* deposit_resource */
+! deposit_resource(Type, Num)[source(Ag)] : Ag == self
		<-	.print("Depositing ", Type);
				
			// do moves, log and then clear memory
			rover.ia.get_distance_from_base(DX, DY);
			mapping.efficientRoute(DX, DY, DXeff, DYeff);
		   	rover.ia.log_movement(DXeff, DYeff);
		   	move(DXeff, DYeff);
			rover.ia.clear_movement_log;
			
			// How many resources do I have?
			?carrying(Carrying);
			
			// Are there more resources back at loc?
			Remaining = Num - Carrying;
			
			// Deposit xNum of type 'Type'
			for ( .range(I, 1, Carrying) ) {
				deposit(Type);
								
				// add to java singleton memory
				memory.logDeposit(Type, DepotCount);
				
				-+carrying(Carrying - I);
				.print("Carrying ", Carrying - I, " ", Type, " || ", Type, " Deposit Count: ", DepotCount);
			}
			
			// now move back to where initial scan was
			if (Remaining > 0 ){
				.print("---> Collecting the rest of the resources");
				rover.ia.log_movement(-DXeff, -DYeff);
				move(-DXeff, -DYeff);
				
				// run shuttle plan
				!shuttle_resource(Type, Remaining);
			}
			else {
				?whereScanWas(X, Y);
				mapping.efficientRoute(X, Y, Xeff, Yeff);
				rover.ia.log_movement(-Xeff, -Yeff);
				move(-Xeff, -Yeff);
				.print("---> Now back at scan position");
			}.

// plan failure	
-! deposit_resource(Type, Num) : true
		<-	.print("!!!!!!!! deposit_resource failed");
			//-resource_found(Type, Num, DX, DY);
			
			// make a random move to get out of the way of failure (range = +-2)
			//movement.random_walk(2, X, Y, C);
			.


/* ------------- Triggered Beliefs ------------- */

/* resource_not_found */
 // move around if nothing found  	  
 + resource_not_found[source(Ag)] : Ag == percept
 		<-	.print("Nothing found during scan");.
 		

/* resource_found */
// find resources and remember where they are
@resource_found[atomic]
+ resource_found(Type, Num, DX, DY)[source(Ag)] : Ag == percept
		<-	.print(Type, " found");
		
			// map resource to shared singleton map with scan coordinates
			?whereScanWas(DXme, DYme);
			mapping.updateMap(Type, DXme, DYme, DX, DY, Num);
		
			// is it a resource?
			if (Type == "Gold" | Type == "Diamond"){
				// can I carry the resource?
				?resourceType(MyType);
				if (MyType == "None" | MyType == Type){
					// passed checks, collect the resource
					!collect_resource(Type, Num, DX, DY);
				}
			}.


/* invalid_action */
// if action failed, print why, then stop eveything
+ invalid_action(action, reason)[source(Ag)] : Ag == percept
		<- .print(action, " failed. Reason: ", reason);
			.drop_all_desires;.


/* obstructed  */
// if obstruction, then log the negative of remaining travel distance
+ obstructed(X_travelled, Y_travelled, X_left, Y_left)[source(Ag)] : Ag == percept
		<-	.print("Obstruction, updating logger");
		
			// get the planned movement
			mapping.efficientRoute(-X_left, -Y_left, Xeff, Yeff);
			rover.ia.log_movement(Xeff, Yeff);
			
			// still want to get to same location, try move there again
			// !!! this should be replaced with A* searcher to find new route !!!
			//mapping.efficientRoute(X_left, Y_left, DX_left_eff, DY_left_eff);
		   	//rover.ia.log_movement(DX_left_eff, DY_left_eff);
		   	
		   	.drop_all_desires;
		   	
		   	!scan_move;
		   	.




/* NOTES BELOW:
 * 1) deal with obstructions stopping agents mapping their chosen routes
 * 2) when map is completely scanned, agents should go back to base and deposit resources
 * 3) When move randomly is passed, newScanLoc should check map and send agent to a location
 *  	with resources in it rather than pass random movement.
 * 4) Agents failing on their collection plan is messing up the mapping, they lose their location.
 * 		I think we need to move collect_resource out of the atomic resource_found. perhaps
 * 		can .drop_all_desires and then trigger the obstructed? Check with deleting the belief!
 * 5) Keep track of number of resources stored (DONE)
 * 6) Think about the scan to distance travelled weighting.. is it better to cover more ground?
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */