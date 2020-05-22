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
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
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
	public static CSVWriter csvWriter = null;
	public static String projName;
	public static String training = "Training";
	public static String testing = "Testing";
	
	public static void evaluation(String version,double defectiveInTraining, double defectiveInTesting, int majorityPerc, List<Object> classifiers, Instances training, Instances testing) throws Exception {
		
		String trainingPercent = String.valueOf(trainingPerc);
		String defectiveInTrainingPercent = String.valueOf(defectiveInTraining);
		String defectiveInTestingPercent = String.valueOf(defectiveInTesting);
		
		NaiveBayes naiveBayes = (NaiveBayes) classifiers.get(0);
		RandomForest randomForest = (RandomForest) classifiers.get(1);
		IBk ibk = (IBk) classifiers.get(2);
		int numAttr = training.numAttributes();
		training.setClassIndex(numAttr - 1);
		testing.setClassIndex(numAttr - 1);
		AttributeSelection filter = new AttributeSelection();
		CfsSubsetEval eval = new CfsSubsetEval();
		GreedyStepwise search = new GreedyStepwise();
		search.setSearchBackwards(true);
		//set the filter to use the evaluator and search algorithm
		filter.setEvaluator(eval);
		filter.setSearch(search);
		//specify the dataset
		filter.setInputFormat(training);
		//apply
		Instances filteredTraining = Filter.useFilter(training, filter);
		int numAttrFiltered = filteredTraining.numAttributes();
		filteredTraining.setClassIndex(numAttrFiltered - 1);
		Instances testingFiltered = Filter.useFilter(testing, filter);
		testingFiltered.setClassIndex(numAttrFiltered - 1);

		naiveBayes.buildClassifier(training);
		randomForest.buildClassifier(training);
		ibk.buildClassifier(training);
		Evaluation evalClass = new Evaluation(testing);	
		Resample resample = new Resample();
		String[] overSamplingOpts = new String[]{ "-B", "1.0", "-Z", String.valueOf(majorityPerc*2)};
		resample.setOptions(overSamplingOpts);
		
		FilteredClassifier fc = new FilteredClassifier();
		SpreadSubsample  spreadSubsample = new SpreadSubsample();
		String[] underSamplingOpts = new String[]{ "-M", "1.0"};
		spreadSubsample.setOptions(underSamplingOpts);
		SMOTE smote = new SMOTE();
		
		
		//No filter & No balancing
		evalClass.evaluateModel(naiveBayes, testing); 
		BigDecimal precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		BigDecimal recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		BigDecimal auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		BigDecimal kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"No filter", "None", String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		//Filter & No balancing
		naiveBayes.buildClassifier(filteredTraining);
		evalClass.evaluateModel(naiveBayes, testingFiltered);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = precision.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"Filter", "None", String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		//No filter & underSample
		NaiveBayes naiveBayes2 = new NaiveBayes();
		fc.setClassifier(naiveBayes2);
		fc.setFilter(spreadSubsample);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing); 
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = precision.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"No filter", "UnderSampling",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		//filter & undersample
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = precision.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"Filter", "UnderSampling",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		// No filter & smote
		NaiveBayes naiveBayes3 = new NaiveBayes();
		smote.setInputFormat(training);
		fc.setClassifier(naiveBayes3);
		fc.setFilter(smote);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing); 
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"No filter", "SMOTE",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		//filter & smote
		smote.setInputFormat(filteredTraining);
		fc.setFilter(smote);
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"Filter", "SMOTE",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		// No filter & oversampling
		NaiveBayes naiveBayes4 = new NaiveBayes();
		resample.setInputFormat(training);
		fc.setClassifier(naiveBayes4);
		fc.setFilter(resample);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"No filter", "OverSampling",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		// filter & oversampling
		resample.setInputFormat(filteredTraining);
		fc.setFilter(resample);
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"Filter", "OverSampling",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		
		
		
		
		
		
		
		//No filter & No balancing
		evalClass.evaluateModel(randomForest, testing); 
		csvWriter.writeNext(new String[] {projName,version, "RandomForest",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"No filter", "None",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		//Filter & No balancing
		randomForest.buildClassifier(filteredTraining);
		evalClass.evaluateModel(randomForest, testingFiltered);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"Filter", "None",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		//No filter & underSample
		RandomForest randomForest2 = new RandomForest();
		fc.setClassifier(randomForest2);
		fc.setFilter(spreadSubsample);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing); 
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"No filter", "UnderSampling", String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		//filter & undersample
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"Filter", "UnderSampling",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		// No filter & smote
		RandomForest randomForest3 = new RandomForest();
		smote.setInputFormat(training);
		fc.setClassifier(randomForest3);
		fc.setFilter(smote);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing); 
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"No filter", "SMOTE",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		//filter & smote
		smote.setInputFormat(filteredTraining);
		fc.setFilter(smote);
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"Filter", "SMOTE", String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		// No filter & oversampling
		RandomForest randomForest4 = new RandomForest();
		resample.setInputFormat(training);
		fc.setClassifier(randomForest4);
		fc.setFilter(resample);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"No filter", "OverSampling",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		// filter & oversampling
		resample.setInputFormat(filteredTraining);
		fc.setFilter(resample);
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		precision = new BigDecimal(Double.toString(evalClass.precision(1)));
		precision = precision.setScale(2, RoundingMode.HALF_UP);
		recall = new BigDecimal(Double.toString(evalClass.recall(1)));
		recall = recall.setScale(2, RoundingMode.HALF_UP);
		auc = new BigDecimal(Double.toString(evalClass.areaUnderPRC(1)));
		auc = auc.setScale(2, RoundingMode.HALF_UP);
		kappa = new BigDecimal(Double.toString(evalClass.kappa()));
		kappa = kappa.setScale(2, RoundingMode.HALF_UP);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest",trainingPercent,defectiveInTrainingPercent,defectiveInTestingPercent,"Filter", "OverSampling",  String.valueOf(precision), String.valueOf(recall), String.valueOf(auc),String.valueOf(kappa)});
		
		
		
		
		
		
		
		
	}
	
	public static List<Double> computeDefectivePerc(String projName, int trainingRelease) throws IOException {
		
		List<String[]> trainingReleases = getReleases(projName, trainingRelease, training);
		List<String[]> testingReleases = getReleases(projName, trainingRelease, testing);
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
			  if(type.equals("Training") && Integer.valueOf(record[1]) <= release) {  
				  releases.add(record);
			  }
			  if(type.equals("Testing") && Integer.valueOf(record[1]) == release + 1) {
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

	  List<String[]> trainingReleases = getReleases(projName, trainingRelease,training);
	  List<String[]> records = getAllReleases(projName);
	  return (int)(((double)trainingReleases.size()/(double)records.size())*100);
	  
	}
	public static int computeMajorityClass(String projName, int trainingRelease) throws IOException {
		List<String[]> records = getReleases(projName, trainingRelease, testing);
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
		String projName = "OPENJPA";
		CSVWriter csvWriter =  new CSVWriter(new FileWriter(projName + "Classification.csv"),';',
	            CSVWriter.NO_QUOTE_CHARACTER,
	            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	            CSVWriter.DEFAULT_LINE_END);
		csvWriter.writeNext(new String[] {"Dataset", "#Training" , "Classifier","%training", "%defective in training", "%defective in testing", "Feature Selection","Balancing", "Precision", "Recall", "AUC", "Kappa"});
		VersionParser vp = new VersionParser();
		vp.setProgress(0.5);
		List<String> versionsList = vp.getVersionList(projName);
		versionsList.remove(versionsList.size()-1);
		versionsList.remove(versionsList.size()-1);
		List<Object> classifiers = new ArrayList<>();
		NaiveBayes naiveBayesClassifier = new NaiveBayes();
		RandomForest randomForestClassifier = new RandomForest();
		IBk ibkClassifier = new IBk();
		
		classifiers.add(naiveBayesClassifier);
		classifiers.add(randomForestClassifier);
		classifiers.add(ibkClassifier);
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
			evaluation(version,defectiveInTrainingFormatted.doubleValue(),defectiveInTestingFormatted.doubleValue(), majorityPerc, classifiers, training, testing);

		}
		csvWriter.close();
	}
}
