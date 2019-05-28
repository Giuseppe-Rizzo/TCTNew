package it.uniba.di.lacam.ontologymining.tct.refinementoperators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;


import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.*;



//import evaluation.Parameters;


public class NonRecursiveDownwardRefinementOperator extends RefinementOperator{
	
	
 KnowledgeBase kb;
	static final double d = 0.3;
	private OWLClassExpression[] allConcepts;
	private OWLObjectProperty[] allRoles;
	
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
	public OWLClassExpression getRandomConcept() {
		// sceglie casualmente uno tra i concetti presenti 
		OWLClassExpression newConcept = null;
	
			// case A:  ALC and more expressive ontology
			do {
				
				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)]; // caso base della ricorsione 
				if (KnowledgeBase.generator.nextDouble() < d) {
					OWLClassExpression newConceptBase =   newConcept; //getRandomConcept();  // ricorsione
					if (KnowledgeBase.generator.nextDouble() < d) {
//						
						if (KnowledgeBase.generator.nextDouble() <d) { // new role restriction
							OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
//							//					OWLOWLClassExpression roleRange = (OWLOWLClassExpression) role.getRange;
//
							if (KnowledgeBase.generator.nextDouble() < d)
								newConcept = kb.getDataFactory().getOWLObjectAllValuesFrom(role, newConceptBase);
							else
								newConcept = kb.getDataFactory().getOWLObjectSomeValuesFrom(role, newConceptBase);
						}
						else					
							newConcept =kb.getDataFactory().getOWLObjectComplementOf(newConceptBase);
					}
					newConceptBase=newConcept;
				} // else ext
				//				System.out.printf("-->\t %s\n",newConcept);
				//			} while (newConcept==null || !(reasoner.getOWLIndividuals(newConcept,false).size() > 0));
			} while (!(kb.getReasoner().getInstances(newConcept,false).getFlattened().size()>0));
		

		return newConcept;				
	}
	
	public ArrayList<OWLClassExpression> generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {

		System.out.printf("Generating node concepts ");
		ArrayList<OWLClassExpression> rConcepts = new ArrayList<OWLClassExpression>(dim);
		OWLClassExpression newConcept;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				newConcept = getRandomConcept();

				Set<OWLNamedIndividual> owlndividuals = (kb.getReasoner()).getInstances(newConcept,false).getFlattened();
				Iterator<OWLNamedIndividual> instIterator = owlndividuals.iterator();
				while (emptyIntersection && instIterator.hasNext()) {
					OWLIndividual nextInd = (OWLIndividual) instIterator.next();
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
