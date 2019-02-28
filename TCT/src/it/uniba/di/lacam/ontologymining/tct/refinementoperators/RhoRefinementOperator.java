package it.uniba.di.lacam.ontologymining.tct.refinementoperators;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.refinementoperators.PsiDown;
import org.dllearner.refinementoperators.RhoDRDown;
import org.dllearner.refinementoperators.RhoDown; 
import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.*;


public class RhoRefinementOperator extends RefinementOperator {
	private RhoDRDown op;
	private KnowledgeBase kb;

	public RhoRefinementOperator(KnowledgeBase kb) {
		super();
		 this.kb=kb;
		 RhoDRDown rhodr = new RhoDRDown();
		 AbstractReasonerComponent reasoner = kb.getReasoner();
		rhodr.setReasoner(reasoner);
		 rhodr.setSubHierarchy(reasoner.getClassHierarchy());
         rhodr.setObjectPropertyHierarchy(reasoner.getObjectPropertyHierarchy());
         rhodr.setDataPropertyHierarchy(reasoner.getDatatypePropertyHierarchy());
         
         rhodr.setApplyAllFilter(false);
         rhodr.setUseAllConstructor(true);
         rhodr.setUseExistsConstructor(true);
         rhodr.setUseHasValueConstructor(false);
         rhodr.setUseCardinalityRestrictions(false);
         rhodr.setUseNegation(true);
         rhodr.setUseBooleanDatatypes(false);
         rhodr.setUseDoubleDatatypes(false);
         rhodr.setUseStringDatatypes(false);
        
         
         try {
        	 op=rhodr;
			op.init();
		} catch (ComponentInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public ArrayList<Description> generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs, Description...con) {

		System.out.printf("Generating node concepts "+ con[0]);
		
	
		Set<Description> refine = op.refine(con[0], 1);// generate refinement
		System.out.println(refine.size());
		
		ArrayList<Description> rConcepts = new ArrayList<Description>();
		Description newConcept;
		boolean emptyIntersection;
		for (Description d:refine) {
//			do {
			
				emptyIntersection = false; // true
				newConcept= d;
				Set<Individual> individuals = (op.getReasoner()).getIndividuals(newConcept);
				Iterator<Individual> instIterator = individuals.iterator();
				while (emptyIntersection && instIterator.hasNext()) {
					Individual nextInd = (Individual) instIterator.next();
					int index = -1;
					 final Individual[] individuals2 = kb.getIndividuals();
					for (int i=0; index<0 && i<individuals2.length; ++i)
						if (nextInd.equals(individuals2[i])) index = i;
					if (posExs.contains(index))
						emptyIntersection = false;
					else if (negExs.contains(index))
						emptyIntersection = false;
				}					
//			} while 
				if (!emptyIntersection);
						rConcepts.add(newConcept);
//			System.out.printf("%d ", newConcept);
		}
		System.out.println();

		return rConcepts;
	}


}
