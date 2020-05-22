package thirdmilestone;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTrainingForDataSet {
	public static void createTrainingDataSet(String projName, int trainingVersion, List<String[]> fileRecords) throws IOException {
		FileWriter myWriter = new FileWriter(projName + String.valueOf(trainingVersion) + "Training.arff");
		try (
				BufferedWriter buffWriter = new BufferedWriter(myWriter)
	     ){
		
	     buffWriter.write("@RELATION "+ projName+ trainingVersion + "TrainingDataset");
	     buffWriter.newLine();
	     buffWriter.newLine();
	     List<String> fileNameAtt = new ArrayList<>();
	     
	     List<String> trainingVersions = new ArrayList<>();
	     for(int i = 0; i <= trainingVersion; i++) {
	    	 trainingVersions.add(String.valueOf(i));
	     }
	     List<String[]> versionsRows = new ArrayList<>();
	     for(String[] record : fileRecords) {	
	    	 if(trainingVersions.contains(record[1])) {
	    			 versionsRows.add(record);	 
	    	 }
	    	 if(!fileNameAtt.contains(record[0])) {
	    		 fileNameAtt.add(record[0]);
	    	 }
	    	 
	     }
	     WriteAttibutes.writeAttributes(buffWriter);
	     for( String[] versionRow : versionsRows) {
	    	 StringBuilder builder2 = new StringBuilder();
	    	 for(int i = 1; i < versionRow.length;i++) {
	    		 builder2.append(versionRow[i]+",");
	    	 }
	    	 String attributes = builder2.toString();
	    	 attributes = attributes.substring(0, attributes.length()-1);
	    	 buffWriter.write(attributes);
	    	 buffWriter.newLine();
	     }}catch(Exception e) {
	    	 e.printStackTrace();
	     }
	     }
}
