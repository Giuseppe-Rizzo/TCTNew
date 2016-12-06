
import it.uniba.di.lacam.ontologymining.tct.*;
import it.uniba.di.lacam.ontologymining.tct.distances.*;
import it.uniba.di.lacam.ontologymining.tct.parameters.Parameters;
import it.uniba.di.lacam.ontologymining.tct.refinementoperators.RefinementOperator;
import it.uniba.di.lacam.ontologymining.tct.utils.Couple;
import it.uniba.di.lacam.ontologymining.tct.utils.MathUtils;
import it.uniba.di.lacam.ontologymining.variableassociation.Apriori;
import it.uniba.di.lacam.ontologymining.variableassociation.Correlations;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.Reasoner;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Intersection;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Negation;



//import samplers.DatasetAcquisition;
//import samplers.DatasetGenerator;






public class Main {
	
static it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase kb;
//	static int[][] classification;
 public  PrintStream console = System.out;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Parameters.loadParameters(); //loading from property file
		System.out.println(Parameters.urlOwlFile);
		kb = new it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase(Parameters.urlOwlFile);
		
	//	Reasoner r=;
		double [] resultsAxs = new double[Parameters.NFOLDS];
		double [] resultsInc = new double[Parameters.NFOLDS];
		
		for (int j=1; j<Parameters.NFOLDS;j++){
		final Individual[] individuals = kb.getIndividuals();
		final AbstractReasonerComponent reasoner = kb.getReasoner();
		final NamedClass[] classes = kb.getClasses();
		
		it.uniba.di.lacam.ontologymining.tct.distances.FeaturesDrivenDistance.preLoadPi(reasoner, classes, individuals);
		FeaturesDrivenDistance.computeFeatureEntropies(reasoner, classes);
		System.out.println( "************************Discovering disjointntness axioms ********** ");
		if (args[0].equalsIgnoreCase("apriori")){
		System.out.println("Learning algorithm: A priori");
			Apriori apriori= new Apriori(reasoner, classes, individuals);
			ArrayList<HashSet<Description>> arrayList = new ArrayList<HashSet<Description>>();
			ArrayList<HashSet<Description>> arrayList2 = new ArrayList<HashSet<Description>>();
			apriori.generateCandidate(arrayList, 1, 2);
			for (HashSet<Description> hashSet : arrayList) {
				boolean disjoint= false;
				for (Description description : hashSet) {
					if (description instanceof Negation){
						disjoint = true;
					}
				}
				if (disjoint)
					arrayList2.add(hashSet);

			} 		
			System.out.println("Number of axioms : "+ arrayList2.size());
 	}
		else if (args[0].equalsIgnoreCase("tct")){	
//			//TODO da decommentare	
			TCTInducer2 t = new TCTInducer2(kb);
			RefinementOperator op = new RefinementOperator(kb);
			ArrayList<Integer> list= new ArrayList<Integer>();
			for (int i = 0; i<individuals.length;i++)
				list.add(i);
			ClusterTree induceDLTree = t.induceDLTree(list, new ArrayList<Integer>(),  new ArrayList<Integer>(), 4, op);
			//System.out.println(induceDLTree);	 
			final ArrayList<Couple<Description,Description>> extractDisjointnessAxiom = t.extractDisjointnessAxiom(induceDLTree);
			System.out.println("Number of axioms: "+ extractDisjointnessAxiom.size());
			resultsAxs[j-1]= extractDisjointnessAxiom.size();
			int nInc=0;
			for (Couple<Description,Description> c:extractDisjointnessAxiom){
			    if (reasoner.getIndividuals(new Intersection(c.getFirstElement(),c.getSecondElement())).size()!=0){
			    	nInc++;
			    	//System.out.println("Number of inconsistencies: "+ nInc);
			    }
			}
			System.out.println("Number of inconsistencies: "+ nInc);
			resultsInc[j-1]= nInc;
		}
		else  if ( (args[0].equalsIgnoreCase("corr"))){
			Correlations corr= new Correlations(reasoner, classes, individuals);
			final ArrayList<Couple<Description,Description>> extractDisjointnessAxiom = corr.computeCorrelation();
			System.out.println("Number of axioms: "+ extractDisjointnessAxiom.size());
			int nInc=0;
			resultsAxs[j-1]= extractDisjointnessAxiom.size();
			for (Couple<Description,Description> c:extractDisjointnessAxiom){
			    if (reasoner.getIndividuals(new Intersection(c.getFirstElement(),c.getSecondElement())).size()!=0){
			    	nInc++;
			    	System.out.println("Number of inconsistencies: "+ nInc);
			    }
			}
			System.out.println("Number of inconsistencies: "+ nInc);
			resultsInc[j-1]= nInc;
		}
		else{
			System.out.println("Please, insert one of the following parameters:");
			System.out.println( "'apriori' - for running the association rule mining algorithm");
			System.out.println("'tct- for running the terminological cluster tree induction algorithm");
			System.out.println("corr- for running the negative correlation algorithm ");
			
		 break;
		}
		}
		System.out.println("***************************************************************");
		System.out.println(" Overall Results");
		System.out.println("Axioms: "+MathUtils.avg(resultsAxs)+" ("+MathUtils.stdDeviation(resultsAxs)+")");
		System.out.println("Inconsistencies: "+MathUtils.avg(resultsInc)+" ("+MathUtils.stdDeviation(resultsInc)+")");
		
//	
////
////		
	
////	
		
	
	 
	
	//Correlations corr= new Correlations(reasoner, classes, individuals);
		//corr.computeCorrelation();
//		
	
			
System.out.println("\n\nEnding: "+Parameters.urlOwlFile);

	} // main
	
	


} // class DLTreeInducer