package thirdmilestone;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

public class ClassifierEvaluator {
	private Instances training;
	private Instances testing;
	public ClassifierEvaluator(Instances training, Instances testing) {
		this.training = training;
		this.testing = testing;
	}
	public void setTraining(Instances training) {
		this.training = training;
	}
	public void setTesting(Instances testing) {
		this.testing = testing;
	}

	public Evaluation evaluateNaiveBayes(NaiveBayes naiveBayes) {
		try {
			naiveBayes.buildClassifier(this.training);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Evaluation evalClass = null;
		try {
			evalClass = new Evaluation(this.testing);
			evalClass.evaluateModel(naiveBayes, this.training);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return evalClass;
	}
	public Evaluation evaluateRandomForest(RandomForest randomForest) {
		try {
			randomForest.buildClassifier(this.training);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Evaluation evalClass = null;
		try {
			evalClass = new Evaluation(this.testing);
			evalClass.evaluateModel(randomForest, this.training);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return evalClass;
	}
	public Evaluation evaluateIBk(IBk ibk) {
		try {
			ibk.buildClassifier(this.training);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Evaluation evalClass = null;
		try {
			evalClass = new Evaluation(this.testing);
			evalClass.evaluateModel(ibk, this.training);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return evalClass;
	}
	public Evaluation evaluateFilteredClassifier(FilteredClassifier fc) {
		Evaluation evalClass = null;
		try {
			evalClass = new Evaluation(this.testing);
			evalClass.evaluateModel(fc, this.training);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return evalClass;
	}
	/*
	public FilteredClassifier setBalancer(Object balancer, String balancerName) {
		FilteredClassifier fc = new FilteredClassifier();
		SMOTE smote =null;
		Resample overSample = null;
		SpreadSubsample subSample = null;
		if(balancerName.equals("SMOTE")) {
			smote = (SMOTE) balancer;
			try {
				smote.setInputFormat(this.training);
			} catch (Exception e) {
				e.printStackTrace();
			}
			fc.setClassifier(this.classifier);
			fc.setFilter(smote);
			
		}
		if(balancerName.contentEquals("Oversample")) {
			overSample = (Resample) balancer;
			try {
				overSample.setInputFormat(this.training);
			} catch (Exception e) {
				e.printStackTrace();
			}
			fc.setClassifier(this.classifier);
			fc.setFilter(overSample);
			
		}
		if(balancerName.contentEquals("Undersample")) {
			subSample = (SpreadSubsample) balancer;
			try {
				subSample.setInputFormat(this.training);
			} catch (Exception e) {
				e.printStackTrace();
			}
			fc.setClassifier(this.classifier);
			fc.setFilter(subSample);
			
		}	
		return fc;
	}
	*/
}
