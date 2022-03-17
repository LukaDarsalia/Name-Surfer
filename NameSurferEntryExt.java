/*
 * File: NameSurferEntry.java
 * --------------------------
 * This class represents a single entry in the database.  Each
 * NameSurferEntry contains a name and a list giving the popularity
 * of that name for each decade stretching back to 1900.
 */


import java.util.*;

public class NameSurferEntryExt implements NameSurferConstantsExt {

/* Constructor: NameSurferEntry(line) */
/**
 * Creates a new NameSurferEntry from a data line as it appears
 * in the data file.  Each line begins with the name, which is
 * followed by integers giving the rank of that name for each
 * decade.
 */
	private String name = "";
	private int[] ranking = new int [NDECADES];
	public NameSurferEntryExt(String line) {
		// You fill this in //
	
		
		String[] arr = line.split(" ");
		name = arr[0].toLowerCase();
		
		for(int i = 1; i<arr.length; i++){
			ranking[i-1] = Integer.parseInt(arr[i]);
		}
	}

/* Method: getName() */
/**
 * Returns the name associated with this entry.
 */
	public String getName() {
		// You need to turn this stub into a real implementation //
		return name;
	}

/* Method: getRank(decade) */
/**
 * Returns the rank associated with an entry for a particular
 * decade.  The decade value is an integer indicating how many
 * decades have passed since the first year in the database,
 * which is given by the constant START_DECADE.  If a name does
 * not appear in a decade, the rank value is 0.
 */
	public int getRank(int decade) {
		
		// You need to turn this stub into a real implementation //
		try {
			return ranking[decade];
		} catch (Exception e) {
			return 0;
		}
		
	}

/* Method: toString() */
/**
 * Returns a string that makes it easy to see the value of a
 * NameSurferEntry.
 */
	public String toString() {
		// You need to turn this stub into a real implementation //
		return name+ " " + Arrays.toString(ranking);
	}
}

