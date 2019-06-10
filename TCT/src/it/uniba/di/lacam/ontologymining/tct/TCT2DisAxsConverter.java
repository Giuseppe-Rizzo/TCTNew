package it.uniba.di.lacam.ontologymining.tct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClassExpression;

import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;
import it.uniba.di.lacam.ontologymining.tct.parameters.*;
import it.uniba.di.lacam.ontologymining.tct.utils.*;
import it.uniba.di.lacam.ontologymining.tct.*;
import it.uniba.di.lacam.ontologymining.tct.ClusterTree;


public class TCT2DisAxsConverter {
	public static KnowledgeBase kb;

	public TCT2DisAxsConverter() {
	}

	public static ArrayList<OWLClassExpression> extractConcepts (OWLClassExpression  fatherOWLClassExpression, ClusterTree  tree){

		ArrayList<OWLClassExpression> concepts= new ArrayList<OWLClassExpression>();

		ArrayList<ClusterTree> queue = new ArrayList<ClusterTree>();

		queue.add(tree);
		while (!queue.isEmpty()) {

			ClusterTree tree1 =queue.get(0);
			queue.remove(0);


			if ( tree1.getRoot() !=null && (tree1.getPos().root==null && tree1.getNeg().root==null)) 
			{	 OWLClassExpression root = tree1.getRoot();
			System.out.println(root+" Added" );
			concepts.add(root);

			}else{ 
				OWLClassExpression root = tree1.getRoot();
				System.out.println("Added:    "+root );
				concepts.add(root);
				if (tree1.getPos().getRoot()!=null) {
						

				ClusterTree p = tree1.getPos();
				//ClusterTree n= tree1.getNeg();
				queue.add(p);
				}
				//queue.add(n);
				if (tree1.getNeg().getRoot()!=null){
										ClusterTree n = tree1.getNeg();
					//ClusterTree n= tree1.getNeg();
					queue.add(n);
				}
			} 



			}


			System.out.println("number of leaves: "+ concepts.size());

			/*	if (tree.getRoot() ==null){
		System.out.println(fatherOWLClassExpression==null);
			concepts.add(fatherOWLClassExpression);
			return  concepts;
		}
		else {

			OWLClassExpression root = tree.getRoot();
			OWLClassExpression currentOWLClassExpressionLeft= root; //fatherOWLClassExpression !=null? new Intersection(fatherOWLClassExpression, root): root;
			OWLClassExpression currentOWLClassExpressionRight= new Negation(root);
			System.out.println("Current OWLClassExpressions: "+ currentOWLClassExpressionLeft +" and "+ currentOWLClassExpressionRight);
			//System.out.println("tree sx"+ tree.getPos()==null);
			//System.out.println("tree dx"+ tree.get());

			ArrayList<OWLClassExpression> toAdd= new ArrayList<OWLClassExpression>();
			ArrayList<OWLClassExpression> toAdd2= new ArrayList<OWLClassExpression>();
			toAdd.addAll(extractDisjointnessAxiom(currentOWLClassExpressionLeft, tree.getPos()));
			toAdd2.addAll(extractDisjointnessAxiom(currentOWLClassExpressionRight, tree.getNeg()));
			concepts.addAll(toAdd);
			concepts.addAll(toAdd2);
			return concepts;
		}*/



			return concepts;


		}

		public static ArrayList<Couple<OWLClassExpression,OWLClassExpression>>  extractDisjointnessAxiom(ClusterTree t){
			OWLClassExpression fatherNode= null;
			ArrayList<OWLClassExpression> concepts=  extractConcepts(fatherNode, t);
			int size = Parameters.nOfresults==0?concepts.size():Parameters.nOfresults;
			List<OWLClassExpression> clist = concepts.subList(0, size-1);
			HashSet<OWLClassExpression> conceptset= new HashSet<OWLClassExpression>(clist);
			ArrayList<Couple<OWLClassExpression,OWLClassExpression>>result= new ArrayList<Couple<OWLClassExpression,OWLClassExpression>>();
		

			//System.out.println("starting disjointness extraction: "+ conceptset.size()*conceptset.size());
			
			
			for  (OWLClassExpression c: conceptset){
			//	OWLClassExpression  c= concepts.get(i);	
				Couple<OWLClassExpression,OWLClassExpression> element= new Couple<OWLClassExpression, OWLClassExpression>();

				for (OWLClassExpression  d:conceptset) {
					
					if ((c.toString().compareTo(d.toString()))!=0){//)  && !(result.contains(new Couple(c,d)))){ //(!(result.contains(new Couple(d,c))
						//SortedSet<OWLClassExpression> subClasses1 = kb.getReasoner().getSubClasses(c);
						//SortedSet<OWLClassExpression> subClasses2 = kb.getReasoner().getSubClasses(d);
						if ((kb.getReasoner().getInstances(kb.getDataFactory().getOWLObjectIntersectionOf(c,d),false).getFlattened().size()<5)){
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