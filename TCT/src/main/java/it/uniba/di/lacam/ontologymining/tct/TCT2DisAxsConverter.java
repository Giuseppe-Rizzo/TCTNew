package it.uniba.di.lacam.ontologymining.tct;

import java.util.ArrayList;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Intersection;
import org.dllearner.core.owl.Negation;

import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;
import it.uniba.di.lacam.ontologymining.tct.utils.Couple;

public class TCT2DisAxsConverter {
	public static KnowledgeBase kb;

	public TCT2DisAxsConverter() {
	}
	
	public static ArrayList<Description> extractDisjointnessAxiom (Description  fatherDescription, ClusterTree  tree){

		ArrayList<Description> concepts= new ArrayList<Description>();
		
		
			
		
		if (tree.getRoot() ==null){
		System.out.println(fatherDescription==null);
			concepts.add(fatherDescription);
			return  concepts;
		}
		else {

			Description root = tree.getRoot();
			Description currentDescriptionLeft= root; //fatherDescription !=null? new Intersection(fatherDescription, root): root;
			Description currentDescriptionRight= new Negation(root);
			System.out.println("Current Descriptions: "+ currentDescriptionLeft +" and "+ currentDescriptionRight);
			//System.out.println("tree sx"+ tree.getPos()==null);
			//System.out.println("tree dx"+ tree.get());
			
			ArrayList<Description> toAdd= new ArrayList<Description>();
			ArrayList<Description> toAdd2= new ArrayList<Description>();
			toAdd.addAll(extractDisjointnessAxiom(currentDescriptionLeft, tree.getPos()));
			toAdd2.addAll(extractDisjointnessAxiom(currentDescriptionRight, tree.getNeg()));
			concepts.addAll(toAdd);
			concepts.addAll(toAdd2);
			return concepts;
		}
		


	}

	public static ArrayList<Couple<Description,Description>>  extractDisjointnessAxiom(ClusterTree t){
		Description fatherNode= null;
		ArrayList<Description> concepts=  extractDisjointnessAxiom(fatherNode, t);
		ArrayList<Couple<Description,Description>>result= new ArrayList<Couple<Description,Description>>();

		for  (int i=0; i<concepts.size();i++){
			Description  c= concepts.get(i);
			Couple<Description,Description> element= new Couple<Description, Description>();

			for (int j=i; j<concepts.size();j++){
				Description  d= concepts.get(j);
				if (!(c.toKBSyntaxString().equals(d.toKBSyntaxString()))){//)  && !(result.contains(new Couple(c,d)))){ //(!(result.contains(new Couple(d,c))
					//SortedSet<Description> subClasses1 = kb.getReasoner().getSubClasses(c);
					//SortedSet<Description> subClasses2 = kb.getReasoner().getSubClasses(d);
					if ((kb.getReasoner().getIndividuals(new Intersection(c,d)).size()<10)){
						element.setFirstElement(c);
						element.setSecondElement(d);
						result.add(element);
					}
					//System.out.println(c +" disjoint With "+d);
				}

			} 
		}

		return result;
	}

}