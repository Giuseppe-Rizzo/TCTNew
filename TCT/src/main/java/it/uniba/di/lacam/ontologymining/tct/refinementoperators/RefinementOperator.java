package it.uniba.di.lacam.ontologymining.tct.refinementoperators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;




import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Intersection;
import org.dllearner.core.owl.Negation;
import org.dllearner.core.owl.ObjectAllRestriction;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.core.owl.ObjectSomeRestriction;
import org.dllearner.core.owl.Thing;

import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;


//import evaluation.Parameters;

public class RefinementOperator {

	
	KnowledgeBase kb;
	static final double d = 0.5;
	private Description[] allConcepts;
	private ObjectProperty[] allRoles;
	
	public RefinementOperator(KnowledgeBase kb) {
		// TODO Auto-generated constructor stub
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
			
			// case A:  ALC and more expressive ontologies
			do {
				
				     newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)];
				
				if (KnowledgeBase.generator.nextDouble() > 0.5) {
				   Description newConceptBase = getRandomConcept();
					if (KnowledgeBase.generator.nextDouble() >0.5) {
						if (KnowledgeBase.generator.nextDouble() <0.7) { // new role restriction
							ObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
							//					OWLDescription roleRange = (OWLDescription) role.getRange;
							if (KnowledgeBase.generator.nextDouble() < 0.9)
								newConcept = new ObjectAllRestriction(role, newConceptBase);
							else
								newConcept = new ObjectSomeRestriction(role, newConceptBase);
						}
						else					
							newConcept = new Negation(newConceptBase);
					}
				} // else ext
				
			} while ((newConcept instanceof Thing) &&(!((kb.getReasoner().getIndividuals(newConcept).size())>0)));
						
//		}

		return newConcept;				
	}
	
	public ArrayList<Description> generateNewConcepts(Description father, int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {

		System.out.printf("Generating node concepts ");
		ArrayList<Description> rConcepts = new ArrayList<Description>(dim);
		Description newConcept;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection =  true;
				
				Description randomConcept =getRandomConcept();
//				boolean contains = kb.getReasoner().getSuperClasses(randomConcept).contains(father);
				if (!(father instanceof Thing))
				newConcept =  randomConcept; //contains? randomConcept:
				else {if ((kb.getReasoner().getSuperClasses(randomConcept).contains(father))) 	
				newConcept =  randomConcept;
				else
					newConcept=new Intersection(father,randomConcept);
				}
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

	
	public RefinementOperator(){};

}
