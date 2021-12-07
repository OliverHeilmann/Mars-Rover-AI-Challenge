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
   
   // Initialise the scan coverage matrix- will be used for updating map
   public int[][] scanCoverageMatrix;
   public void scanCoverageArea(int scan_range) {
	   // Update scanCoverageMatrix to be the size of the game
	   scanCoverageMatrix = new int[2*scan_range +1][2*scan_range +1];
	   
		// Make all values of scanCoverageMatrix = 0
		for (int i = 0; i < scanCoverageMatrix.length; i++) {
			for (int j = 0; j < scanCoverageMatrix[i].length; j++) {
				scanCoverageMatrix[i][j] = 0;
			}
		}
	
		// now append 1s to the corresponding cells which the scan reaches
		int i,j,r;
		int N = 0; // shift row
		int M = 0; // shift col
		int shift = 0;
		r = scan_range+1;
		
		// top half plus middle
		for(i=0; i<=r; i++) {
			shift = 0;
			for(j=1;j<=r-i;j++) { 
				shift += 1;
			}
			for(j=1;j<=2*i-1;j++) {
				scanCoverageMatrix[i-1+N][j-1+shift+M] = 1;
			}
		}
		
		// bottom half minus middle
		for(i=r-1;i>=1;i--) {
			shift = 0;
			for(j=1;j<=r-i;j++) {
				shift += 1;
			}
			for(j=1;j<=2*i-1;j++) {
				scanCoverageMatrix[2*scan_range+1 - i+N][j-1+shift+M] = 1;
			}
		}
		return;
	}
   
   
   // Print the agent scan range in console if requested
   public void showScanRange() {
	   String matVal;
	   for (int ii = 0; ii < scanCoverageMatrix.length; ii++) {
		   for (int jj = 0; jj < scanCoverageMatrix[ii].length; jj++) {
			   matVal = Integer.toString(scanCoverageMatrix[ii][jj]) + ", ";
			   System.out.print(matVal);
		   }
		   System.out.println("");
	   }
	   return;
   }
   
   
   public void showNeighbourCoords(int scan_range, Integer [] coords) {
	   // setup variables for appending to matrix
	   int matSize = 2*scan_range+1;
	   int centre = (int) Math.floor(matSize/2);
	   int[][][] myMat = new int[matSize][matSize][2];
		
	   // matrix specific vars
	   int shift = 0;
	   int r = scan_range+1;
	   
	   // top half
	   for (int i=0; i<=r; i++) {
		   shift = 0;
		   for(int j=1; j<=r-i; j++) { 
			   shift += 1;
		   }
		   for (int j=1; j<=2*i-1; j++) {
			   //myMat[i-1][j-1+shift][0] = coords[0] - (j-1+shift-centre);
			   //myMat[i-1][j-1+shift][1] = coords[1] - (i-1-centre);
			   
			   int asset_to_baseX = matrixAdjust(coords[0] - (j-1+shift-centre));
			   int asset_to_baseY = matrixAdjust(coords[1] - (i-1-centre));
			   
			   if (wholeMap[asset_to_baseX][asset_to_baseY] != resourceDict_s2i.get("Obstacle")) {
				   wholeMap[asset_to_baseX][asset_to_baseY] = resourceDict_s2i.get("Empty");
			   }
			   
			   
		   }
	   }
	   
	   // bottom half
	   for (int i=r-1; i>=1; i--) {
		   shift = 0;
		   for(int j=1; j<=r-i; j++) { 
			   shift += 1;
		   }
		   for (int j=1; j<=2*i-1; j++) {
			   //myMat[matSize - i][j-1+shift][0] = coords[0] - (j-1+shift-centre);
			   //myMat[matSize - i][j-1+shift][1] = coords[1] + (i-1-centre);
			   
			   int asset_to_baseX = matrixAdjust(coords[0] - (j-1+shift-centre));
			   int asset_to_baseY = matrixAdjust( coords[1] + (i-1-centre));
			   
			   if (wholeMap[asset_to_baseX][asset_to_baseY] != resourceDict_s2i.get("Obstacle")) {
				   wholeMap[asset_to_baseX][asset_to_baseY] = resourceDict_s2i.get("Empty");
			   }
			   
		   }
	   }
	   /*
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
	   */
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
}
