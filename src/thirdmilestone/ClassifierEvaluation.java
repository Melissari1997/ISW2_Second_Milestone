package thirdmilestone;
import weka.core.Instances;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import secondmilestone.VersionParser;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.RandomForest;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;



public class ClassifierEvaluation{
	private static int trainingPerc = 0;
	private static CSVWriter csvWriter = null;
	private static String projName;
	private static final String TRAINING = "Training";
	private static final String TESTING = "Testing";
	private static String naiveB = "Naive Bayes";
	private static String randomF = "Random Forest";
	private static String ibkStr = "IBK";
	private static String noFilter = "No";
	private static String filterYes = "Yes";
	private static String noSampler = "No";
	private static String samplerSmote = "SMOTE";
	private static String samplerOverSampling = "OverSampling";
	private static String samplerUnderSampling = "UnderSampling";
	private static Resample resample;
	private static SpreadSubsample  spreadSubsample;
	private static SMOTE smote;
	private static FilteredClassifier fc;

	public static void noSampler(Instances training, Instances testing, Instances filteredTraining, Instances testingFiltered, String version, String defectiveInTrainingPercent, String defectiveInTestingPercent){
		try {
		for(int i = 0; i < 3 ; i++) {
			if(i == 0) {
				NaiveBayes nb = new NaiveBayes();
				Evaluation evalResult =new Evaluation(testing);
				nb.buildClassifier(training);
				evalResult.evaluateModel(nb, testing);
				String[] extra = {noFilter,noSampler};
				writeOnCsv(version,naiveB,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,noSampler};
				nb.buildClassifier(filteredTraining);
				evalResult.evaluateModel(nb, testingFiltered);
				writeOnCsv(version,naiveB,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
			if(i == 1) {
				RandomForest rf = new RandomForest();
				Evaluation evalResult =new Evaluation(testing);
				rf.buildClassifier(training);
				evalResult.evaluateModel(rf, testing); 
				String[] extra = {noFilter,noSampler};
				writeOnCsv(version,randomF,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,noSampler};
				rf.buildClassifier(filteredTraining);
				evalResult.evaluateModel(rf, testingFiltered);
				writeOnCsv(version,randomF,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
			if(i == 2) {
				IBk ibk2 = new IBk();
				Evaluation evalResult =new Evaluation(testing);
				ibk2.buildClassifier(training);
				evalResult.evaluateModel(ibk2, testing); 
				String[] extra = {noFilter,noSampler};
				writeOnCsv(version,ibkStr,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,noSampler};
				ibk2.buildClassifier(filteredTraining);
				evalResult.evaluateModel(ibk2, testingFiltered);
				writeOnCsv(version,ibkStr,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void smote(Instances training, Instances testing, Instances filteredTraining, Instances testingFiltered, String version, String defectiveInTrainingPercent, String defectiveInTestingPercent) throws IOException {
		try {
		for(int i = 0; i< 3; i++) {
			if(i == 0) {
				smote.setInputFormat(training);
				NaiveBayes nb = new NaiveBayes();
				fc.setClassifier(nb);
				fc.setFilter(smote);
				Evaluation evalResult =new Evaluation(testing);
				fc.buildClassifier(training);
				evalResult.evaluateModel(fc, testing);
				String[] extra = {noFilter,samplerSmote};
				writeOnCsv(version,naiveB,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,samplerSmote};
				smote.setInputFormat(filteredTraining);
				fc.setFilter(smote);
				fc.buildClassifier(filteredTraining);
				evalResult.evaluateModel(fc, testingFiltered);
				writeOnCsv(version,naiveB,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);

			}
			if(i == 1) {
				RandomForest rf = new RandomForest();
				fc.setClassifier(rf);
				fc.setFilter(smote);
				Evaluation evalResult =new Evaluation(testing);
				fc.buildClassifier(training);
				evalResult.evaluateModel(fc, testing);
				String[] extra = {noFilter,samplerSmote};
				writeOnCsv(version,randomF,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,samplerSmote};
				smote.setInputFormat(filteredTraining);
				fc.setFilter(smote);
				fc.buildClassifier(filteredTraining);
				evalResult.evaluateModel(fc, testingFiltered);
				writeOnCsv(version,randomF,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
			if(i == 2) {
				IBk ibk2 = new IBk();
				smote.setInputFormat(training);
				fc.setClassifier(ibk2);
				fc.setFilter(smote);
				Evaluation evalResult =new Evaluation(testing);
				fc.buildClassifier(training);
				evalResult.evaluateModel(fc, testing);
				String[] extra = {noFilter,samplerSmote};
				writeOnCsv(version,ibkStr,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,samplerSmote};
				smote.setInputFormat(filteredTraining);
				fc.setFilter(smote);
				fc.buildClassifier(filteredTraining);
				evalResult.evaluateModel(fc, testingFiltered);
				writeOnCsv(version,ibkStr,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void overSampling(Instances training, Instances testing, Instances filteredTraining, Instances testingFiltered, String version, String defectiveInTrainingPercent, String defectiveInTestingPercent) throws IOException{
		try {
		for(int i = 0; i< 3; i++) {
			if(i == 0) {
				NaiveBayes nb = new NaiveBayes();
				resample.setInputFormat(training);
				fc.setClassifier(nb);
				fc.setFilter(resample);
				Evaluation evalResult =new Evaluation(testing);
				fc.buildClassifier(training);
				evalResult.evaluateModel(fc, training);
				String[] extra = {noFilter,samplerOverSampling};
				writeOnCsv(version,naiveB,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,samplerOverSampling};
				resample.setInputFormat(filteredTraining);
				fc.setFilter(resample);	
				fc.buildClassifier(filteredTraining);
				evalResult.evaluateModel(fc, testingFiltered);
				writeOnCsv(version,naiveB,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
			if(i == 1) {
				RandomForest rf = new RandomForest();
				resample.setInputFormat(training);
				fc.setClassifier(rf);
				fc.setFilter(resample);
				Evaluation evalResult =new Evaluation(testing);
				fc.buildClassifier(training);
				evalResult.evaluateModel(fc, training);
				String[] extra = {noFilter,samplerOverSampling};
				writeOnCsv(version,randomF,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,samplerOverSampling};
				resample.setInputFormat(filteredTraining);
				fc.setFilter(resample);
				fc.buildClassifier(filteredTraining);
				evalResult.evaluateModel(fc, testingFiltered);
				writeOnCsv(version,randomF,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
				
			}
			
			if(i == 2) {
				IBk ibk2 = new IBk();
				resample.setInputFormat(training);
				fc.setClassifier(ibk2);
				fc.setFilter(resample);
				Evaluation evalResult =new Evaluation(testing);
				fc.buildClassifier(training);
				evalResult.evaluateModel(fc, training);
				String[] extra = {noFilter,samplerOverSampling};
				writeOnCsv(version,ibkStr,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,samplerOverSampling};
				resample.setInputFormat(filteredTraining);
				fc.setFilter(resample);
				fc.buildClassifier(filteredTraining);
				evalResult.evaluateModel(fc, testingFiltered);
				writeOnCsv(version,ibkStr,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void underSampling(Instances training, Instances testing, Instances filteredTraining, Instances testingFiltered, String version, String defectiveInTrainingPercent, String defectiveInTestingPercent) throws IOException{
		try {
		for(int i = 0; i< 3; i++) {
			if(i == 0) {
				NaiveBayes nb = new NaiveBayes();
				spreadSubsample.setInputFormat(training);
				fc.setClassifier(nb);
				fc.setFilter(spreadSubsample);
				Evaluation evalResult =new Evaluation(testing);
				fc.buildClassifier(training);
				evalResult.evaluateModel(fc, training);
				String[] extra = {noFilter,samplerUnderSampling};
				writeOnCsv(version,naiveB,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,samplerUnderSampling};
				spreadSubsample.setInputFormat(filteredTraining);
				fc.setFilter(spreadSubsample);
				fc.buildClassifier(filteredTraining);
				evalResult.evaluateModel(fc, testingFiltered);
				writeOnCsv(version,naiveB,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
			if(i == 1) {
				RandomForest rf = new RandomForest();
				spreadSubsample.setInputFormat(training);
				fc.setClassifier(rf);
				fc.setFilter(spreadSubsample);
				Evaluation evalResult =new Evaluation(testing);
				fc.buildClassifier(training);
				evalResult.evaluateModel(fc, training);
				String[] extra = {noFilter,samplerUnderSampling};
				writeOnCsv(version,randomF,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,samplerUnderSampling};
				spreadSubsample.setInputFormat(filteredTraining);
				fc.setFilter(spreadSubsample);
				fc.buildClassifier(filteredTraining);
				evalResult.evaluateModel(fc, testingFiltered);
				writeOnCsv(version,randomF,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
			
			if(i == 2) {
				IBk ibk2 = new IBk();
				spreadSubsample.setInputFormat(training);
				fc.setClassifier(ibk2);
				fc.setFilter(spreadSubsample);
				Evaluation evalResult =new Evaluation(testing);
				fc.buildClassifier(training);
				String[] extra = {noFilter,samplerUnderSampling};
				evalResult.evaluateModel(fc, training);
				writeOnCsv(version,ibkStr,defectiveInTrainingPercent,defectiveInTestingPercent,extra,evalResult);
				String[] extra2 = {filterYes,samplerUnderSampling};
				spreadSubsample.setInputFormat(filteredTraining);
				fc.setFilter(spreadSubsample);
				fc.buildClassifier(filteredTraining);
				evalResult.evaluateModel(fc, testingFiltered);
				writeOnCsv(version,ibkStr,defectiveInTrainingPercent,defectiveInTestingPercent,extra2,evalResult);
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	
	}
	
	
	public static void writeOnCsv(String version, String classifierName, String defectiveInTrainingPercent, String defectiveInTestingPercent,String[] extra, Evaluation eval) throws IOException {
		String filter = extra[0];
		String balancing = extra[1];
		BigDecimal precision = new BigDecimal(Double.toString(eval.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		BigDecimal recall = new BigDecimal(Double.toString(eval.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		BigDecimal f1 = new BigDecimal(Double.toString(eval.fMeasure(1)));
		f1 = f1.setScale(2, RoundingMode.HALF_UP);
		BigDecimal auc = new BigDecimal(Double.toString(eval.areaUnderROC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		BigDecimal prc = new BigDecimal(Double.toString(eval.areaUnderPRC(1)));
		prc = prc.setScale(2, RoundingMode.HALF_UP);
		BigDecimal kappa = new BigDecimal(Double.toString(eval.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, classifierName,String.valueOf(trainingPerc),defectiveInTrainingPercent,defectiveInTestingPercent,filter, balancing,  String.valueOf(precision), String.valueOf(recall), String.valueOf(f1), String.valueOf(prc),String.valueOf(auc),String.valueOf(kappa)});
		csvWriter.flush();
	}
	
	public static void evaluation2(String version,double defectiveInTraining, double defectiveInTesting, int majorityPerc,Instances training, Instances testing){
		String defectiveInTrainingPercent = String.valueOf(defectiveInTraining);
		String defectiveInTestingPercent = String.valueOf(defectiveInTesting);
		int numAttr = training.numAttributes();
		training.setClassIndex(numAttr - 1);
		testing.setClassIndex(numAttr - 1);
		AttributeSelection filter = new AttributeSelection();
		CfsSubsetEval eval = new CfsSubsetEval();
		//GreedyStepwise search = new GreedyStepwise();
		BestFirst search = new BestFirst();
		//search.setSearchBackwards(true);
		//set the filter to use the evaluator and search algorithm
		filter.setEvaluator(eval);
		filter.setSearch(search);
		Instances filteredTraining = null;
		Instances testingFiltered = null;
		try {
			filter.setInputFormat(training);
			filteredTraining = Filter.useFilter(training, filter);
			int numAttrFiltered = filteredTraining.numAttributes();
			filteredTraining.setClassIndex(numAttrFiltered - 1);
			testingFiltered = Filter.useFilter(testing, filter);
			testingFiltered.setClassIndex(numAttrFiltered - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resample = new Resample();
		spreadSubsample = new SpreadSubsample();
		smote = new SMOTE();
		String[] overSamplingOpts = new String[]{ "-B", "1.0", "-Z", String.valueOf(majorityPerc*2)};
		String[] underSamplingOpts = new String[]{ "-M", "1.0"};
		try {
			resample.setOptions(overSamplingOpts);
			spreadSubsample.setOptions(underSamplingOpts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fc = new FilteredClassifier();
		noSampler(training,testing,filteredTraining,testingFiltered, version, defectiveInTrainingPercent,defectiveInTestingPercent);
		try {
			smote(training,testing,filteredTraining,testingFiltered, version, defectiveInTrainingPercent,defectiveInTestingPercent);
			overSampling(training,testing,filteredTraining,testingFiltered, version, defectiveInTrainingPercent,defectiveInTestingPercent);
			underSampling(training,testing,filteredTraining,testingFiltered, version, defectiveInTrainingPercent,defectiveInTestingPercent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static List<Double> computeDefectivePerc(String projName, int trainingRelease) throws IOException {
		
		List<String[]> trainingReleases = getReleases(projName, trainingRelease, TRAINING);
		List<String[]> testingReleases = getReleases(projName, trainingRelease, TESTING);
		int defectiveInTraining = 0;
		int defectiveInTesting = 0;
		for(String[] info: trainingReleases) {
			if(info[11].equals("yes") && Integer.parseInt(info[1]) <= trainingRelease) {  
				defectiveInTraining++;
			}
		}
			
		for(String[] release: testingReleases) {
			if(release[11].equals("yes") && Integer.parseInt(release[1]) == trainingRelease+1) {  
				defectiveInTesting++;
			}
		}	
		double percDefectiveInTraining = (((double)defectiveInTraining/(double)trainingReleases.size())*100);
		double percDefectiveInTesting = (((double)defectiveInTesting/(double)testingReleases.size())*100);
		List<Double> result = new ArrayList<>();
		result.add(percDefectiveInTraining);
		result.add(percDefectiveInTesting);
		return result; 
	}
	
	public static List<String[]> getReleases(String projName, int release, String type) throws IOException{
		  List<String[]> records = getAllReleases(projName);
		  List<String[]> releases = new ArrayList<>();
		  for(String[] record : records) {
			  if(type.equals(TRAINING) && Integer.valueOf(record[1]) <= release) {  
				  releases.add(record);
			  }
			  if(type.equals(TESTING) && Integer.valueOf(record[1]) == release + 1) {
				  releases.add(record);
			  }
		  }
		  return releases;
	}
	
	public static List<String[]> getAllReleases(String projName) throws IOException{
		Reader reader = Files.newBufferedReader(Paths.get(projName + "Dataset.csv"));
		  CSVReader csvReader = new CSVReader(reader,';',
	    		',', '\'',1);
		  List<String[]> records = csvReader.readAll();
		  csvReader.close();
		  return records;
	}
	public static int computeTrainingPerc(String projName, int trainingRelease) throws IOException {

	  List<String[]> trainingReleases = getReleases(projName, trainingRelease,TRAINING);
	  List<String[]> records = getAllReleases(projName);
	  return (int)(((double)trainingReleases.size()/(double)records.size())*100);
	  
	}
	public static int computeMajorityClass(String projName, int trainingRelease) throws IOException {
		List<String[]> records = getReleases(projName, trainingRelease, TESTING);
		int yes = 0;
		int no = 0;
		for(String[] info: records) {
			if(info[11].equals("yes"))
				yes++;
			else 
				no++;
			
		}
		if(yes>no) {
			return (int)(((double)yes/(double)records.size())*100);
		}
		else {
			return (int)(((double)no/(double)records.size())*100);
		}
	}
	public static void main(String[] args) throws Exception{
		//load datasets
		projName = "OPENJPA";
		csvWriter =  new CSVWriter(new FileWriter(projName + "Classification.csv"),';',
	            CSVWriter.NO_QUOTE_CHARACTER,
	            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	            CSVWriter.DEFAULT_LINE_END);
		csvWriter.writeNext(new String[] {"Dataset", "#Training" , "Classifier","%training", "%defective in training", "%defective in testing", "Feature Selection","Balancing", "Precision", "Recall","F1","AUPRC","ROC_Area", "Kappa"});
		VersionParser vp = new VersionParser();
		vp.setProgress(0.5);
		List<String> versionsList = vp.getVersionList(projName);
		versionsList.remove(versionsList.size()-1);
		versionsList.remove(versionsList.size()-1);
		for(String version : versionsList) {
			
			DataSource source1 = new DataSource(projName + version + "Training.arff");
			Instances training = source1.getDataSet();
			DataSource source2 = new DataSource(projName + version + "Test.arff");
			Instances testing = source2.getDataSet();
			trainingPerc = computeTrainingPerc(projName, Integer.valueOf(version));
			List<Double> defectivePerc = computeDefectivePerc(projName, Integer.parseInt(version));
			Double defectiveInTraining =defectivePerc.get(0);
			Double defectiveInTesting = defectivePerc.get(1);
			BigDecimal defectiveInTestingFormatted = new BigDecimal(Double.toString(defectiveInTesting));
			defectiveInTestingFormatted = defectiveInTestingFormatted.setScale(3, RoundingMode.HALF_UP);
			
			BigDecimal defectiveInTrainingFormatted = new BigDecimal(Double.toString(defectiveInTraining));
			defectiveInTrainingFormatted = defectiveInTrainingFormatted.setScale(3, RoundingMode.HALF_UP);
			int majorityPerc = computeMajorityClass(projName, Integer.parseInt(version));
			evaluation2(version,defectiveInTrainingFormatted.doubleValue(),defectiveInTestingFormatted.doubleValue(), majorityPerc, training, testing);

		}
		
		csvWriter.close();
	}
}
