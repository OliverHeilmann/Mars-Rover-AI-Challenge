package mapping;

import java.util.HashMap;
import java.util.Map;

import mapping.mapSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
   
   
   // Initialise the empty 2d array to append data to later
   public int[][] wholeMap;
   private int mapWidth;
   private int mapHeight;
   private int[][] obstacleMap; // for A* pathing
   public void init(int width, int height) {
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
   }

   
   // Use scan range to populate scanned tiles, see if/ else gates to see exceptions
   private int[][][] myMat;
   public void setNeighbourCoords(int scan_range, Integer [] coords) {
	   // setup variables for appending to matrix
	   int matSize = 2*scan_range+1;
	   int centre = (int) Math.floor(matSize/2);
	   myMat = new int[matSize][matSize][2];
		
	   // matrix specific vars
	   int shift = 0;
	   int r = scan_range+1;
	   
	   // top half of matrix
	   for (int i=0; i<=r; i++) {
		   shift = 0;
		   for(int j=1; j<=r-i; j++) { 
			   shift += 1;
		   }
		   for (int j=1; j<=2*i-1; j++) {
			   // add this to myMat matrix, which can be printed with showNeighbourMatrix()
			   myMat[i-1][j-1+shift][0] = coords[0] - (j-1+shift-centre);
			   myMat[i-1][j-1+shift][1] = coords[1] - (i-1-centre);
			   
			   // adjust for matrix dims
			   int asset_to_baseX = matrixAdjust(coords[0] - (j-1+shift-centre));
			   int asset_to_baseY = matrixAdjust(coords[1] - (i-1-centre));

			   // if current tile is (0,0) i.e. the base, set it as the base
			   if (asset_to_baseY == asset_to_baseX & asset_to_baseY == 0) {
				   wholeMap[asset_to_baseY][asset_to_baseX] = resourceDict_s2i.get("Base");
			   }
			   // if current tile is NOT an obstacle, ok to set to empty (not base, not obstacle)
			   else if (wholeMap[asset_to_baseY][asset_to_baseX] != resourceDict_s2i.get("Obstacle")) {
				   wholeMap[asset_to_baseY][asset_to_baseX] = resourceDict_s2i.get("Empty");
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
			   myMat[matSize - i][j-1+shift][0] = coords[0] - (j-1+shift-centre);
			   myMat[matSize - i][j-1+shift][1] = coords[1] + (i-1-centre);
			   
			   // adjust for matrix dims
			   int asset_to_baseX = matrixAdjust(coords[0] - (j-1+shift-centre));
			   int asset_to_baseY = matrixAdjust(coords[1] + (i-1-centre)); // NOTICE THIS IS +VE!!!
			   
			   // if current tile is (0,0) i.e. the base, set it as the base
			   if (asset_to_baseY == asset_to_baseX & asset_to_baseY == 0) {
				   wholeMap[asset_to_baseY][asset_to_baseX] = resourceDict_s2i.get("Base");
			   }
			   // if current tile is NOT an obstacle, ok to set to empty (not base, not obstacle)
			   else if (wholeMap[asset_to_baseY][asset_to_baseX] != resourceDict_s2i.get("Obstacle")) {
				   wholeMap[asset_to_baseY][asset_to_baseX] = resourceDict_s2i.get("Empty");
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
   
   
   // Print the current map belief in console if requested
   public void showMap() {
	   String matVal;
	   
	   // Make all values of wholeMap = 0
	   for (int i = 0; i < wholeMap.length; i++) {
           for (int j = 0; j < wholeMap[i].length; j++) {
        	   
        	   matVal = Integer.toString(wholeMap[i][j]) + ", ";
        	   
        	   System.out.print(matVal);
           }    
           System.out.println("");
       }
	   System.out.println("");
   }
   
   
   // look at values from current map and select a location with maximum scan effectiveness
   public int[] scanNewArea(Integer[] myD_base, int scanRange){
	   // create placeholder for 1D array containing (dx, dy) from current agent position
	   int[] theScanLocation = new int[3];
	   theScanLocation[2] = 0; // set this to 1 when the whole map has been scanned
	   
	   Integer me_to_tileX, me_to_tileY;
	   double total_score = 0.;
	   
	   // setup variables for appending to matrix
	   int matSize = 2*scanRange+1;
	   int centre = (int) Math.floor(matSize/2);
	   myMat = new int[matSize][matSize][2];
	   int r = scanRange+1;
	   int shift;

	   // loop through current map and find best distance score
	   for (int i = 0; i < wholeMap.length; i++) {
           for (int j = 0; j < wholeMap[i].length; j++) {
        	   
        	   // get optimised route distance TO TILE (from agent)
        	   me_to_tileX = matrixAdjust(myD_base[0] - matrixAdjust(j));
        	   me_to_tileX = mapAdjust(me_to_tileX);
        	   
        	   me_to_tileY = matrixAdjust(myD_base[1] - matrixAdjust(i));
        	   me_to_tileY = mapAdjust(me_to_tileY);
        	   
        	   // calculate distance score (X + Y, where X&Y are +ve and larger is worse)
        	   int distance_score = Math.abs(me_to_tileX) + Math.abs(me_to_tileY);
        	   
        	   // Now count up how many unscanned tiles there are for this location
        	   int scan_score = 0;

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
        			   if (wholeMap[tile_to_baseY][tile_to_baseX] == 0) {
        				   scan_score += 1;
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
        			   if (wholeMap[tile_to_baseY][tile_to_baseX] == 0) {
        				   scan_score += 1;
        			   }
        			   
        		   }
        	   }
        	   
        	   // now check if scan_score/distance_score is bigger than the previously stored total_score
        	   // remember, we are only interested in finding the best place to scan...
        	   // now we also store the distance the agent must travel to get there...
        	   // note, we +1 to distance_score to avoid / by 0 error
        	   if ((scan_score / (distance_score+1.0)) > total_score) {
        		   
        		   // calculate new total score
        		   total_score = scan_score / (distance_score+1.0);
        		   
        		   // update the movement coordinates to give the agent
        		   theScanLocation[0] = mapAdjust(j - matrixAdjust(myD_base[0]));
        		   theScanLocation[1] = mapAdjust(i - matrixAdjust(myD_base[1]));
        	   }
           }
       }
	   
	   // final checks to see if the map has been completely scanned...
	   // if yes, then set 0 to 1 to show ASL code that process is done
	   if ( total_score <= 0) {
		   theScanLocation[2] = 1;
	   }
	   
	   // return results to internal action function (newScanLoc())
	   return theScanLocation;
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
   
   // update obstacle map with new obstacles before plotting route to end point
   private void updateObstacleMap() {
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
   
   // Print the current map belief in console if requested
   public void showObstacleMap() {
	   
	   // update before showing
	   updateObstacleMap();
	   
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
   private static boolean IsWalkable(int[][] map, Point point) {
       if (point.y < 0 || point.y > map.length - 1) return false;
       if (point.x < 0 || point.x > map[0].length - 1) return false;
       return map[point.y][point.x] == 0;
   }

   
   // look at neighbouring coordinates
   private static List<Point> FindNeighbors(int[][] map, Point point) {
       List<Point> neighbors = new ArrayList<>();
       Point up = point.offset(0,  1);
       Point down = point.offset(0,  -1);
       Point left = point.offset(-1, 0);
       Point right = point.offset(1, 0);
       if (IsWalkable(map, up)) neighbors.add(up);
       if (IsWalkable(map, down)) neighbors.add(down);
       if (IsWalkable(map, left)) neighbors.add(left);
       if (IsWalkable(map, right)) neighbors.add(right);
       return neighbors;
   }

   
   // search for path (loop)
   public static List<Point> FindPath(int[][] map, Point start, Point end) {
       boolean finished = false;
       List<Point> used = new ArrayList<>();
       used.add(start);
       while (!finished) {
           List<Point> newOpen = new ArrayList<>();
           for(int i = 0; i < used.size(); ++i){
               Point point = used.get(i);
               for (Point neighbor : FindNeighbors(map, point)) {
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

   
   // main function to run a route through the maze
   public void main(int[] startPoint, int[] endPoint) {
	   int[][] map = {
					{0, 0, 0, 0, 0},
					{0, 5, 1, 0, 1},
					{1, 0, 0, 1, 1},
					{0, 0, 0, 1, 0},
					{1, 1, 0, 0, 1}
					};

        Point start = new Point(0, 0, null);
        Point end = new Point(3, 4, null);
        
        List<Point> path = FindPath(map, start, end);
        if (path != null) {
            for (Point point : path) {
                System.out.println(point);
            }
        }
        else {
            System.out.println("No path found");
        }
    }
}







/*	NOTES:
 *  1) Set tile to empty when resources have been all collected [DONE]
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
