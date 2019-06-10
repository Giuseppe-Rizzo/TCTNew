package it.uniba.di.lacam.ontologymining.tct.refinementoperators;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;





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
	private OWLClassExpression[] allConcepts;
	private OWLObjectProperty[] allRoles;
	
	public NonRandomRefinementOperator(KnowledgeBase kb) {

		super();
		this.kb=kb;
		allConcepts=kb.getClasses();
		allRoles=kb.getRoles();
		

	}


	public OWLClassExpression getRandomConcept() {return null;
	}


	/**
	 * Sceglie casualmente un concetto tra quelli generati
	 * @return il concetto scelto
	 */
	public ArrayList<OWLClassExpression> getRandomConcept(int...parameter) {
		// sceglie casualmente uno tra i concetti presenti 
		OWLClassExpression newConcept = null;
		boolean stop=false;
		ArrayList<OWLClassExpression> toRefine;

		// case A:  ALC and more expressive ontology
		do {

			newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)]; // caso base della ricorsione 
			int refinementLength=0;
			final int MAXLENGTH=1;
			toRefine= new ArrayList<OWLClassExpression>();
			//getRandomConcept();  // ricorsione
			toRefine.add(newConcept);
			while (refinementLength<MAXLENGTH){
				ArrayList<OWLClassExpression> refinements= new ArrayList<OWLClassExpression>();
				while(!toRefine.isEmpty()){
					OWLClassExpression newConceptBase =   toRefine.get(0); // first element
					toRefine.remove(0);
					// new role restriction
					OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
					newConcept =kb.getDataFactory().getOWLObjectAllValuesFrom(role, newConceptBase);
					if (kb.getReasoner().getInstances(newConcept, false).getFlattened().size()>0)
						refinements.add(newConcept);
					newConcept = kb.getDataFactory().getOWLObjectSomeValuesFrom(role, newConceptBase);
					if (kb.getReasoner().getInstances(newConcept, false).getFlattened().size()>0)
						refinements.add(newConcept);

					newConcept = kb.getDataFactory().getOWLObjectComplementOf(newConceptBase);
					if (kb.getReasoner().getInstances(newConcept, false).getFlattened().size()>0)
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

	public ArrayList<OWLClassExpression> generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {

		System.out.printf("Generating node concepts ");
		ArrayList<OWLClassExpression> rConcepts = new ArrayList<OWLClassExpression>(dim);
		ArrayList<OWLClassExpression> newConcepts;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				newConcepts = getRandomConcept(1);

				for(OWLClassExpression newConcept: newConcepts){


					Set<OWLNamedIndividual> individuals = (kb.getReasoner()).getInstances(newConcept,false).getFlattened();
					Iterator<OWLNamedIndividual> instIterator = individuals.iterator();
					
					int numIntersections=0;
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

					rConcepts.add(newConcept);

				}
			} while (emptyIntersection);
			System.out.printf("%d ", c);
		}
		System.out.println();

		return rConcepts;
	}






}



