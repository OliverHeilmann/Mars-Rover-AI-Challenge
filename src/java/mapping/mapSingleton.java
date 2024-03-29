package mapping;

import java.util.HashMap;
import java.util.Map;

import mapping.mapSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class mapSingleton {

	//create an object of SingleObject
   private static mapSingleton instance = new mapSingleton();
   
   //Get the only object available
   public static mapSingleton getInstance() {
	   return instance;
   }

   // -------------------------------------------------------------------------------//
   // ----------------------- SCENE MAPPING SECTION BELOW ---------------------------//
   // -------------------------------------------------------------------------------//
   
   // Private Constructor. Create a key for referencing to
   private Map<String, Integer> resourceDict_s2i;
   private Map<Integer, String> resourceDict_i2s;
   private mapSingleton() {
	   // Create a hashmap to get the numerical equivalent to append in matrix
	   // FOR STRING TO INTEGER QUIERIES
	   resourceDict_s2i = new HashMap<String, Integer>();
	   resourceDict_s2i.put("Unknown", 0);
	   resourceDict_s2i.put("Empty", 1);
	   resourceDict_s2i.put("Obstacle", 2);
	   resourceDict_s2i.put("Gold", 3);
	   resourceDict_s2i.put("Diamond", 4);
	   resourceDict_s2i.put("Base", 5);
	   resourceDict_s2i.put("Me", 6);
	   resourceDict_s2i.put("Ally", 7);
	   resourceDict_s2i.put("Enemy", 8);
	   
	   // Create a hashmap to get the numerical equivalent to append in matrix
	   // FOR INTEGER TO STRING QUIERIES
	   resourceDict_i2s = new HashMap<Integer, String>();
	   resourceDict_i2s.put(0,"Unknown");
	   resourceDict_i2s.put(1, "Empty");
	   resourceDict_i2s.put(2, "Obstacle");
	   resourceDict_i2s.put(3, "Gold");
	   resourceDict_i2s.put(4, "Diamond");
	   resourceDict_i2s.put(5, "Base");
	   resourceDict_i2s.put(6, "Me");
	   resourceDict_i2s.put(7, "Ally");
	   resourceDict_i2s.put(8, "Enemy"); 
   }
   
   
   // use this Map to add and get current position 
   private static Map<String, Integer[]> agentMapPosition = new HashMap<String, Integer[]>();
   public void createMyLocLogger(String myName) {
	   
	   // set agent current position to (0,0) i.e. start of base
	   Integer [] positionRel = new Integer[2];
	   positionRel[0] = 0;
	   positionRel[1] = 0;
	   
	   // put the location of base
	   agentMapPosition.put(myName, positionRel);
   }
   
   
   // update map value for agent in question
   public void updateMyLocLogger( String myName, Integer[] currPosRel) {
	   
	   // replace current position for my agent in map
	   currPosRel[0] = matrixAdjust( currPosRel[0] );
	   currPosRel[1] =  matrixAdjust( currPosRel[1] );
	   
	   // update coordinates
	   agentMapPosition.replace(myName, currPosRel);
   }
   
   
   // make a function to call for an internal action
   public static Set<String> getEnemies( Set<String> allAgents){
	   

	   for (String key : agentMapPosition.keySet() ) {
		   if ( !Arrays.asList(allAgents).contains(key) ) {
		   
		   }

	   }
	   
	   return allAgents;
   }
   
   
   // create a dictionary for adding resource count to
   private Map<String, Integer> resource_log = new HashMap<String, Integer>();
   public int resourceDepositLog(String Type) {
	   
	   // get count of key type passed
	   Integer count = resource_log.get(Type);
	   
       // add to dict if not already in
       if (count == null) {
    	   resource_log.put(Type, 1);
       }
       // else increment the found value by 1
       else {
    	   resource_log.put(Type, count + 1);
       }
	   
       // now return the updated value by getting value from log again (to avoid null error)
	   return resource_log.get(Type);
   }
   
   
   // make a deepcopy of a 2D array used for clone
   public static int[][] makeDeepCopy(int[][] source) {
       int[][] destination = new int[source.length][];
       for (int i = 0; i < source.length; ++i) {
            destination[i] = new int[source[i].length];
            System.arraycopy(source[i], 0, destination[i], 0, destination[i].length);
       }
       return destination;      
   }
   
   
   // Initialise the empty 2d array to append data to later
   private int[][] wholeMap;
   public int mapWidth;
   private int mapHeight;
   private int[][] obstacleMap; // for A* pathing
   private int[][] cloneMap;
   private int numberOfAgents;
   private int cloneIncr = 0;
   public boolean triggerInit = false;
   private int cloneThresh = 2; // change this to edit the refresh rate of the cloneMap (higher number == further from ground truth)
   public void init(int width, int height, int countOfAgents) {
	   triggerInit = true;
	   
	   // Update wholeMap to be the size of the game
	   wholeMap = new int[width][height];
	   
	   // make obstacle map for A*
	   obstacleMap = new int[width][height];
	   
	   // Store map dimensions internally
	   mapWidth = width;
	   mapHeight = height;
	   
	   // Make all values of wholeMap = 0
	   for (int i = 0; i < wholeMap.length; i++) {
		   
           for (int j = 0; j < wholeMap[i].length; j++) {
        	   // for main belief map
        	   if (i == 0 & j == 0) {
        		   wholeMap[i][j] = resourceDict_s2i.get("Base");
        	   }
        	   else { 
        		   wholeMap[i][j] = 0;
        	   } 
        	   
        	   // for obstacle map
        	   obstacleMap[i][j] = 0;
           }
       } 
	   // make a clone map for adding information scan areas to (used for multiple agents search optimisation)
	   cloneMap = makeDeepCopy(wholeMap);
	   numberOfAgents = countOfAgents; // for dividing up the map into sections for searching
   }

   
   // Use scan range to populate scanned tiles, see if/ else gates to see exceptions
   private int[][][] myMat;
   public void setNeighbourCoords(int scan_range, Integer [] coords, int mapType, Integer... args2) {
	   // setup variables for appending to matrix
	   int matSize = 2*scan_range+1;
	   int centre = (int) Math.floor(matSize/2);
	   //myMat = new int[matSize][matSize][2];
		
	   // matrix specific vars
	   int shift = 0;
	   int r = scan_range+1;
	   
	   // allow user to choose which map to append scan coverage area to
	   int[][][] chooseMap = new int[2][][];
	   chooseMap[0] = wholeMap; 
	   chooseMap[1] = cloneMap;
	   
	   // top half of matrix
	   for (int i=0; i<=r; i++) {
		   shift = 0;
		   for(int j=1; j<=r-i; j++) { 
			   shift += 1;
		   }
		   for (int j=1; j<=2*i-1; j++) {
			   // add this to myMat matrix, which can be printed with showNeighbourMatrix()
			   //myMat[i-1][j-1+shift][0] = coords[0] - (j-1+shift-centre);
			   //myMat[i-1][j-1+shift][1] = coords[1] - (i-1-centre);
			   
			   // adjust for matrix dims
			   int asset_to_baseX = matrixAdjust(coords[0] - (j-1+shift-centre));
			   int asset_to_baseY = matrixAdjust(coords[1] - (i-1-centre));

			   // if current tile is (0,0) i.e. the base, set it as the base
			   if (asset_to_baseY == asset_to_baseX & asset_to_baseY == 0) {
				   chooseMap[mapType][asset_to_baseY][asset_to_baseX] = resourceDict_s2i.get("Base");
			   }
			   // if current tile is NOT an obstacle, ok to set to empty (not base, not obstacle)
			   else if (chooseMap[mapType][asset_to_baseY][asset_to_baseX] != resourceDict_s2i.get("Obstacle")) {
				   chooseMap[mapType][asset_to_baseY][asset_to_baseX] = resourceDict_s2i.get("Empty");
			   }
		   }
	   }
	   
	   // bottom half of matrix
	   for (int i=r-1; i>=1; i--) {
		   shift = 0;
		   for(int j=1; j<=r-i; j++) { 
			   shift += 1;
		   }
		   for (int j=1; j<=2*i-1; j++) {
			   // add this to myMat matrix, which can be printed with showNeighbourMatrix()
			   //myMat[matSize - i][j-1+shift][0] = coords[0] - (j-1+shift-centre);
			   //myMat[matSize - i][j-1+shift][1] = coords[1] + (i-1-centre);
			   
			   // adjust for matrix dims
			   int asset_to_baseX = matrixAdjust(coords[0] - (j-1+shift-centre));
			   int asset_to_baseY = matrixAdjust(coords[1] + (i-1-centre)); // NOTICE THIS IS +VE!!!
			   
			   // if current tile is (0,0) i.e. the base, set it as the base
			   if (asset_to_baseY == asset_to_baseX & asset_to_baseY == 0) {
				   chooseMap[mapType][asset_to_baseY][asset_to_baseX] = resourceDict_s2i.get("Base");
			   }
			   // if current tile is NOT an obstacle, ok to set to empty (not base, not obstacle)
			   else if (chooseMap[mapType][asset_to_baseY][asset_to_baseX] != resourceDict_s2i.get("Obstacle")) {
				   chooseMap[mapType][asset_to_baseY][asset_to_baseX] = resourceDict_s2i.get("Empty");
			   }
		   }
	   }
	   return;
   }
   
   
   // use singleton myMat created above in showNeighbourCoords(_,_) to print to console
   public void showNeighbourMatrix() {
	   // print out mat
	   String matVal;
	   for (int ii = 0; ii < myMat.length; ii++) {
		   for (int jj = 0; jj < myMat[ii].length; jj++) {
			   
			   matVal = "(" + Integer.toString(myMat[ii][jj][0]) + ", " 
					   		+ Integer.toString(myMat[ii][jj][1]) + "), ";
			   	
			   System.out.print(matVal);
		   }
		   System.out.println("");
	   }
	   return;
   }
   
   
   // Function to correct for distances to base greater than the base max
   // dimensions. This is used because the map wraps! This assumes square
   // map!
   public Integer mapAdjust(Integer thing_to_base) {
	   
	   // ensure that we drop all full loops around the map using modulo
	   thing_to_base = thing_to_base % mapWidth;
	   
	   if (Math.abs(thing_to_base) >= (mapWidth * 0.5)) {
		   if (thing_to_base > 0) {
			   thing_to_base -= mapWidth;
		   }
		   else {
			   thing_to_base += mapWidth;
		   }
	   }
	   
	   // if neither condition is met, return the funct input
	   return thing_to_base;
   }
   
   
   // Function to adjust distance to base into a matrix readable format.
   // Note that maps wrap so must correct for this. This assumes square
   // map [n, n]
   private Integer matrixAdjust(Integer thing_to_adjust) {
	   // adjust thing_to_base if == -ve to acquire correct modulo value
	   // e.g. -10%8 = 6 --> what we want is =-2
	   if (thing_to_adjust < 0) {
		   thing_to_adjust = -(Math.abs(thing_to_adjust) % mapWidth);
	   } else thing_to_adjust %= mapWidth;
	   
	   // if value is -ve, we should adjust for smallest distance to base
	   // else it is +ve and we correct also, but operator is opposite (-+)
	   if (thing_to_adjust > 0) {
		   thing_to_adjust = mapWidth - thing_to_adjust;
	   }
	   else thing_to_adjust = Math.abs(thing_to_adjust);

	   // return shortest path to base
	   return thing_to_adjust;
   }
   
   
   // updateMap function called to update belief state of agents' map
   public void updateMap(String type, Integer[] d_base, Integer[] d_asset, Integer quantity){
	   int asset_to_baseX = matrixAdjust(d_base[0] - d_asset[0]);
	   int asset_to_baseY = matrixAdjust(d_base[1] - d_asset[1]);

	   // remember that we choose which row first, then column
	   // meaning that width refers to columns and height refs
	   // rows (in case the order of X and Y looks strange!)
	   // (Use type.equals(<YOURASSET>); to check if they are eq).
	   wholeMap[asset_to_baseY][asset_to_baseX] = resourceDict_s2i.get(type);

	   return;
   }
   
   // if resources have been fully collected, set tile to empty
   public void setTileEmpty(Integer[] myD_base) {
	   int tile_to_baseX = matrixAdjust(myD_base[0]);
	   int tile_to_baseY = matrixAdjust(myD_base[1]);

	   // set tile to empty
	   wholeMap[tile_to_baseY][tile_to_baseX] = resourceDict_s2i.get("Empty");
   }
   
   
   // if resources have been fully collected, set tile to obstacle
   public void setTileObstacle(Integer[] myD_base) {
	   int tile_to_baseX = matrixAdjust(myD_base[0]);
	   int tile_to_baseY = matrixAdjust(myD_base[1]);

	   // set tile to empty
	   if (tile_to_baseX != 0 & tile_to_baseY != 0 ) {
		   wholeMap[tile_to_baseY][tile_to_baseX] = resourceDict_s2i.get("Obstacle");
	   }
   }
   
   // Print the current map belief in console if requested
   public void showMap(int mapType) {
	   
	   // allow user to choose which map to append scan coverage area to
	   int[][][] chooseMap = new int[2][][];
	   chooseMap[0] = wholeMap; 
	   chooseMap[1] = cloneMap;
	   
	   String matVal;
	   
	   // Make all values of wholeMap = 0
	   for (int i = 0; i < chooseMap[mapType].length; i++) {
           for (int j = 0; j < chooseMap[mapType][i].length; j++) {
        	   
        	   matVal = Integer.toString(chooseMap[mapType][i][j]) + ", ";
        	   
        	   System.out.print(matVal);
           }    
           System.out.println("");
       }
	   System.out.println("");
   }
   
   
   // look at values from current map and select a location with maximum scan effectiveness
   public Integer[] scanNewArea(Integer[] myD_base, int scanRange){
	   // create placeholder for 1D array containing (dx, dy) from current agent position
	   Integer[] theScanLocation = new Integer[3];
	   theScanLocation[2] = 0; // set this to 1 when the whole map has been scanned
	   
	   Integer me_to_tileX, me_to_tileY;
	   double lowestEnergy = Double.POSITIVE_INFINITY;
	   
	   // setup variables for appending to matrix
	   int matSize = 2*scanRange+1;
	   int centre = (int) Math.floor(matSize/2);
	   myMat = new int[matSize][matSize][2];
	   int r = scanRange+1;
	   int shift;
	   
	   // the cloneMap should give new results to the next agent asking so long as 
	   // the incrementer is less than the number of agents. The intention here is to 
	   // give out one new DIFFERENT scan location to each agent. If statement below
	   // results the cloneMap to be in line with wholeMap if not. Otherwise, increment
	   // up until threshold has been reached.
	   if (cloneIncr >= numberOfAgents*cloneThresh) {
		   cloneMap = makeDeepCopy(wholeMap);
	   }
	   else {
		   cloneIncr += 1;
	   }

	   // loop through current map and find best distance score
	   for (int i = 0; i < cloneMap.length; i++) {
           for (int j = 0; j < cloneMap[i].length; j++) {
        	   
        	   // get optimised route distance TO TILE (from agent)
        	   me_to_tileX = matrixAdjust(myD_base[0] - matrixAdjust(j));
        	   me_to_tileX = mapAdjust(me_to_tileX);
        	   
        	   me_to_tileY = matrixAdjust(myD_base[1] - matrixAdjust(i));
        	   me_to_tileY = mapAdjust(me_to_tileY);
        	   
        	   // calculate distance score (X + Y, where X&Y are +ve and larger is worse)
        	   //int distance_score = Math.abs(me_to_tileX) + Math.abs(me_to_tileY);
        	   int distanceEnergy = Math.abs(me_to_tileX) * 6; // 6 energy to move to location
        	   
        	   // Now count up how many unscanned tiles there are for this location
        	   int tileCount = 0;

        	   // top half of matrix
        	   for (int ii=0; ii<=r; ii++) {
        		   shift = 0;
        		   for(int jj=1; jj<=r-ii; jj++) { 
        			   shift += 1;
        		   }
        		   for (int jj=1; jj<=2*ii-1; jj++) {
        			   // adjust for matrix dims by taking the agent coordinates first and then
        			   // checking the entire map for the score for each tile position.
        			   int tile_to_baseX = matrixAdjust(-j - (jj-1+shift-centre));
        			   int tile_to_baseY = matrixAdjust(-i - (ii-1-centre));
        			   
        			   // if tile data is 0 (for unscanned), then this is a good place to scan, +1 point
        			   if (cloneMap[tile_to_baseY][tile_to_baseX] == 0) {
        				   tileCount += 1; // +7.5 energy scan
        			   }
        		   }
        	   }
        	   
        	   // bottom half of matrix
        	   for (int ii=r-1; ii>=1; ii--) {
        		   shift = 0;
        		   for(int jj=1; jj<=r-ii; jj++) { 
        			   shift += 1;
        		   }
        		   for (int jj=1; jj<=2*ii-1; jj++) {
        			   // adjust for matrix dims by taking the agent coordinates first and then
        			   // checking the entire map for the score for each tile position.
        			   int tile_to_baseX = matrixAdjust(-j - (jj-1+shift-centre));
        			   int tile_to_baseY = matrixAdjust(-i + (ii-1-centre)); // NOTICE THIS IS +VE!!!
        			   
        			   // if tile data is 0 (for unscanned), then this is a good place to scan, +1 point
        			   if (cloneMap[tile_to_baseY][tile_to_baseX] == 0) {
        				   tileCount += 1; // +7.5 energy scan
        			   }
        			   
        		   }
        	   }
        	   // now check if scan_score/distance_score is bigger than the previously stored total_score
        	   // remember, we are only interested in finding the best place to scan...
        	   // now we also store the distance the agent must travel to get there...
        	   // note, we +1 to distance_score to avoid / by 0 error
        	   //Double currScore = Math.pow(scan_score,2) / (distance_score+1.0);
        	   Double energyPerTile = ((distanceEnergy + (7.5 * scanRange)) +1) / (tileCount+1.);
        	   if (energyPerTile < lowestEnergy) {
        		   
        		   // now check to see if the tile is an obstacle. We check the actual map for
        		   // information about whether there is an obstacle is there. As the cloneMap
        		   // may be out of sync with the wholeMap, there may be missing obstacles. Hence,
        		   // we check if the wholeMap has the obstacle even though we are using the cloneMap
        		   // for determining the next best scan location
        		   if (wholeMap[j][i] != resourceDict_s2i.get("Obstacle")) {
        			   // calculate new total score
        			   lowestEnergy = energyPerTile;
            		   
            		   // update the movement coordinates to give the agent
            		   theScanLocation[0] = mapAdjust(j - matrixAdjust(myD_base[0]));
            		   theScanLocation[1] = mapAdjust(i - matrixAdjust(myD_base[1]));
        		   }
        	   }
        	   //System.out.format("%.2f", energyPerTile);
        	   //System.out.print(" | ");
           }
           //System.out.println("");
       }
	   //System.out.println(lowestEnergy);
	   
	   // final checks to see if the map has been completely scanned...
	   // if yes, then set 0 to 1 to show ASL code that process is done
	   if ( lowestEnergy >= (7.5 * scanRange)+1) {
		   theScanLocation[2] = 1;
	   }

	   // return results to internal action function (newScanLoc())
	   return theScanLocation;
   }
   
   
   // Agent has crashed into an object so reset the clone map to equal the whole map!
   public void resetCloneMap() {
	   // now clone map is up to date with data
	   cloneMap = makeDeepCopy(wholeMap);
	   
	   // now start allocating clone info from 0
	   cloneIncr = 0;
   }
   
   
   // Do I have any resources to collect on the map? If yes, go to them instead of random scan...
   public int[][] myOnMapResources(String type, Integer[] myD_base){
	   // make a 2D array with length of map width*height
	   int[][] myResourceList = new int[wholeMap.length * wholeMap.length][2];
	   int stepList = 0;

	   // loop through whole map and look for agent's resources
	   for (int i = 0; i < wholeMap.length; i++) {
           for (int j = 0; j < wholeMap[i].length; j++) {
        	   // if I can carry anything, look for diamond and gold
        	   if (type.equals("None")) {
        		   // if it is gold OR diamond
        		   if (wholeMap[i][j] == resourceDict_s2i.get("Gold") || wholeMap[i][j] == resourceDict_s2i.get("Diamond")) {
            		   // add the corrected map cell location
            		   myResourceList[stepList][0] = mapAdjust(j - matrixAdjust(myD_base[0]));
            		   myResourceList[stepList][1] = mapAdjust(i - matrixAdjust(myD_base[1]));
            		   
            		   stepList += 1;
        		   }
        	   }
        	   // else if cell has agent resource type, store it
        	   else if (wholeMap[i][j] == resourceDict_s2i.get(type)) {
        		   
        		   // add the corrected map cell location
        		   myResourceList[stepList][0] = mapAdjust(j - matrixAdjust(myD_base[0]));
        		   myResourceList[stepList][1] = mapAdjust(i - matrixAdjust(myD_base[1]));
        		   
		   		   stepList += 1;
        	   }
           }    
       }

	   return myResourceList;
   }
   
   // -------------------------------------------------------------------------------//
   // ------------------ A* SEARCHING ALGORITHM SECTION BELOEW ----------------------//
   // -------------------------------------------------------------------------------//
   // Taken From: https://gamedev.stackexchange.com/questions/197165/java-simple-2d-grid-pathfinding
   // modified for world wrapping
   
   // update obstacle map with new obstacles before plotting route to end point
   private void updateObstacleMap(String myName) {
	   // first add all my agents onto the obstacle map
	   for (String key : agentMapPosition.keySet() ) {
		   Integer[] mapLoc = agentMapPosition.get(key);
		   
		   // don't add an obstacle on top of myself!
		   if (key != myName) {
			   obstacleMap[mapLoc[0]][mapLoc[1]] = resourceDict_s2i.get(("Obstacle"));
		   }
	   };

	   // loop through wholeMap and add the obstacles to the obstacle map
	   for (int i = 0; i < wholeMap.length; i++) {
           for (int j = 0; j < wholeMap[i].length; j++) {
        	   
        	   // update if there is an obstacle there
        	   if (wholeMap[i][j] == resourceDict_s2i.get(("Obstacle"))) {
        		   obstacleMap[i][j] = 1; // A* version of obstacle
        	   }
           }    
       }
   }
   
   
   // restore obstacle map to just obstacles, no agents
   private void restoreObstacleMap(String myName) {
	   // first add all my agents onto the obstacle map
	   for (String key : agentMapPosition.keySet() ) {
		   Integer[] mapLoc = agentMapPosition.get(key);
		   
		   // don't add an obstacle on top of myself!
		   if (key != myName) {
			   obstacleMap[mapLoc[0]][mapLoc[1]] = 0;
		   }
	   }
   }
   
   
   // observe the individual tiles
   private static class Point {
	   public int x;
	   public int y;
	   public Point previous;
	
	   public Point(int x, int y, Point previous) {
	       this.x = x;
	       this.y = y;
	       this.previous = previous;
	   }
	
	   @Override
	   public String toString() { return String.format("(%d, %d)", x, y); }
	
       @Override
       public boolean equals(Object o) {
           Point point = (Point) o;
           return x == point.x && y == point.y;
       }

       @Override
       public int hashCode() { return Objects.hash(x, y); }

       public Point offset(int ox, int oy) { return new Point(x + ox, y + oy, this);  }
   }
	
   
   // check if tile can be walked on (this is the map wrapping == off)
   private boolean IsWalkable(int[][] map, Point point) { 

	   // Adjust for map wrapping by making x and y positive if past map end
	   // for point.x
	   if (point.x > (mapWidth-1) ) {
		   point.x = point.x - mapWidth;
	   }
	   else if (point.x < 0) {
		   point.x = point.x + mapWidth;
	   }
	   // for point.y
	   if (point.y > (mapWidth-1) ) {
		   point.y = point.y - mapWidth;
	   }
	   else if (point.y < 0) {
		   point.y = point.y + mapWidth;
	   }

	   // original map border conditions (likely the first two lines will never fire)
       if (point.y < 0 || point.y > map.length - 1) return false;
       if (point.x < 0 || point.x > map[0].length - 1) return false;
       return map[point.y][point.x] == 0;
   }

   
   // look at neighbouring coordinates
   private List<Point> FindNeighbors(int[][] map, Point point) {
       List<Point> neighbors = new ArrayList<>();
       Point up = point.offset(0,  1);
       Point down = point.offset(0,  -1);
       Point left = point.offset(-1, 0);
       Point right = point.offset(1, 0);
       
       // now add diagonal
       Point upleft = point.offset(-1,  1);
       Point upright = point.offset(1,  1);
       Point downleft = point.offset(-1, -1);
       Point downright = point.offset(1, -1);
       
       if (IsWalkable(map, up)) neighbors.add(up);
       if (IsWalkable(map, down)) neighbors.add(down);
       if (IsWalkable(map, left)) neighbors.add(left);
       if (IsWalkable(map, right)) neighbors.add(right);
       
       // now add diagonal
       if (IsWalkable(map, upleft)) neighbors.add(upleft);
       if (IsWalkable(map, upright)) neighbors.add(upright);
       if (IsWalkable(map, downleft)) neighbors.add(downleft);
       if (IsWalkable(map, downright)) neighbors.add(downright);
       return neighbors;
   }

   
   // search for path (loop)
   private List<Point> FindPath(int[][] map, Point start, Point end) {
       boolean finished = false;
       List<Point> used = new ArrayList<>();
       used.add(start);
       while (!finished) {
           List<Point> newOpen = new ArrayList<>();
           for(int i = 0; i < used.size(); ++i){
               Point point = used.get(i);
               
               //System.out.println("-----------------");
               for (Point neighbor : FindNeighbors(map, point)) {
            	   // if the successful point is not already in lists then add
            	   if (!used.contains(neighbor) && !newOpen.contains(neighbor)) {
                       newOpen.add(neighbor);
                   }
               }
           }

           for(Point point : newOpen) {
               used.add(point);
               if (end.equals(point)) {
                   finished = true;
                   break;
               }
           }

           if (!finished && newOpen.isEmpty())
               return null;
       }

       List<Point> path = new ArrayList<>();
       Point point = used.get(used.size() - 1);
       while(point.previous != null) {
           path.add(0, point);
           point = point.previous;
       }
       return path;
   }
   
   
   // Print the current map belief in console if requested
   public void showObstacleMap() {
	   String matVal;
	   
	   // Make all values of wholeMap = 0
	   for (int i = 0; i < obstacleMap.length; i++) {
           for (int j = 0; j < obstacleMap[i].length; j++) {
        	   
        	   matVal = Integer.toString(obstacleMap[i][j]) + ", ";
        	   
        	   System.out.print(matVal);
           }    
           System.out.println("");
       }
	   System.out.println("");
   }

   
   // main function to run a route through the maze
   public List<List<Integer>> calcAStarRoute(String myName, int[] startPoint, int[] endPoint) {
	   
	   	// update the obstacle map before plotting a route (obstacleMap)
	   	updateObstacleMap(myName);
	   	
	   	// print for debugging
	   	//showObstacleMap();
	   
	   	// now we need to take distance to base and turn it into matrix equivalents
	   	int startX = matrixAdjust(startPoint[0]);
	   	int startY = matrixAdjust(startPoint[1]);
	   	int endX = matrixAdjust(endPoint[0]);
	   	int endY = matrixAdjust(endPoint[1]);
	   	
	   	// check if chosen tile to move to has an obstacle on it, if yes, pretend it 
	   	// does not. Ok to not reach it on movement and throw +obstacle belief.
	   	// replace tile with initial information at end of this function
	   	int tileState = obstacleMap[endX][endY];
	   	obstacleMap[endX][endY] = 0;
	   	
	   	/*
	   	// print out start and end points for debugging
	   	String P1 = "---> " + Integer.toString(startX) + "," + Integer.toString(startY) + 
	   				" || " + Integer.toString(endX) + "," + Integer.toString(endY);
	   	System.out.print(P1);
	   	System.out.println("");
	   	*/
	   	
	   	// reformat coordinates to points for A* algorithm
        Point start = new Point(startX, startY, null);
        Point end = new Point(endX, endY, null);
        
        // make array and arraylist for returning to aStarRoute internal action
        List<List<Integer>> coordList = new ArrayList<List<Integer>>();
        
        // store previous steps
        int prevX = 0;
        int prevY = 0;

        // now find path and print it to console
        List<Point> path = FindPath(obstacleMap, start, end);
        if (path != null) {
            for (Point point : path) {
            	
            	// need to change map coordinates for corresponding dx, dy movement actions
         	   	// get optimised route distance TO TILE (from agent)
         	   	int me_to_tileX = matrixAdjust(matrixAdjust(point.x) - startPoint[0]);
         	   	me_to_tileX = mapAdjust(me_to_tileX);
			   
         	   	int me_to_tileY = matrixAdjust(matrixAdjust(point.y) - startPoint[1]);
         	   	me_to_tileY = mapAdjust(me_to_tileY);
         	   	
         	   	/*
        	   	// print out start and end points for debugging
        	   	String P = "-----> " + Integer.toString(mapAdjust(me_to_tileX - prevX)) + "," +
        	   							Integer.toString(mapAdjust(me_to_tileY - prevY));
        	   	System.out.print(P);
        	   	System.out.println("");
         	   	*/
         	   	
         	   	// add dx, dy to coordinate list
         	   	coordList.add(Arrays.asList(mapAdjust(me_to_tileX - prevX),
         	   								mapAdjust(me_to_tileY - prevY)));
         	   	
         	   	// update previous step to equal current (for next step)
         	   	prevX = me_to_tileX;
         	   	prevY = me_to_tileY;
            }
        }
        else {
        	//System.out.println("-------------------------> No path found");
        	//coordList.add(Arrays.asList(-999,-999));
        }
        
        // reset tileState to its initial state
        obstacleMap[endX][endY] = tileState;
        
        // restore obstacle map (to exclude agent positions)
        restoreObstacleMap(myName);
        
        // return the array of coordinates the agent should travel via
        return coordList;
    }
}







/*	NOTES:
 *  1) Set tile to empty when resources have been all collected [DONE]
 * 	2) Update map next search to account for known obstacles
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
