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

	public static void main(String[] args) throws IOException {
		String projName = "OPENJPA";
		String[] filterList = {"No", "Yes"};
		String[] balancerList = {"No", "SMOTE", "UnderSampling", "OverSampling"};
		String[] classifierList = {"Naive Bayes", "Random Forest", "IBK"};
		double precision = 0;
		double recall = 0;
		double f1 = 0;
		double roc = 0;
		double prc = 0;
		double kappa = 0;
		
		Reader reader = Files.newBufferedReader(Paths.get(projName + "Classification.csv"));
		  CSVReader csvReader = new CSVReader(reader,';',
	    		',', '\'',1);
		  List<String[]> records = csvReader.readAll();
		  csvReader.close();
		  CSVWriter writer =  new CSVWriter(new FileWriter(projName + "FinalResult.csv"),';',
		            CSVWriter.NO_QUOTE_CHARACTER,
		            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
		            CSVWriter.DEFAULT_LINE_END);
		  writer.writeNext(new String[]{"Classifier", "Feature Selection", "Balancing", "Precision", "Recall", "F1","AUPRC", "ROC_Area","Kappa"});
		for(String filter:filterList) {
			System.out.println(filter);
			for(String balancer:balancerList) {
				for(String classifier : classifierList) {
					precision = 0;
					recall = 0;
					f1 = 0;
					roc = 0;
					prc = 0;
					kappa = 0;
					for(int i = 0; i< records.size();i++) {
						if(records.get(i)[2].equals(classifier) && records.get(i)[6].equals(filter) && records.get(i)[7].equals(balancer)) {
							precision = precision + Double.valueOf(records.get(i)[8]);
							System.out.println("Actual precision "+precision);
							recall = recall + Double.valueOf(records.get(i)[9]);
							f1 = f1 + Double.valueOf(records.get(i)[10]);
							roc = roc + Double.valueOf(records.get(i)[12]);
							prc = prc + Double.valueOf(records.get(i)[11]);
							kappa = kappa + Double.valueOf(records.get(i)[13]);
						}
						
					}
					precision = precision/13;
					System.out.println("Normalized "+precision);
					recall = recall/13;
					roc = roc/13;
					prc = prc/13;
					f1 = f1/13;
					kappa=  kappa/13;
					BigDecimal precision_str = new BigDecimal(Double.toString(precision));
					precision_str = precision_str.setScale(2, RoundingMode.HALF_UP);
					BigDecimal recall_str = new BigDecimal(Double.toString(recall));
					recall_str = recall_str.setScale(2, RoundingMode.HALF_UP);
					BigDecimal f1_str = new BigDecimal(Double.toString(f1));
					f1_str = f1_str.setScale(2, RoundingMode.HALF_UP);
					BigDecimal auc_str = new BigDecimal(Double.toString(roc));
					auc_str = auc_str.setScale(2, RoundingMode.HALF_UP);
					BigDecimal prc_str = new BigDecimal(Double.toString(prc));
					prc_str = prc_str.setScale(2, RoundingMode.HALF_UP);
					BigDecimal kappa_str = new BigDecimal(Double.toString(kappa));
					kappa_str = kappa_str.setScale(2, RoundingMode.HALF_UP);
					System.out.println(precision_str);
					writer.writeNext(new String[]{classifier, filter,balancer,String.valueOf(precision_str),String.valueOf(recall_str),String.valueOf(f1_str),String.valueOf(prc_str),String.valueOf(auc_str),String.valueOf(kappa_str)});
					writer.flush();
				}
			}
		}
		writer.close();
	}

}
