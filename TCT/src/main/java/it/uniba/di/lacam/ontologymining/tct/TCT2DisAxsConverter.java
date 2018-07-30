package it.uniba.di.lacam.ontologymining.tct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Intersection;
import org.dllearner.core.owl.Negation;

import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;
import it.uniba.di.lacam.ontologymining.tct.parameters.Parameters;
import it.uniba.di.lacam.ontologymining.tct.utils.Couple;

public class TCT2DisAxsConverter {
	public static KnowledgeBase kb;

	public TCT2DisAxsConverter() {
	}

	public static ArrayList<Description> extractConcepts (Description  fatherDescription, ClusterTree  tree){

		ArrayList<Description> concepts= new ArrayList<Description>();

		ArrayList<ClusterTree> queue = new ArrayList<ClusterTree>();

		queue.add(tree);
		while (!queue.isEmpty()) {

			ClusterTree tree1 =queue.get(0);
			queue.remove(0);


			if ( tree1.getRoot() !=null && (tree1.getPos().root==null && tree1.getNeg().root==null)) 
			{	 Description root = tree1.getRoot();
			//System.out.println(root+" Added" );
			concepts.add(root);

			}else{ 
				Description root = tree1.getRoot();
				//System.out.println("    "+root );
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
		}*/



			return concepts;


		}

		public static ArrayList<Couple<Description,Description>>  extractDisjointnessAxiom(ClusterTree t){
			Description fatherNode= null;
			ArrayList<Description> concepts=  extractConcepts(fatherNode, t);
			int size = Parameters.nOfresults==0?concepts.size():Parameters.nOfresults;
			List<Description> clist = concepts.subList(0, size-1);
			HashSet<Description> conceptset= new HashSet<Description>(clist);
			ArrayList<Couple<Description,Description>>result= new ArrayList<Couple<Description,Description>>();
		

			//System.out.println("starting disjointness extraction: "+ conceptset.size()*conceptset.size());
			
			
			for  (Description c: conceptset){
			//	Description  c= concepts.get(i);	
				Couple<Description,Description> element= new Couple<Description, Description>();

				for (Description  d:conceptset) {
					
					if ((c.toKBSyntaxString().compareTo(d.toKBSyntaxString()))!=0){//)  && !(result.contains(new Couple(c,d)))){ //(!(result.contains(new Couple(d,c))
						//SortedSet<Description> subClasses1 = kb.getReasoner().getSubClasses(c);
						//SortedSet<Description> subClasses2 = kb.getReasoner().getSubClasses(d);
						if ((kb.getReasoner().getIndividuals(new Intersection(c,d)).size()<5)){
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