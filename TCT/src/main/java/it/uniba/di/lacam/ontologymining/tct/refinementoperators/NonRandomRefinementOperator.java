package it.uniba.di.lacam.ontologymining.tct.refinementoperators;



import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Negation;
import org.dllearner.core.owl.ObjectAllRestriction;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.core.owl.ObjectSomeRestriction;
import org.semanticweb.owlapi.model.OWLException;





/**
 * A new refinement operator without random choice
 * @author Utente
 *
 */
public class NonRandomRefinementOperator extends RefinementOperator {

	public NonRandomRefinementOperator() {
		// TODO Auto-generated constructor stub
	}





	KnowledgeBase kb;
	static final double d = 0.3;
	private Description[] allConcepts;
	private ObjectProperty[] allRoles;
	
	public NonRandomRefinementOperator(KnowledgeBase kb) {

		super();
		this.kb=kb;
		allConcepts=kb.getClasses();
		allRoles=kb.getRoles();
		

	}


	public Description getRandomConcept() {

		return null;
	}


	/**
	 * Sceglie casualmente un concetto tra quelli generati
	 * @return il concetto scelto
	 */
	public ArrayList<Description> getRandomConcept(int...parameter) {
		// sceglie casualmente uno tra i concetti presenti 
		Description newConcept = null;
		boolean stop=false;
		ArrayList<Description> toRefine;

		// case A:  ALC and more expressive ontology
		do {

			newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)]; // caso base della ricorsione 
			int refinementLength=0;
			final int MAXLENGTH=1;
			toRefine= new ArrayList<Description>();
			//getRandomConcept();  // ricorsione
			toRefine.add(newConcept);
			while (refinementLength<MAXLENGTH){
				ArrayList<Description> refinements= new ArrayList<Description>();
				while(!toRefine.isEmpty()){
					Description newConceptBase =   toRefine.get(0); // first element
					toRefine.remove(0);
					// new role restriction
					ObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
					newConcept = new ObjectAllRestriction(role, newConceptBase);
					if (kb.getReasoner(). getIndividuals(newConcept).size()>0)
						refinements.add(newConcept);
					newConcept = new ObjectSomeRestriction(role, newConceptBase);
					if (kb.getReasoner(). getIndividuals(newConcept).size()>0)
						refinements.add(newConcept);

					newConcept = new Negation(newConceptBase);
					if (kb.getReasoner(). getIndividuals(newConcept).size()>0)
						refinements.add(newConcept);
				}
				toRefine.addAll(refinements); // add all the refinements generated from the inner loop
				refinementLength++;

				//				newConceptBase=newConcept;

			}


			if (!toRefine.isEmpty()){ // check satisifiability of all possible refinements
				stop= true;
			}

		} while (!stop); // come stopparlo?


		return toRefine;				
	}

	public ArrayList<Description> generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {

		System.out.printf("Generating node concepts ");
		ArrayList<Description> rConcepts = new ArrayList<Description>(dim);
		ArrayList<Description> newConcepts;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				newConcepts = getRandomConcept(1);

				for(Description newConcept: newConcepts){


					Set<Individual> individuals = (kb.getReasoner()).getIndividuals(newConcept);
					Iterator<Individual> instIterator = individuals.iterator();
					
					int numIntersections=0;
					while (emptyIntersection && instIterator.hasNext()) {
						Individual nextInd = (Individual) instIterator.next();
						int index = -1;
						for (int i=0; index<0 && i<kb.getIndividuals().length; ++i)
							if (nextInd.equals(kb.getIndividuals()[i])) index = i;
						
						if (posExs.contains(index))
							emptyIntersection = false;
						else if (negExs.contains(index))
							emptyIntersection = false;
					}					

					rConcepts.add(newConcept);

				}
			} while (emptyIntersection);
			System.out.printf("%d ", c);
		}
		System.out.println();

		return rConcepts;
	}






}



