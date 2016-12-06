
package it.uniba.di.lacam.ontologymining.tct;
import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;
import it.uniba.di.lacam.ontologymining.tct.distances.FeaturesDrivenDistance;
import it.uniba.di.lacam.ontologymining.tct.parameters.Parameters;
import it.uniba.di.lacam.ontologymining.tct.refinementoperators.RefinementOperator;
import it.uniba.di.lacam.ontologymining.tct.refinementoperators.RhoRefinementOperator;
import it.uniba.di.lacam.ontologymining.tct.utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Intersection;
import org.dllearner.core.owl.Negation;
import org.dllearner.core.owl.Nothing;
import org.dllearner.core.owl.Thing;










public class TCTInducer2 {


	KnowledgeBase kb;
	public TCTInducer2(KnowledgeBase k){

		kb=k;
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
		Description concept = new Intersection(new Thing(),  new Thing());
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
				if (currentTime-startingTime>10000)
					currentTree.setRoot(null, posExs, null, null);
				else{
					System.out.println(currentTree.getRoot() instanceof Negation);

					//System.out.println("Concept to be refined:"+currentTree.getRoot());
					ArrayList<Description> generateNewConcepts = null;

					generateNewConcepts=op.generateNewConcepts(currentTree.getRoot(),Parameters.beam, posExs, negExs); // genera i concetti sulla base degli esempi

					Description[] cConcepts= new Description[0];
					//
					cConcepts = generateNewConcepts.toArray(cConcepts);

					// select node concept
					Description newRootConcept =  selectConceptWithMinOverlap(cConcepts, posExs) ; //(Parameters.CCP?(selectBestConceptCCP(cConcepts, posExs, negExs, undExs, prPos, prNeg, truePos, trueNeg)):(selectBestConcept(cConcepts, posExs, negExs, undExs, prPos, prNeg));
					//System.out.println("Best Concept:"+newRootConcept);
					//System.out.println();
					ArrayList<Integer> posExsT = new ArrayList<Integer>();
					ArrayList<Integer> negExsT = new ArrayList<Integer>();
					ArrayList<Integer> undExsT = new ArrayList<Integer>();
					ArrayList<Integer> posExsF = new ArrayList<Integer>();
					ArrayList<Integer> negExsF = new ArrayList<Integer>();
					ArrayList<Integer> undExsF = new ArrayList<Integer>();

					splitInstanceCheck(newRootConcept, posExs, posExsT, negExsT, undExsT);
					Integer medoidP = getMedoid(posExsT);
					Integer medoidN = getMedoid(negExsT);
					split(newRootConcept, posExs,  posExsT, negExsT );


					// select node concept
					currentTree.setRoot(newRootConcept, posExs, medoidP, medoidN);		
					// build subtrees
					//		undExsT = union(undExsT,);
					ClusterTree posTree= new ClusterTree();
					ClusterTree negTree= new ClusterTree(); // recursive calls simulation
					currentTree.setPosTree(posTree);
					posTree.setRoot(newRootConcept, posExs, medoidP, medoidN);
					Negation concept2 = new Negation(newRootConcept);
					//System.out.println("Concept to be refined right branch:"+ concept2);
					negTree.setRoot(concept2, negExs, medoidP, medoidN);
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
					

					//}
				}		}
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
		int idx=0;

		for (int i =0; i< cConcepts.length;i++){

			ArrayList<Integer> trueExs= new ArrayList<Integer>();
			ArrayList<Integer> falseExs= new ArrayList<Integer>();
			ArrayList<Integer> undExs= new ArrayList<Integer>();
			splitInstanceCheck(cConcepts[i], posExs, trueExs, falseExs, undExs);
			Integer medoidP=  trueExs.isEmpty()?getMedoid(posExs): getMedoid(trueExs); // compute the overlap between individuals 
			Integer  medoidN= falseExs.isEmpty()? getMedoid(posExs): getMedoid(falseExs);

			double simpleEntropyDistance = FeaturesDrivenDistance.simpleDistance(medoidP, medoidN);
			if (simpleEntropyDistance>= maxDiff){
				maxDiff= simpleEntropyDistance;
				bestConcept= cConcepts[i];
				idx= i;

			}




		}

		return cConcepts[idx]; // the concept with the minimum risk of overlap
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
			for (Integer integer : trueExs) {
				double sumDistance= 0.0f;
				for (Integer integer2 : trueExs) {
					sumDistance+=FeaturesDrivenDistance.simpleDistance(integer, integer2);

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
			if (kb.getReasoner().hasType(concept,kb.getIndividuals()[exIndex]))
				posExsT.add(exIndex);
			else if (kb.getReasoner().hasType(negConcept, kb.getIndividuals()[exIndex]))
				negExsT.add(exIndex);
			else
				undExsT.add(exIndex);		
		}			
	}


	private void split(Description newRootConcept, ArrayList<Integer> posExs,
			ArrayList<Integer> posExsT,
			ArrayList<Integer> negExsT) {
		// TODO Auto-generated method stub


	}



	public ArrayList<Description> extractDisjointnessAxiom (Description  fatherDescription, ClusterTree  tree){

		ArrayList<Description> concepts= new ArrayList<Description>();
		Description root = tree.getRoot();
		if (root ==null){
			concepts.add(fatherDescription);
			return   concepts;
		}
		else {

			Description currentDescriptionLeft= //fatherDescription !=null? new Intersection(fatherDescription, root): 
					root;
			Description currentDescriptionRight= //fatherDescription !=null? new Intersection( new Negation(fatherDescription),new Negation(root)): 
					new Negation(root);

			ArrayList<Description> toAdd= new ArrayList<Description>();
			ArrayList<Description> toAdd2= new ArrayList<Description>();
			toAdd.addAll(extractDisjointnessAxiom(currentDescriptionLeft, tree.getPos()));
			toAdd2.addAll(extractDisjointnessAxiom(currentDescriptionRight, tree.getNeg()));
			concepts.addAll(toAdd);
			concepts.addAll(toAdd2);
			;			return concepts;
		}

	}

	public ArrayList<Couple<Description,Description>>  extractDisjointnessAxiom(ClusterTree t){
		Description fatherNode= null;
		ArrayList<Description> concepts=  extractDisjointnessAxiom(fatherNode, t);
		ArrayList<Couple<Description,Description>>result= new ArrayList<Couple<Description,Description>>();

		for  (int i=0; i<concepts.size();i++){
			Description  c= concepts.get(i);
			Couple<Description,Description> element= new Couple<Description, Description>();

			for (int j=0; j<concepts.size();j++){
				Description  d= concepts.get(j);
				if (!(c.equals(d))){
					element.setFirstElement(c);
					element.setSecondElement(d);
					result.add(element);
					//System.out.println(c +" disjoint With "+d);
				}

			} 
		}

		return result;
	}

}



