package it.uniba.di.lacam.ontologymining.variableassociation;

import java.util.ArrayList;

import it.uniba.di.lacam.ontologymining.tct.utils.Couple;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;


import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;


/**
 * Using Pearson's coefficient correlation for discovering disjointness axioms
 * @author Utente
 *
 */
public class Correlations {
	
	OWLReasoner r;
	OWLClassExpression[] concept;
	OWLNamedIndividual[] individual;
	OWLClassExpression[][] axiom;
	private static OWLDataFactory df= new OWLDataFactoryImpl();
	
	public Correlations(OWLReasoner r,  OWLClassExpression[] classes, OWLNamedIndividual[] individuals){
		 this.r= r;
		 this.concept= classes;
		 this.individual= individuals;
		 axiom=new OWLClassExpression[classes.length][2];
	} 
	
	
	public ArrayList<Couple<OWLClassExpression,OWLClassExpression>> computeCorrelation(){
		ArrayList<Couple<OWLClassExpression,OWLClassExpression>> axioms= new ArrayList<Couple<OWLClassExpression,OWLClassExpression>>();
		int a=0;
		int co=0;
		for (int i=0; i<concept.length-1;i++) {
			OWLClassExpression c= concept[i];
			for (int j=i+1; j<concept.length;j++){
				OWLClassExpression d= concept[j];
				OWLClassExpression DAndC= df.getOWLObjectIntersectionOf(d,c);
				double DANDCInds= r.getInstances(DAndC, false).getFlattened().size(); // both in c and D
				double DAndNotC= r.getInstances(df.getOWLObjectIntersectionOf(c, df.getOWLObjectComplementOf(d)),false).getFlattened().size();
				double NotDAndC= r.getInstances(df.getOWLObjectIntersectionOf(df.getOWLObjectComplementOf(c), d),false).getFlattened().size();
				double NotDAndNotC= r.getInstances(df.getOWLObjectIntersectionOf(df.getOWLObjectComplementOf(c),df.getOWLObjectComplementOf(d)),false).getFlattened().size();
				
				double  dInds=  r.getInstances(d,false).getFlattened().size();
				double cInds=  r.getInstances(d,false).getFlattened().size();
				double negD= r.getInstances(df.getOWLObjectComplementOf(d),false).getFlattened().size();
				double negC= r.getInstances(df.getOWLObjectComplementOf(c), false).getFlattened().size();
				
				double den= Math.sqrt(negC*negD*dInds*cInds);
				double  num= (DANDCInds*NotDAndNotC)-(DAndNotC*NotDAndC);
				double coeff= num/den;
				co++;
				if (coeff<0.5){ // threshold to express a weak correlation between concepts
					//System.out.println(d+"disjoint with "+ c);
					Couple<OWLClassExpression, OWLClassExpression> e = new Couple<OWLClassExpression, OWLClassExpression>();
					e.setFirstElement(c);
					e.setSecondElement(d);
					axioms.add(e);
					a++;
				}
			}
		}
		
		//System.out.println( "Number of axioms: "+a);
		//System.out.println( "Number of corrlations: "+(co));
		
		return axioms;
	}

}
