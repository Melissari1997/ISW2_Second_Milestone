package thirdmilestone;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class FinalEval {
	public static void classify(String projName, String classifier, String filter, String balancer, CSVWriter writer) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(projName + "Classification.csv"));
		  CSVReader csvReader = new CSVReader(reader,';',
	    		',', '\'',1);
		  List<String[]> records = csvReader.readAll();
		  csvReader.close();
		double precision = 0;
		double recall = 0;
		double f1 = 0;
		double roc = 0;
		double prc = 0;
		double kappa = 0;
		precision = 0;
		recall = 0;
		f1 = 0;
		roc = 0;
		prc = 0;
		kappa = 0;
		for(int i = 0; i< records.size();i++) {
			if(records.get(i)[2].equals(classifier) && records.get(i)[6].equals(filter) && records.get(i)[7].equals(balancer)) {
				precision = precision + Double.valueOf(records.get(i)[8]);
			
				recall = recall + Double.valueOf(records.get(i)[9]);
				f1 = f1 + Double.valueOf(records.get(i)[10]);
				roc = roc + Double.valueOf(records.get(i)[12]);
				prc = prc + Double.valueOf(records.get(i)[11]);
				kappa = kappa + Double.valueOf(records.get(i)[13]);
			}
			
		}
		precision = precision/5;
		recall = recall/5;
		roc = roc/5;
		prc = prc/5;
		f1 = f1/5;
		kappa=  kappa/5;
		BigDecimal precisionStr = new BigDecimal(Double.toString(precision));
		precisionStr = precisionStr.setScale(2, RoundingMode.HALF_UP);
		BigDecimal recallStr = new BigDecimal(Double.toString(recall));
		recallStr = recallStr.setScale(2, RoundingMode.HALF_UP);
		BigDecimal f1Str = new BigDecimal(Double.toString(f1));
		f1Str = f1Str.setScale(2, RoundingMode.HALF_UP);
		BigDecimal aucStr = new BigDecimal(Double.toString(roc));
		aucStr = aucStr.setScale(2, RoundingMode.HALF_UP);
		BigDecimal prcStr = new BigDecimal(Double.toString(prc));
		prcStr = prcStr.setScale(2, RoundingMode.HALF_UP);
		BigDecimal kappaStr = new BigDecimal(Double.toString(kappa));
		kappaStr = kappaStr.setScale(2, RoundingMode.HALF_UP);
		
		writer.writeNext(new String[]{classifier, filter,balancer,String.valueOf(precisionStr),String.valueOf(recallStr),String.valueOf(f1Str),String.valueOf(prcStr),String.valueOf(aucStr),String.valueOf(kappaStr)});
		writer.flush();
	}
	public static void main(String[] args) throws IOException {
		String projName = "BOOKKEEPER";
		String[] filterList = {"No", "Yes"};
		String[] balancerList = {"No", "SMOTE", "UnderSampling", "OverSampling"};
		String[] classifierList = {"Naive Bayes", "Random Forest", "IBK"};
		Reader reader = Files.newBufferedReader(Paths.get(projName + "Classification.csv"));
		  CSVReader csvReader = new CSVReader(reader,';',
	    		',', '\'',1);
		  csvReader.readAll();
		  csvReader.close();
		  CSVWriter writer =  new CSVWriter(new FileWriter(projName + "FinalResult.csv"),';',
		            CSVWriter.NO_QUOTE_CHARACTER,
		            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
		            CSVWriter.DEFAULT_LINE_END);
		  writer.writeNext(new String[]{"Classifier", "Feature Selection", "Balancing", "Precision", "Recall", "F1","AUPRC", "ROC_Area","Kappa"});
		for(String filter:filterList) {
			for(String balancer:balancerList) {
				for(String classifier : classifierList) {
					classify(projName, classifier, filter, balancer, writer);
				}
			}
		}
		writer.close();
	}

}
