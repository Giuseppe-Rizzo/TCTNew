package it.uniba.di.lacam.ontologymining.variableassociation;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.util.*;


/**
 * Implements Apriori algorithm for discovering disjointntess axioms (according to Lawryoniwicz's approach)
 * @author Utente
 *
 */
public class Apriori {

	OWLReasoner r;
	OWLClassExpression[] concept;
	OWLClassExpression[] negConcept;
	static OWLDataFactory df = new OWLDataFactoryImpl();
	OWLNamedIndividual[] individual;
	OWLClassExpression[][] axiom; 
	
	public Apriori(OWLReasoner r,  OWLClassExpression[] classes, OWLNamedIndividual[] individuals){
		 this.r= r;
		 this.concept= new  OWLClassExpression[(classes.length*2)]; //classes;
		 OWLClassExpression[] d= new OWLClassExpression[classes.length];
		 for (int i= 0; i< classes.length;i++){
			 concept[i]=classes[i];
		 d[i]=  df.getOWLObjectComplementOf(concept[i]);
		 }
		 for (int i= classes.length; i<(2*classes.length);i++)
			 concept[i]= d[(i-classes.length)];
		 
		 
		 this.individual= individuals;
		 axiom=new OWLClassExpression[classes.length][2];
	}
	
	
	
	
	public ArrayList<HashSet<OWLClassExpression>>  generateCandidate(ArrayList<HashSet<OWLClassExpression>> candidates, int itemsetSize, int length){
		System.out.println("Candidates: "+ candidates);
		if( itemsetSize==1){
			for (OWLClassExpression  c : concept) {
				//System.out.println("Concept"+c);
				final int size = r.getInstances(c,false).getFlattened().size();
				if (size >10){
					 HashSet<OWLClassExpression> arrayList = new HashSet<OWLClassExpression>();
					 arrayList.add(c);
					candidates.add( arrayList);
				}
			}
			 return generateCandidate(candidates, itemsetSize+1, length);
			
		}else{
			//System.out.println("Sono qui: "+ itemsetSize);
			if (itemsetSize<=length){
			ArrayList<HashSet<OWLClassExpression>>  newCandidates= new ArrayList<HashSet<OWLClassExpression>>();
			 // definire passo ricorsivo
			for (HashSet<OWLClassExpression> c: candidates){
				 for (HashSet<OWLClassExpression> d : candidates) { // make the join
					  if (!c.equals(d)){
						    HashSet<OWLClassExpression>  newElem=  new HashSet<OWLClassExpression>(c);
						    newElem.addAll(d);
						    //System.out.println("newElem: "+ newElem);
						     int size= r.getInstances(df.getOWLObjectIntersectionOf(newElem),false).getFlattened().size();
						     if (size<10){
						    	 newCandidates.add(newElem);
						     //System.out.println("After join: "+ newElem);
						     }			  
					  }
				}
				 
			}
			candidates.removeAll(candidates);
			candidates.addAll(newCandidates);
			return generateCandidate(newCandidates, itemsetSize+1, length);	
			}		
			
		}
			
	return candidates;
		
		
	}
}
