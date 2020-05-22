package thirdmilestone;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.opencsv.CSVReader;

import secondmilestone.VersionParser;
public class WalkForwardDataSetGenerator {
	
	public static void main(String[] args) throws IOException {
	     String projName = "BOOKKEEPER";
		
		 Reader reader = Files.newBufferedReader(Paths.get(projName + "Dataset.csv"));
		 CSVReader csvReader = new CSVReader(reader,';',
	    		',', '\'',1);
		 List<String[]> records = csvReader.readAll();
		 csvReader.close();
		 VersionParser vp = new VersionParser();
		 vp.setProgress(0.5);
		 List<String> versionsList = vp.getVersionList(projName); 
		 versionsList.remove(versionsList.size()-1);
		 versionsList.remove(versionsList.size()-1);
		 for(String version:versionsList) {
			 CreateTrainingForDataSet.createTrainingDataSet(projName,Integer.valueOf(version), records);
			 CreateTestForDataSet.createTestDataSet(projName, Integer.valueOf(version),records);
		 }
	}

}
