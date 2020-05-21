package thirdmilestone;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import secondmilestone.VersionParser;
public class WalkForwardDataSetGenerator {
	public static void createTrainingDataSet(String projName, int trainingVersion, List<String[]> fileRecords) throws IOException {
		FileWriter myWriter = new FileWriter(projName + String.valueOf(trainingVersion) + "Training.arff");
	     BufferedWriter buffWriter = new BufferedWriter(myWriter);
	     buffWriter.write("@RELATION "+ projName+ String.valueOf(trainingVersion) + "TrainingDataset");
	     buffWriter.newLine();
	     buffWriter.newLine();
	     List<String> fileNameAtt = new ArrayList<>();
	     String fileNameAttribute = "{";
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
	     for(String fileName : fileNameAtt) {
	    	 fileNameAttribute += fileName + ",";
	     }
	     fileNameAttribute = fileNameAttribute.substring(0, fileNameAttribute.length()-1);
	     fileNameAttribute += "}";
	     //buffWriter.write("@ATTRIBUTE filename " + fileNameAttribute);
	     //buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE versionname REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE #revision REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE #fixcommit REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE size REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE churn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE maxchurn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE avgchurn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE chgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE maxchgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE avgchgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE buggy {no,yes}");
	     buffWriter.newLine();
	     buffWriter.write("@DATA");
	     buffWriter.newLine();
	     for( String[] row : versionsRows) {
	    	 String token = "";
	    	 for(int i = 1; i < row.length;i++) {
	    		 token += row[i] + ",";
	    	 }
	    	 token = token.substring(0, token.length()-1);
	    	 buffWriter.write(token);
	    	 buffWriter.newLine();
	     }
	     buffWriter.close();
	}
	
	public static void createTestDataSet(String projName, int testVersion, List<String[]> fileRecords) throws IOException{
		FileWriter myWriter = new FileWriter(projName + String.valueOf(testVersion) + "Test.arff");
	     BufferedWriter buffWriter = new BufferedWriter(myWriter);
	     buffWriter.write("@RELATION " + projName+ String.valueOf(testVersion) + "TestDataset");
	     buffWriter.newLine();
	     buffWriter.newLine();
	     String fileNameAttribute = "{";
	     List<String[]> versionsRows = new ArrayList<>();
	     for(String[] record : fileRecords) {
	    	 if((record[1]).equals(String.valueOf(testVersion+1))) {
	    		 fileNameAttribute += record[0]+",";
	    		 versionsRows.add(record);
	    		 
	    	 }
	    	 
	     }
	     fileNameAttribute = fileNameAttribute.substring(0, fileNameAttribute.length()-1);
	     fileNameAttribute += "}";
	     //buffWriter.write("@ATTRIBUTE filename " + fileNameAttribute);
	     //buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE versionname REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE #revision REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE #fixcommit REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE size REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE churn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE maxchurn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE avgchurn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE chgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE maxchgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE avgchgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE buggy {no,yes}");
	     buffWriter.newLine();
	     buffWriter.write("@DATA");
	     buffWriter.newLine();
	     for( String[] row : versionsRows) {
	    	 String token = "";
	    	 for(int i = 1; i < row.length;i++) {
	    		 token += row[i] + ",";
	    	 }
	    	 token = token.substring(0, token.length()-1);
	    	 buffWriter.write(token);
	    	 buffWriter.newLine();
	     }
	     buffWriter.close();
	}
	
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
		 System.out.println(versionsList);
		 for(String version:versionsList) {
			 createTrainingDataSet(projName,Integer.valueOf(version), records);
			 createTestDataSet(projName, Integer.valueOf(version),records);
		 }
		 
		 /*
	     FileWriter myWriter = new FileWriter("OPENJPA.arff");
	     BufferedWriter buffWriter = new BufferedWriter(myWriter);
	     buffWriter.write("@RELATION BOOKKEEPERDataset");
	     buffWriter.newLine();
	     buffWriter.newLine();
	     String fileNameAttribute = "{";
	     List<String[]> versionsRows = new ArrayList<>();
	     for(String[] record : records) {
	    	 if(record[1].equalsIgnoreCase("1")) {
	    		 fileNameAttribute += record[0]+",";
	    		 versionsRows.add(record);
	    		 
	    	 }
	    	 
	     }
	     fileNameAttribute = fileNameAttribute.substring(0, fileNameAttribute.length()-1);
	     fileNameAttribute += "}";
	     System.out.println(fileNameAttribute);
	     buffWriter.write("@ATTRIBUTE filename " + fileNameAttribute);
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE versionname REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE #revision REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE #fixcommit REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE size REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE churn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE maxchurn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE avgchurn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE chgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE maxchgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE avgchgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE buggy {no,yes}");
	     buffWriter.newLine();
	     buffWriter.write("@DATA");
	     buffWriter.newLine();
	     for( String[] row : versionsRows) {
	    	 String token = "";
	    	 for(int i = 0; i < row.length;i++) {
	    		 token += row[i] + ",";
	    	 }
	    	 token = token.substring(0, token.length()-1);
	    	 buffWriter.write(token);
	    	 buffWriter.newLine();
	     }
	     buffWriter.close();
	     */
	     System.out.println("Successfully wrote to the file.");
		

	}

}
