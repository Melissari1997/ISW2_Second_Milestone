package thirdmilestone;
import weka.core.Instances;

import java.io.FileWriter;
import java.util.List;

import com.opencsv.CSVWriter;

import secondmilestone.VersionParser;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.RandomForest;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.lazy.IBk;




public class ClassifierEvaluation{
	public static void main(String args[]) throws Exception{
		//load datasets
		String projName = "BOOKKEEPER";
		CSVWriter csvWriter =  new CSVWriter(new FileWriter(projName + "Classification.csv"),';',
	            CSVWriter.NO_QUOTE_CHARACTER,
	            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	            CSVWriter.DEFAULT_LINE_END);
		csvWriter.writeNext(new String[] {"Dataset", "#Training" , "Classifier", "Precision", "Recall", "AUC", "Kappa"});
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
			int numAttr = training.numAttributes();
			training.setClassIndex(numAttr - 1);
			testing.setClassIndex(numAttr - 1);
			NaiveBayes naiveBayerClassifier = new NaiveBayes();
			RandomForest randomForestClassifier = new RandomForest();
			IBk ibkClassifier = new IBk();

			naiveBayerClassifier.buildClassifier(training);
			randomForestClassifier.buildClassifier(training);
			ibkClassifier.buildClassifier(training);
			Evaluation eval = new Evaluation(testing);	
			
			eval.evaluateModel(naiveBayerClassifier, testing); 
			System.out.println(eval.toSummaryString("Evaluation results for Naive Bayes:\n", false));
			System.out.println("AUC for version " + version + " = "+eval.areaUnderROC(1));
			System.out.println("kappa for version " + version + " = "+eval.kappa());
			System.out.println("Precision for version " + version + " = " + eval.precision(1));
			System.out.println("Recall for version " + version + " = " + eval.recall(1));
			System.out.println("---------------");
			csvWriter.writeNext(new String[] {projName,version, "Naive Bayes", String.valueOf(eval.precision(1)), String.valueOf(eval.recall(1)), String.valueOf(eval.areaUnderPRC(1)),String.valueOf(eval.kappa())});
			eval.evaluateModel(randomForestClassifier, testing);
			System.out.println(eval.toSummaryString("Evaluation results for Random Forest:\n", false));
			System.out.println("AUC for version " + version + " = "+eval.areaUnderROC(1));
			System.out.println("kappa for version " + version + " = "+eval.kappa());
			System.out.println("Precision for version " + version + " = " + eval.precision(1));
			System.out.println("Recall for version " + version + " = " + eval.recall(1));

			csvWriter.writeNext(new String[] {projName,version, "Random Forest", String.valueOf(eval.precision(1)), String.valueOf(eval.recall(1)), String.valueOf(eval.areaUnderPRC(1)),String.valueOf(eval.kappa())});
			eval.evaluateModel(ibkClassifier, testing); 
			System.out.println(eval.toSummaryString("Evaluation results for IBk:\n", false));
			System.out.println("AUC for version " + version + " = "+eval.areaUnderROC(1));
			System.out.println("kappa for version " + version + " = "+eval.kappa());
			System.out.println("Precision for version " + version + " = " + eval.precision(1));
			System.out.println("Recall for version " + version + " = " + eval.recall(1));
			csvWriter.writeNext(new String[] {projName,version, "IBk", String.valueOf(eval.precision(1)), String.valueOf(eval.recall(1)), String.valueOf(eval.areaUnderPRC(1)),String.valueOf(eval.kappa())});
		}
		csvWriter.close();
		/*
		DataSource source1 = new DataSource("C:\\Users\\melis\\Desktop\\BOOKKEEPERDataset.arff");
		Instances training = source1.getDataSet();
		DataSource source2 = new DataSource("C:\\Users\\melis\\Desktop\\BOOKKEEPERDataset.arff");
		Instances testing = source2.getDataSet();
		int numAttr = training.numAttributes();
		training.setClassIndex(numAttr - 1);
		testing.setClassIndex(numAttr - 1);
	
		NaiveBayes classifier = new NaiveBayes();

		classifier.buildClassifier(training);

		Evaluation eval = new Evaluation(testing);	
		
		eval.evaluateModel(classifier, testing); 
		
		System.out.println("AUC = "+eval.areaUnderROC(1));
		System.out.println("kappa = "+eval.kappa());
		System.out.println("Precision: " + eval.precision(1));
		System.out.println("Recall: " + eval.recall(1));
		*/
	}
}
