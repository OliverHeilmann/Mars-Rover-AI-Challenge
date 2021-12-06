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
			.my_name(Me);
			mapping.initMap(Width, Height, Me);

			// now can start scanning and moving
			!scan_move;.
			
// plan failure
-! init : true	<-	.print("!!!!!!!! init failed");.


/* scan_move */
// perform scan then move plan
+! scan_move[source(Ag)] : Ag == self
		<-	.print("Making random move...");
		
			// remember where the scan was made
			rover.ia.get_distance_from_base(Xrem, Yrem);
			-+whereScanWas(Xrem, Yrem);
			
			// scan with pre-defined range
			rover.ia.check_config(_,Scanrange,_);
		   	scan(Scanrange);

		   	?randomwalk_max(N);
			movement.random_walk(N, X, Y, C);

		   	// don't log until it is completed (see action_completed)
		   	mapping.efficientRoute(X, Y, Xeff, Yeff);
		   	rover.ia.log_movement(Xeff, Yeff);
		   	move(Xeff, Yeff);
		   	
		   	// only main agent prints map to console
		   	.my_name(Me);
		   	mapping.printMap(Me);

		   	!scan_move.
		   	
// plan failure
-! scan_move : true <-	.print("!!!!!!!! scan_move failed");.


/* collect_resource */
// collect resource then return to base
+! collect_resource(Type, Num, X, Y)[source(Ag)] : Ag == self
		<- .print("Collecting ", Type);
		
			// move to location (if at resource and can carry more, move to it directly)
			?whereScanWas(Xscan, Yscan);
			rover.ia.get_distance_from_base(Xbase, Ybase);

			// efficient route optimisation
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
				}
			}
			// must set rover resource type to collected type
			-+resourceType(Type);
			
			// go back to base if capacity is full, else continue exploring
			?carrying(Amount);
			if (Amount >= MaxCapacity){
				//return to base
				!deposit_resource(Type, Num);
				//.drop_desire(scan_move);
			}.
			
// plan failure
-! collect_resource(_,_,_) : true <- .print("!!!!!!!! collect_resource failed");.			


/* deposit_resource */
+! deposit_resource(Type, Num)[source(Ag)] : Ag == self
		<-	.print("Depositing ", Type);
		
			// do moves, log and then clear memory
			rover.ia.get_distance_from_base(DX, DY);
			mapping.efficientRoute(DX, DY, DXeff, DYeff);
		   	rover.ia.log_movement(DXeff, DYeff);
		   	move(DXeff, DYeff);
			rover.ia.clear_movement_log;
			
			// Deposit xNum of type 'Type'
			?carrying(Carrying);
			for ( .range(I, 1, Carrying) ) {
				deposit(Type);
				-+carrying(Carrying - I);
				.print("Carrying ", Carrying - I, " ", Type);
			}
			
			// now move back to where initial scan was
			?whereScanWas(X, Y);
			mapping.efficientRoute(X, Y, Xeff, Yeff);
			rover.ia.log_movement(-Xeff, -Yeff);
			move(-Xeff, -Yeff);
			.print("---> Now back at scan position");.

// plan failure	
-! deposit_resource : true <- .print("!!!!!!!! deposit_resource failed");.


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
		
			// map resource to shared singleton map
			rover.ia.get_distance_from_base(DXme, DYme);
			mapping.updateMap(Type, DXme, DYme, DX, DY, Num);
		
			// is it a resource?
			if (Type == "Gold" | Type == "Diamond"){
				// can I carry the resource?
				?resourceType(MyType);
				if (MyType == "None" | MyType == Type){
					// passed checks, collect resource
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




/* NOTES BELOW
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
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */