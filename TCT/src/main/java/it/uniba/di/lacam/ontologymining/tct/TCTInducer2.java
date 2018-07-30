
package it.uniba.di.lacam.ontologymining.tct;
import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;
import it.uniba.di.lacam.ontologymining.tct.distances.FeaturesDrivenDistance;
import it.uniba.di.lacam.ontologymining.tct.parameters.Parameters;
import it.uniba.di.lacam.ontologymining.tct.refinementoperators.RefinementOperator;
import it.uniba.di.lacam.ontologymining.tct.refinementoperators.RhoRefinementOperator;
import it.uniba.di.lacam.ontologymining.tct.refinementoperators.SparkRefinementOperator;
import it.uniba.di.lacam.ontologymining.tct.utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;

import org.apache.spark.api.java.JavaRDD;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Intersection;
import org.dllearner.core.owl.Negation;
import org.dllearner.core.owl.Nothing;
import org.dllearner.core.owl.Thing;

import com.fasterxml.jackson.core.JsonFactory.Feature;




/**
 *  A class for inducing terminological cluster trees
 * @author Giuseppe Rizzo
 *
 */

public class TCTInducer2 {

	TCT2DisAxsConverter data;

	public TCTInducer2(KnowledgeBase k){

		data= new TCT2DisAxsConverter();
		data.kb=k;
		//	super(k);

	}


	/**
	 * Induction of clustering tree for description logics
	 * @param posExs
	 * @param negExs
	 * @param undExs
	 * @param dim
	 * @param op
	 * @return
	 */
	public ClusterTree induceDLTree(ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs, 
			int dim, RefinementOperator op) {		
		System.out.printf("Learning problem\t p:%d\t n:%d\t u:%d\t prPos:%4f\t prNeg:%4f\n", 
				posExs.size(), negExs.size(), undExs.size(), 0.5, 0.5);
		//		ArrayList<Integer> truePos= posExs;
		//		ArrayList<Integer> trueNeg= negExs;

		long startingTime= System.currentTimeMillis();

		Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double> examples = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExs, negExs, undExs, dim, 0.5, 0.5);
		Description concept = new Thing();
		ClusterTree tree = new ClusterTree((Description)concept,  posExs, 0,0); // new (sub)tree



		Stack<Couple<ClusterTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>> stack= new Stack<Couple<ClusterTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>>();
		Couple<ClusterTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> toInduce= new Couple<ClusterTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
		toInduce.setFirstElement(tree);
		toInduce.setSecondElement(examples);
		stack.push(toInduce);

		while(!stack.isEmpty()){

			Couple<ClusterTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> current= stack.pop(); // extract the next element
			ClusterTree currentTree= current.getFirstElement();
			//System.out.println(currentTree.getRoot());
			Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double> currentExamples= current.getSecondElement();
			// set of negative, positive and undefined example
			posExs=currentExamples.getFirst();
			negExs=currentExamples.getSecond();
			undExs=currentExamples.getThird();

			if (posExs.size() <=1) // no exs
				//	if (prPos >= prNeg) { // prior majority of positives
				currentTree.setRoot(null, posExs, null, null); // set positive leaf
			else{
				long currentTime=System.currentTimeMillis(); // time out for making the approach more scalable
				if (Parameters.timeout>0 && currentTime-startingTime>100000)
					currentTree.setRoot(null, posExs, null, null);
				else {
					//Description concept=currentTree.getRoot();
					//if (score(posExs, negExs)>=1)
					//currentTree.setRoot(null, posExs, null, null);
					//else{
						//System.out.println(currentTree.getRoot() instanceof Negation);

						//System.out.println("Concept to be refined:"+currentTree.getRoot());
						ArrayList<Description> generateNewConcepts = new ArrayList<Description>();

						ArrayList<Individual> posExs2= new ArrayList<Individual>(); // from indices to individuals
						ArrayList<Individual> negExs2= new ArrayList<Individual>();
						for (Integer p: posExs)
							posExs2.add(data.kb.getIndividuals()[p]);
						for (Integer p: negExs)
							negExs2.add(data.kb.getIndividuals()[p]);
						//System.out.println(posExs2.size());
						Description root = currentTree.getRoot();
						System.out.println("Current node: "+root+"---"+posExs2.size());
						Description[] cConcepts =	new Description[Parameters.beam];
						if (!Parameters.refinementOperator.equalsIgnoreCase("single")){


							JavaRDD<Description> refine = ((SparkRefinementOperator)op).refine(root, posExs2, negExs2, true, true, true);
							//System.out.println("# refinements: "+refine.count());
							List<Description> collect = refine.collect();
							//generateNewConcepts.addAll(collect); //op instanceof SparkRefinementOperator?

						}
						//	; //: 
						else {
							// genera i concetti sulla base degli esempi
							//
							cConcepts =  op.generateNewConcepts(currentTree.getRoot(),Parameters.beam, posExs, negExs).toArray(cConcepts); //generateNewConcepts.toArray(cConcepts);

						}
						for (Description c:cConcepts) System.out.println(c);

						// select node concept
						Description newRootConcept =  selectConceptWithMinOverlap(cConcepts, posExs) ; //(Parameters.CCP?(selectBestConceptCCP(cConcepts, posExs, negExs, undExs, prPos, prNeg, truePos, trueNeg)):(selectBestConcept(cConcepts, posExs, negExs, undExs, prPos, prNeg));
						System.out.println("Best Concept:"+newRootConcept);
						//System.out.println();
						ArrayList<Integer> posExsT = new ArrayList<Integer>();
						ArrayList<Integer> negExsT = new ArrayList<Integer>();
						ArrayList<Integer> undExsT = new ArrayList<Integer>();
						ArrayList<Integer> posExsF = new ArrayList<Integer>();
						ArrayList<Integer> negExsF = new ArrayList<Integer>();
						ArrayList<Integer> undExsF = new ArrayList<Integer>();

						//					splitInstanceCheck(newRootConcept, posExs, posExsT, negExsT, undExsT);
						//					Integer medoidP = getMedoid(posExsT);
						//					Integer medoidN = getMedoid(negExsT);
						split(newRootConcept, posExs,  posExsT, negExsT);



						// select node concept
						currentTree.setRoot(newRootConcept, posExs, null, null);		
						// build subtrees
						//		undExsT = union(undExsT,);
						ClusterTree posTree= new ClusterTree();
						ClusterTree negTree= new ClusterTree(); // recursive calls simulation
						currentTree.setPosTree(posTree);
						System.out.println("Instances routed to the left branch"+ posExsT.size());
						posTree.setRoot(newRootConcept, posExsT, null, null);
						Negation concept2 = new Negation(newRootConcept);
						//System.out.println("Concept to be refined right branch:"+ concept2);
						negTree.setRoot(concept2, negExsT, null, null);
						System.out.println("Instances routed to the right branch: "+negExsT.size());
						System.out.println();
						currentTree.setNegTree(negTree);



						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla1 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExsT, posExsF, undExsT, dim, 0.0, 0.0);
						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla2 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(negExsT, negExsF, undExsF, dim, 0.0, 0.0);
						Couple<ClusterTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> pos= new Couple<ClusterTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						pos.setFirstElement(posTree);
						pos.setSecondElement(npla1);

						// negative branch
						Couple<ClusterTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> neg= new Couple<ClusterTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						neg.setFirstElement(negTree);
						neg.setSecondElement(npla2);
						stack.push(pos);
						stack.push(neg);


					}
						//			}		
				}
			}
			return tree;

		}


		/**
		 * Implements the heuristics for selecting the most promising concept description
		 * @param cConcepts
		 * @param posExs
		 * @return
		 */
		private Description selectConceptWithMinOverlap(Description[] cConcepts,
				ArrayList<Integer> posExs) {
			// TODO Auto-generated method stub

			Double maxDiff= 0.0d;
			Description bestConcept= cConcepts[0];
			System.out.println(bestConcept);
			int idx=0;

			for (int i =0; i< cConcepts.length;i++){

				ArrayList<Integer> trueExs= new ArrayList<Integer>();
				ArrayList<Integer> falseExs= new ArrayList<Integer>();
				ArrayList<Integer> undExs= new ArrayList<Integer>();
				split(cConcepts[i], posExs, trueExs, falseExs);
				System.out.println(cConcepts[i]+ " ("+posExs.size()+", "+trueExs.size()+","+falseExs.size()+")");
				double score = score(trueExs, falseExs);

				//System.out.println(medoidP+ "-"+medoidN);
				//double simpleEntropyDistance= singlelinkage(trueExs,falseExs);
				//double simpleEntropyDistance = (medoidP ==null) || (medoidN==null)?0:FeaturesDrivenDistance.distance(Parameters.distance,medoidP, medoidN);
				if (score>= maxDiff){
					maxDiff= score;
					bestConcept= cConcepts[i];
					idx= i;

				}




			}

			return cConcepts[idx]; // the concept with the minimum risk of overlap
		}


		private double score(ArrayList<Integer> trueExs, ArrayList<Integer> falseExs) {
			// TODO Auto-generated method stub

			if (Parameters.prototype.equalsIgnoreCase("medoid")) {
				Integer medoidP= getMedoid(trueExs);
				Integer  medoidN= getMedoid(falseExs);
				double distance =  medoidP== null || medoidN==null? 1: FeaturesDrivenDistance.distance(Parameters.distance, medoidP, medoidN);
				return distance;
			}
			return singlelinkage(trueExs,falseExs);
		}


		private double singlelinkage(ArrayList<Integer> trueExs, ArrayList<Integer> falseExs) {
			// TODO Auto-generated method stub
			double minDistance =1.0f;
			for (Integer integer : trueExs) {
				for (Integer i:falseExs){

					double t=FeaturesDrivenDistance.distance(Parameters.distance, integer, i);
					if (t>minDistance)
						minDistance=t;
				}

			}

			return minDistance;
		}


		/**
		 * Return the medoid of a group of individuals
		 * @param trueExs
		 * @return
		 */
		private Integer getMedoid(ArrayList<Integer> trueExs) {
			// TODO Auto-generated method stub

			if (trueExs.isEmpty() )
				return null;
			else{
				Double maxDist= Double.MIN_VALUE; // the maximum value is 1
				Integer currentMedoid= trueExs.get(0); // the first element
				double sumDistance= 0.0f;
				for (Integer integer : trueExs) {

					for (Integer integer2 : trueExs) {
						sumDistance+=FeaturesDrivenDistance.distance(Parameters.distance,integer, integer2);

					}

					if (sumDistance> maxDist){
						currentMedoid = integer;
						maxDist= sumDistance;
					}

				}

				return currentMedoid;
			}


		}





		/**
		 * Split individuals that are subsequently used for medoid computation
		 * @param concept
		 * @param posExs
		 * @param posExsT
		 * @param negExsT
		 * @param undExsT
		 */
		private void splitInstanceCheck(Description concept, ArrayList<Integer> posExs, ArrayList<Integer> posExsT, ArrayList<Integer> negExsT, ArrayList<Integer> undExsT){
			Description negConcept = new Negation(concept);
			for (int e=0; e<posExs.size(); e++) {
				int exIndex = posExs.get(e);
				if (data.kb.getReasoner().hasType(concept,data.kb.getIndividuals()[exIndex]))
					posExsT.add(exIndex);
				else if (data.kb.getReasoner().hasType(negConcept, data.kb.getIndividuals()[exIndex]))
					negExsT.add(exIndex);
				else
					undExsT.add(exIndex);		
			}			
		}

		/**
		 * Split according to the closeness w.r.t. the medoids
		 * @param concept
		 * @param iExs
		 * @param posExs
		 * @param negExs
		 */
		private void split (Description concept, ArrayList<Integer> iExs, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {

			ArrayList<Integer> exs=(ArrayList<Integer>)iExs.clone();
			ArrayList<Integer> posExsT= new ArrayList<Integer>();
			ArrayList<Integer> negExsT=new ArrayList<Integer>();;
			ArrayList<Integer> undExsT=new ArrayList<Integer>();;
			splitInstanceCheck(concept,exs,posExsT,negExsT,undExsT); // split according to instance check

			//System.out.println("Exs:"+ exs.size()+ "  l: "+posExsT.size()+ " r: "+negExsT.size());
			if (posExsT.isEmpty())
				fillSet(posExsT, negExsT,0.6);
			//System.out.println("Exs:"+ exs.size()+ "  l: "+posExsT.size()+ "  r: "+negExsT.size());
			Integer posMedoid= getMedoid(posExsT);
			if (negExsT.isEmpty())
				fillSet(negExsT, posExsT,0.6);
			Integer negMedoid= getMedoid(negExsT);


			for (Integer ind: exs) //split according to the closeness to the medoid
				if (FeaturesDrivenDistance.distance(Parameters.distance, ind, posMedoid) <= FeaturesDrivenDistance.distance(Parameters.distance, ind, negMedoid))
					posExs.add(ind);
				else
					negExs.add(ind);

		}



		/**
		 * Extract a subset of the farthest individuals from the medoid
		 * @param toFill, the subset of individuals that are far from the medoid of the srt of indivduals 
		 * @param exs, the set of individuals for which a medoid is computed
		 * @param d
		 */
		private void fillSet(ArrayList<Integer> toFill, ArrayList<Integer> exs, double d) {
			// TODO Auto-generated method stub
			Integer medoid= getMedoid(exs);
			for (Integer i: exs)
				if (FeaturesDrivenDistance.distance(Parameters.distance, i, medoid)>d){
					toFill.add(i);
				}

			//worst case: all the individuals are close to the medoid
			//solutio: pick a fraction of individuals randomly (the first j individuals in exs)
			if (toFill.isEmpty()){
				int j = (exs.size()/3)+1;
				//System.out.println(exs.size());
				//System.out.println(j);
				for (int i=0;i< j;i++)
					toFill.add(exs.get(i));
			}

			exs.removeAll(toFill);

		}



	}



