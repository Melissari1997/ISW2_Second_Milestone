package thirdmilestone;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTestForDataSet {
	private  CreateTestForDataSet() {
		
	}
	
	public static void createTestDataSet(String projName, int testVersion, List<String[]> fileRecords) throws IOException{
		FileWriter myWriter = new FileWriter(projName + String.valueOf(testVersion) + "Test.arff");
		
		try (
				BufferedWriter buffWriter = new BufferedWriter(myWriter);)
		{
	     buffWriter.write("@RELATION " + projName+ testVersion + "TestDataset");
	     buffWriter.newLine();
	     buffWriter.newLine();
	     List<String[]> versionsRows = new ArrayList<>();
	     for(String[] record : fileRecords) {
	    	 if((record[1]).equals(String.valueOf(testVersion+1))) {
	    		 versionsRows.add(record);
	    	 }	 
	     }
	     WriteAttributes.writeAttributes(buffWriter);
	     for( String[] row : versionsRows) {
	    	 StringBuilder bld2 = new StringBuilder();
	    	 for(int i = 1; i < row.length;i++) {
	    		 bld2.append(row[i] + ",");
	    	 }
	    	 String token = bld2.toString();
	    	 token = token.substring(0, token.length()-1);
	    	 buffWriter.write(token);
	    	 buffWriter.newLine();
	     }}catch(Exception e) {
	    	 e.printStackTrace();
	     }
	     
	}
}
