package it.uniba.di.lacam.ontologymining.tct.refinementoperators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Negation;
import org.dllearner.core.owl.ObjectAllRestriction;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.core.owl.ObjectSomeRestriction;
import org.semanticweb.owlapi.model.OWLIndividual;

import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.*;



//import evaluation.Parameters;


public class NonRecursiveDownwardRefinementOperator extends RefinementOperator{
	
	
 KnowledgeBase kb;
	static final double d = 0.3;
	private Description[] allConcepts;
	private ObjectProperty[] allRoles;
	
	public NonRecursiveDownwardRefinementOperator(KnowledgeBase kb) {
		
		super();
	this.kb=kb;
	allConcepts=kb.getClasses();
	allRoles=kb.getRoles();
	
	
	}
	
	
	
	/**
	 * Sceglie casualmente un concetto tra quelli generati
	 * @return il concetto scelto
	 */
	public Description getRandomConcept() {
		// sceglie casualmente uno tra i concetti presenti 
		Description newConcept = null;
	
			// case A:  ALC and more expressive ontology
			do {
				
				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)]; // caso base della ricorsione 
				if (KnowledgeBase.generator.nextDouble() < d) {
					Description newConceptBase =   newConcept; //getRandomConcept();  // ricorsione
					if (KnowledgeBase.generator.nextDouble() < d) {
//						
						if (KnowledgeBase.generator.nextDouble() <d) { // new role restriction
							ObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
//							//					OWLDescription roleRange = (OWLDescription) role.getRange;
//
							if (KnowledgeBase.generator.nextDouble() < d)
								newConcept = new ObjectAllRestriction(role, newConceptBase);
							else
								newConcept = new ObjectSomeRestriction(role, newConceptBase);
						}
						else					
							newConcept = new Negation(newConceptBase);
					}
					newConceptBase=newConcept;
				} // else ext
				//				System.out.printf("-->\t %s\n",newConcept);
				//			} while (newConcept==null || !(reasoner.getIndividuals(newConcept,false).size() > 0));
			} while (!(kb.getReasoner().getIndividuals(newConcept).size()>0));
		

		return newConcept;				
	}
	
	public ArrayList<Description> generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {

		System.out.printf("Generating node concepts ");
		ArrayList<Description> rConcepts = new ArrayList<Description>(dim);
		Description newConcept;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				newConcept = getRandomConcept();

				Set<Individual> individuals = (kb.getReasoner()).getIndividuals(newConcept);
				Iterator<Individual> instIterator = individuals.iterator();
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
			} while (emptyIntersection);
			rConcepts.add(newConcept);
			System.out.printf("%d ", c);
		}
		System.out.println();

		return rConcepts;
	}

	


	

}
