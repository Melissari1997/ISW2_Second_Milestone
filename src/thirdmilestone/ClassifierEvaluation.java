package thirdmilestone;
import weka.core.Instances;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

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
	public static void evaluation(String projName, String version, List<Object> classifiers, Instances training, Instances testing, CSVWriter csvWriter) throws Exception {
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
		String[] overSamplingOpts = new String[]{ "-B", "1.0", "-Z", "130.0"};
		resample.setOptions(overSamplingOpts);
		
		FilteredClassifier fc = new FilteredClassifier();
		SpreadSubsample  spreadSubsample = new SpreadSubsample();
		String[] underSamplingOpts = new String[]{ "-M", "1.0"};
		spreadSubsample.setOptions(underSamplingOpts);
		SMOTE smote = new SMOTE();
		
		
		//No filter & No balancing
		evalClass.evaluateModel(naiveBayes, testing); 
		//System.out.println(eval.toSummaryString("Evaluation results for Naive Bayes:\n", false));
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes","No filter", "None", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		//Filter & No balancing
		naiveBayes.buildClassifier(filteredTraining);
		evalClass.evaluateModel(naiveBayes, testingFiltered);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes","Filter", "None", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		//No filter & underSample
		NaiveBayes naiveBayes2 = new NaiveBayes();
		fc.setClassifier(naiveBayes2);
		fc.setFilter(spreadSubsample);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing); 
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes","No filter", "UnderSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		//filter & undersample
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes","Filter", "UnderSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		// No filter & smote
		NaiveBayes naiveBayes3 = new NaiveBayes();
		smote.setInputFormat(training);
		fc.setClassifier(naiveBayes3);
		fc.setFilter(smote);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing); 
		//System.out.println(eval.toSummaryString("Evaluation results for Naive Bayes:\n", false));
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes","No filter", "SMOTE", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		//filter & smote
		smote.setInputFormat(filteredTraining);
		fc.setFilter(smote);
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes","Filter", "SMOTE", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		// No filter & oversampling
		NaiveBayes naiveBayes4 = new NaiveBayes();
		resample.setInputFormat(training);
		fc.setClassifier(naiveBayes4);
		fc.setFilter(resample);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes","No filter", "OverSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		// filter & oversampling
		resample.setInputFormat(filteredTraining);
		fc.setFilter(resample);
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		csvWriter.writeNext(new String[] {projName,version, "Naive Bayes","Filter", "OverSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		
		
		
		
		
		
		
		//No filter & No balancing
		evalClass.evaluateModel(randomForest, testing); 
		//System.out.println(eval.toSummaryString("Evaluation results for Naive Bayes:\n", false));
		csvWriter.writeNext(new String[] {projName,version, "RandomForest","No filter", "None", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		//Filter & No balancing
		randomForest.buildClassifier(filteredTraining);
		evalClass.evaluateModel(randomForest, testingFiltered);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest","Filter", "None", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		//No filter & underSample
		RandomForest randomForest2 = new RandomForest();
		fc.setClassifier(randomForest2);
		fc.setFilter(spreadSubsample);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing); 
		csvWriter.writeNext(new String[] {projName,version, "RandomForest","No filter", "UnderSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		//filter & undersample
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest","Filter", "UnderSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		// No filter & smote
		RandomForest randomForest3 = new RandomForest();
		smote.setInputFormat(training);
		fc.setClassifier(randomForest3);
		fc.setFilter(smote);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing); 
		//System.out.println(eval.toSummaryString("Evaluation results for Naive Bayes:\n", false));
		csvWriter.writeNext(new String[] {projName,version, "RandomForest","No filter", "SMOTE", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		//filter & smote
		smote.setInputFormat(filteredTraining);
		fc.setFilter(smote);
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest","Filter", "SMOTE", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		// No filter & oversampling
		RandomForest randomForest4 = new RandomForest();
		resample.setInputFormat(training);
		fc.setClassifier(randomForest4);
		fc.setFilter(resample);
		fc.buildClassifier(training);
		evalClass.evaluateModel(fc, testing);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest","No filter", "OverSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		// filter & oversampling
		resample.setInputFormat(filteredTraining);
		fc.setFilter(resample);
		fc.buildClassifier(testingFiltered);
		evalClass.evaluateModel(fc, testingFiltered);
		csvWriter.writeNext(new String[] {projName,version, "RandomForest","Filter", "OverSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
		
		
		
		
		
		
		
		//No filter & No balancing
				evalClass.evaluateModel(ibk, testing); 
				//System.out.println(eval.toSummaryString("Evaluation results for Naive Bayes:\n", false));
				csvWriter.writeNext(new String[] {projName,version, "IBK","No filter", "None", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
				//Filter & No balancing
				randomForest.buildClassifier(filteredTraining);
				evalClass.evaluateModel(randomForest, testingFiltered);
				csvWriter.writeNext(new String[] {projName,version, "IBK","Filter", "None", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
				//No filter & underSample
				IBk ibk2 = new IBk();
				fc.setClassifier(ibk2);
				fc.setFilter(spreadSubsample);
				fc.buildClassifier(training);
				evalClass.evaluateModel(fc, testing); 
				csvWriter.writeNext(new String[] {projName,version, "IBK","No filter", "UnderSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
				//filter & undersample
				fc.buildClassifier(testingFiltered);
				evalClass.evaluateModel(fc, testingFiltered);
				csvWriter.writeNext(new String[] {projName,version, "IBK","Filter", "UnderSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
				
				// No filter & smote
				IBk ibk3 = new IBk();
				smote.setInputFormat(training);
				fc.setClassifier(ibk3);
				fc.setFilter(smote);
				fc.buildClassifier(training);
				evalClass.evaluateModel(fc, testing); 
				//System.out.println(eval.toSummaryString("Evaluation results for Naive Bayes:\n", false));
				csvWriter.writeNext(new String[] {projName,version, "IBK","No filter", "SMOTE", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
				
				//filter & smote
				smote.setInputFormat(filteredTraining);
				fc.setFilter(smote);
				fc.buildClassifier(testingFiltered);
				evalClass.evaluateModel(fc, testingFiltered);
				csvWriter.writeNext(new String[] {projName,version, "IBK","Filter", "SMOTE", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
				
				// No filter & oversampling
				IBk ibk4 = new IBk();
				resample.setInputFormat(training);
				fc.setClassifier(ibk4);
				fc.setFilter(resample);
				fc.buildClassifier(training);
				evalClass.evaluateModel(fc, testing);
				csvWriter.writeNext(new String[] {projName,version, "IBK","No filter", "OverSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
				
				// filter & oversampling
				resample.setInputFormat(filteredTraining);
				fc.setFilter(resample);
				fc.buildClassifier(testingFiltered);
				evalClass.evaluateModel(fc, testingFiltered);
				csvWriter.writeNext(new String[] {projName,version, "IBK","Filter", "OverSampling", String.valueOf(evalClass.precision(1)), String.valueOf(evalClass.recall(1)), String.valueOf(evalClass.areaUnderPRC(1)),String.valueOf(evalClass.kappa())});
	}
	
	public static void main(String args[]) throws Exception{
		//load datasets
		String projName = "BOOKKEEPER";
		CSVWriter csvWriter =  new CSVWriter(new FileWriter(projName + "Classification.csv"),';',
	            CSVWriter.NO_QUOTE_CHARACTER,
	            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	            CSVWriter.DEFAULT_LINE_END);
		csvWriter.writeNext(new String[] {"Dataset", "#Training" , "Classifier","Feature Selection","Balancing", "Precision", "Recall", "AUC", "Kappa"});
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
			evaluation(projName, version, classifiers, training, testing, csvWriter);

		}
		csvWriter.close();
	}
}
