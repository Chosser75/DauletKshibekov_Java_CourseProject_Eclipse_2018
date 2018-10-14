import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Class RunnersLoader loads a data from source and returns a list 
 * of threads with runners created according to the loaded data.
 */
public class RunnersLoader {

	/**
	 * Loads a hard coded data and returns a list of threads created according to the loaded data.
	 * 
	 * @return ArrayList<Thread>
	 */
	public ArrayList<Thread> loadDefaults() {
		ArrayList<Thread> runners = new ArrayList<>();
		Thread runner1 = new Thread(new ThreadRunner("Tortoise", 10, 0), "Tortoise");
		Thread runner2 = new Thread(new ThreadRunner("Hare", 100, 90), "Hare");
		runners.add(runner1);
		runners.add(runner2);
		return runners;
	}

	/**
	 * Loads a data from .txt file a location of which is in the received parameter "file",
	 * and returns a list of threads created according to the loaded data.
	 * 
	 * @param String file
	 * @return ArrayList<Thread>
	 */
	public ArrayList<Thread> loadTxt(String file) {
		ArrayList<Thread> runners = new ArrayList<>();
		Path filePath = Paths.get(file);
		if (Files.notExists(filePath)) {
			System.out.println();
			System.out.println("File '" + file + "' does not exist.");
			System.out.println("Please make sure that the file is in the correct directory: 'src/database/'");
			return null;
		}	
		String name = "";
		int speed = 0;
		int runChance = 0;
		File runnersFile = filePath.toFile();
		//Read data from the file
		try (BufferedReader in = new BufferedReader(new FileReader(runnersFile))) {
			String line = in.readLine();
			if (line == null) {
				in.close();
				return null;
			}
			while (line != null) {
				String[] runnerData = line.split(" ");
				name = runnerData[0];
				speed = Integer.parseInt(runnerData[1]);
				runChance = Integer.parseInt(runnerData[2]);
				runners.add(new Thread(new ThreadRunner(name, speed, runChance), name));
				line = in.readLine();
			}
			in.close();
		} 
		catch (IOException e) {
		    System.out.println(e);
		    return null;
		}
		return runners;
	}
	
	/**
	 * Loads a data from a .xml file a location of which is in the received parameter "file",
	 * and returns a list of threads created according to the loaded data.
	 * 
	 * @param String file
	 * @return ArrayList<Thread>
	 */
	public ArrayList<Thread> loadXml(String file) {
		ArrayList<Thread> runners = new ArrayList<>();
		Path filePath = Paths.get(file);
		if (Files.notExists(filePath)) {
			System.out.println();
			System.out.println("File '" + file + "' does not exist.");
			System.out.println("Please make sure that the file is in the correct directory: 'src/database/'");
			return null;
		}	
		// create the XMLInputFactory object
				XMLInputFactory inputFactory = XMLInputFactory.newFactory();
				try{
					// create an XMLStreamReader object
					FileReader fileReader =	new FileReader(file);
					XMLStreamReader reader = inputFactory.createXMLStreamReader(fileReader);
					// Read XML here
					String name = "";
					int speed = 0;
					int runChance = 0;
					while (reader.hasNext()){
						int eventType = reader.getEventType();
						switch (eventType){
							case XMLStreamConstants.START_ELEMENT:
								String elementName = reader.getLocalName();
								if (elementName.equals("Runner")){
									name = reader.getAttributeValue(0);
								} else	if (elementName.equals("RunnersMoveIncrement")){
									speed = Integer.parseInt(reader.getElementText());
								} else	if (elementName.equals("RestPercentage")){
									runChance = Integer.parseInt(reader.getElementText());
								}
								break;
							case XMLStreamConstants.END_ELEMENT:
								elementName = reader.getLocalName();
								if (elementName.equals("Runner")){							
									runners.add(new Thread(new ThreadRunner(name, speed, runChance), name));
								}
								break;
							default:
								break;
						}
						reader.next();
					}
				}
				catch (IOException | XMLStreamException e)
				{
				System.out.println(e);
				}
		return runners;
	}
	
	/**
	 * Loads a data from .db file a location of which is in the received parameter "file",
	 * and returns a list of threads created according to the loaded data. 
	 * If file does not exist, will create it and fill with default data.
	 * 
	 * @param String file
	 * @return ArrayList<Thread>
	 */
	public ArrayList<Thread> loadDb(String file) {
		ArrayList<Thread> runners = new ArrayList<>();
		String url = "jdbc:sqlite:" + file;
		// SQL statement for creating a new table if does not exists
        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS runners (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name VARCHAR(20),\n"
                + "	RunnersSpeed DOUBLE,\n"
                + "	RestPercentage DOUBLE\n"
                + ");";
        String sqlFillTable = "INSERT INTO runners (\r\n" + 
        		" Name,\r\n" + 
        		" RunnersSpeed,\r\n" +
        		" RestPercentage)\r\n" + 
        		"VALUES\r\n" + 
        		" (\r\n" + 
        		" 'Tortoise',\r\n" + 
        		" 10,\r\n" +
        		" 0),\r\n" +
        		" (\r\n" + 
        		" 'Hare',\r\n" + 
        		" 100,\r\n" +
        		" 90),\r\n" +
        		" (\r\n" + 
        		" 'Dog',\r\n" + 
        		" 50,\r\n" +
        		" 40),\r\n" +
        		" (\r\n" + 
        		" 'Cat',\r\n" + 
        		" 30,\r\n" +
        		" 75);";
        String sqlGetData = "SELECT * FROM runners";
        try (Connection conn = DriverManager.getConnection(url);
        		Statement stmt = conn.createStatement();
        		) {        	
        	DatabaseMetaData dbm = conn.getMetaData();
            ResultSet rs = dbm.getTables(null, null, "runners", null);
            if (!rs.next()) {
            	stmt.execute(sqlCreateTable);
            	stmt.execute(sqlFillTable);
            	System.out.println();
            	System.out.println("File '" + url + "' does not exist.");
            	System.out.println("Created SQLite database file '"  + url + "' and filled it with the default data.");
            } 
            rs = stmt.executeQuery(sqlGetData);
            String name = "";
            int speed = 0;
            int runChance = 0;
            while (rs.next()) {
            	name = rs.getString("Name");
            	speed = (int)rs.getDouble("RunnersSpeed");
            	runChance = (int)rs.getDouble("RestPercentage");
            	runners.add(new Thread(new ThreadRunner(name, speed, runChance), name));                                  
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return runners;
	}
}
