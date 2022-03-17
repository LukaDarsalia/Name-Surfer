import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/*
 * File: NameSurferDataBase.java
 * -----------------------------
 * This class keeps track of the complete database of names.
 * The constructor reads in the database from a file, and
 * the only public method makes it possible to look up a
 * name and get back the corresponding NameSurferEntry.
 * Names are matched independent of case, so that "Eric"
 * and "ERIC" are the same names.
 */

public class NameSurferDataBaseExt implements NameSurferConstantsExt {
	
/* Constructor: NameSurferDataBase(filename) */
/**
 * Creates a new NameSurferDataBase and initializes it using the
 * data in the specified file.  The constructor throws an error
 * exception if the requested file does not exist or if an error
 * occurs as the file is being read.
 */
	
	private Map<String, NameSurferEntryExt> data =  new HashMap<>();
	public NameSurferDataBaseExt(String filename) {
		// You fill this in //
		try {
			BufferedReader rd = new BufferedReader(new FileReader(filename));
			while(true){
				String line = rd.readLine();
				if(line==null || line.length()==0){
					break;
				}
				NameSurferEntryExt name = new NameSurferEntryExt(line);
				data.put(name.getName(), name);
			}
			rd.close();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	
/* Method: findEntry(name) */
/**
 * Returns the NameSurferEntry associated with this name, if one
 * exists.  If the name does not appear in the database, this
 * method returns null.
 */
	public NameSurferEntryExt findEntry(String name) {
		// You need to turn this stub into a real implementation //
		
		if(data.containsKey(name)==false){
			return null;
		}
		
		
		return data.get(name);
	}

}

