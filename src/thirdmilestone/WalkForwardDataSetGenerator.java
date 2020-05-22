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
		BufferedWriter buffWriter = null;
		try {
	     buffWriter = new BufferedWriter(myWriter);
		
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
	    	 StringBuilder bld2 = new StringBuilder();
	    	 for(int i = 1; i < row.length;i++) {
	    		 bld2.append(row[i]+",");
	    	 }
	    	 String token = bld2.toString();
	    	 token = token.substring(0, token.length()-1);
	    	 buffWriter.write(token);
	    	 buffWriter.newLine();
	     }}catch(Exception e) {
	    	 e.printStackTrace();
	     }finally {
	     buffWriter.close();
	     }
	}
	
	public static void createTestDataSet(String projName, int testVersion, List<String[]> fileRecords) throws IOException{
		FileWriter myWriter = new FileWriter(projName + String.valueOf(testVersion) + "Test.arff");
		BufferedWriter buffWriter = null;
		try {
	     buffWriter = new BufferedWriter(myWriter);
	     buffWriter.write("@RELATION " + projName+ testVersion + "TestDataset");
	     buffWriter.newLine();
	     buffWriter.newLine();
	     List<String[]> versionsRows = new ArrayList<>();
	     for(String[] record : fileRecords) {
	    	 if((record[1]).equals(String.valueOf(testVersion+1))) {
	    		 versionsRows.add(record);
	    	 }	 
	     }
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
	     }finally {
	     buffWriter.close();
	     }
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
		 for(String version:versionsList) {
			 createTrainingDataSet(projName,Integer.valueOf(version), records);
			 createTestDataSet(projName, Integer.valueOf(version),records);
		 }
	}

}
