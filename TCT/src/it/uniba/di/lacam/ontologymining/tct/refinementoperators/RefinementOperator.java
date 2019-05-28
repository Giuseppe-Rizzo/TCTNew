package it.uniba.di.lacam.ontologymining.tct.refinementoperators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;





import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;


//import evaluation.Parameters;

public class RefinementOperator {


	KnowledgeBase kb;
	static final double d = 0.5;
	private OWLClass[] allConcepts;
	private OWLObjectProperty[] allRoles;

	public RefinementOperator(KnowledgeBase kb) {
		// TODO Auto-generated constructor stub
		this.kb=kb;
		allConcepts=kb.getClasses();
		//	/System.out.println("All classes:"+ allConcepts);

		allRoles=kb.getRoles();


	}



	/**
	 * Sceglie casualmente un concetto tra quelli generati
	 * @return il concetto scelto
	 */
	public OWLClassExpression getRandomConcept() {
		// sceglie casualmente uno tra i concetti presenti 
		OWLClassExpression newConcept = null;


		do {
			newConcept = kb.getClasses()[KnowledgeBase.generator.nextInt(kb.getClasses().length)];
			if (KnowledgeBase.generator.nextDouble() < d) {
				OWLClassExpression newConceptBase = getRandomConcept();
				if (KnowledgeBase.generator.nextDouble() < d) {

					if (KnowledgeBase.generator.nextDouble() <d) { // new role restriction
						OWLObjectProperty role = kb.getRoles()[KnowledgeBase.generator.nextInt(kb.getRoles().length)];
						//					OWLDescription roleRange = (OWLDescription) role.getRange;

						if (KnowledgeBase.generator.nextDouble() < d)
							newConcept = kb.getDataFactory().getOWLObjectAllValuesFrom(role, newConceptBase);//(dataFactory.getOWLObjectAllRestriction(role, newConceptBase));
						else
							newConcept = kb.getDataFactory().getOWLObjectSomeValuesFrom(role, newConceptBase);
					}
					else					
						newConcept =  kb.getDataFactory().getOWLObjectComplementOf(newConceptBase); //dataFactory.getOWLObjectComplementOf(newConceptBase);
				}
			} // else ext
			//				System.out.printf("-->\t %s\n",newConcept);
			//			} while (newConcept==null || !(reasoner.getIndividuals(newConcept,false).size() > 0));
		} while ((kb.getReasoner().getInstances(newConcept,false).getFlattened().size()<=0));

		return newConcept;		
		//						
		////		}
				
	}

	public ArrayList<OWLClassExpression> generateNewConcepts(OWLClassExpression father, int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs, List<OWLClassExpression> candidates) {


		System.out.printf("Generating node concepts ");
		ArrayList<OWLClassExpression> rConcepts = new ArrayList<OWLClassExpression>(dim);
         OWLClassExpression newConcept;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				newConcept = getRandomConcept();

				//for(OWLClassExpression newConcept: newConcepts){


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

				//}
			} while (emptyIntersection);
			System.out.printf("%d ", c);
		}
		System.out.println();

		return rConcepts;

	}


	public RefinementOperator(){};

}
