package mapping;

import java.util.HashMap;
import java.util.Map;

import mapping.mapSingleton;

public class mapSingleton {

	//create an object of SingleObject
   private static mapSingleton instance = new mapSingleton();
   
   //Get the only object available
   public static mapSingleton getInstance() {
      return instance;
   }
   
   // Private Constructor. Create a key for referencing to
   Map<String, Integer> resourceDict_s2i;
   Map<Integer, String> resourceDict_i2s;
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
   
   
   // Initialise the empty 2d array to append data to later
   public int[][] wholeMap;
   private int mapWidth;
   private int mapHeight;
   public void init(int width, int height) {
	   // Update wholeMap to be the size of the game
	   wholeMap = new int[width][height];
	   
	   // Store map dimensions internally
	   mapWidth = width;
	   mapHeight = height;
	   
	   // Make all values of wholeMap = 0
	   for (int i = 0; i < wholeMap.length; i++) {
		   
           for (int j = 0; j < wholeMap[i].length; j++) {
        	   
        	   if (i == 0 & j == 0) {
        		   wholeMap[i][j] = resourceDict_s2i.get("Base");
        	   }
        	   else { 
        		   wholeMap[i][j] = 0;
        	   } 
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
	   
	   showMap();
	   
	   // create placeholder for 1D array containing (dx, dy) from current agent position
	   int[] theScanLocation = new int[2];
	   Integer me_to_tileX, me_to_tileY;
	   double total_score = 0.;
	   
	   // setup variables for appending to matrix
	   int matSize = 2*scanRange+1;
	   int centre = (int) Math.floor(matSize/2);
	   myMat = new int[matSize][matSize][2];
	   int r = scanRange+1;
	   int shift;
	   
	   String matVal;

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
        	   
        	   // print score matrix out
        	   System.out.format("%d", scan_score);
        	   System.out.print(", ");
        	   System.out.format("%d", (distance_score+1));
        	   System.out.print(", ");
        	   System.out.format("%.2f", scan_score / (distance_score+1.0));

        	   
        	   
        	   // now check if scan_score/distance_score is bigger than the previously stored total_score
        	   // remember, we are only interested in finding the best place to scan...
        	   // now we also store the distance the agent must travel to get there...
        	   // note, we +1 to distance_score to avoid / by 0 error
        	   if ((scan_score / (distance_score+1.0)) > total_score) {
        		   
        		   // calculate new total score
        		   total_score = scan_score / (distance_score+1.0);
        		   
        		   //System.out.format("Total: %.2f", total_score);
        		   //System.out.println("");
        		   System.out.print("!!");

        		   // update the movement coordinates to give the agent
        		   theScanLocation[0] = mapAdjust(j - matrixAdjust(myD_base[0]));
        		   theScanLocation[1] = mapAdjust(i - matrixAdjust(myD_base[1]));
        		   
        		   /*
        		   System.out.print(me_to_tileX);
        		   System.out.print(", ");
        		   System.out.println(me_to_tileY);
        		   */
        	   	}
        	   System.out.print(" | ");
        	   
           }
    	   System.out.println("");
       }
	   
	   // print out best scan location to check
	   String Out = "My D2B: " + Integer.toString(myD_base[0]) + ", " + 
			   					 Integer.toString(myD_base[1]) + " || " +
			   		"Best map coord to scan is (" + Integer.toString(theScanLocation[0]) + ", " 
		   										 + Integer.toString(theScanLocation[1]) + "), ";
	   System.out.println(Out);
	   
	   // return results to internal action function (newScanLoc())
	   return theScanLocation;
   }
}







/*	NOTES:
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
