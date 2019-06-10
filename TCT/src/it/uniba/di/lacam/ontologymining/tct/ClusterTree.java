package it.uniba.di.lacam.ontologymining.tct;


import java.util.ArrayList;
import java.util.Stack;


import org.semanticweb.owlapi.model.OWLClassExpression;

public class ClusterTree {

	public ClusterTree getPos() {
		return root.pos;
	}

	public ClusterTree getNeg() {
		return root.neg;
	}

	private class DLNode {

		OWLClassExpression concept;		// node concept
		ArrayList<Integer> cluster;
		Integer posMedoid;
		Integer  negMedoid;
		ClusterTree pos; 			// positive decision subtree

		ClusterTree neg; 			// negative decision subtree

		public DLNode(OWLClassExpression c, ArrayList<Integer>cluster, Integer p, Integer n) {
			concept = c;
			this.cluster=cluster;
			this.posMedoid= p;
			this.negMedoid=n;
			this.pos = this.neg = null; // node has no children
		}

		//		public DLNode() {
		//			concept = null;
		////			this.pos = this.neg = null; // node has no children
		//		}


		public String toString() {
			return this.concept.toString();
		}

	}

	DLNode root;

	public ClusterTree() {

	}

	public ClusterTree (OWLClassExpression c, ArrayList<Integer> inds, Integer p, Integer n) {		
		this.root = new DLNode(c,inds, p, n);
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(OWLClassExpression concept, ArrayList<Integer>cluster, Integer p, Integer  n) {
		this.root = new DLNode(concept, cluster, p, n);
		//		this.root.concept = concept;
	}

	/**
	 * @return the root
	 */
	public OWLClassExpression getRoot() {
		return root.concept;
	}


	public void setPosTree(ClusterTree subTree) {
		this.root.pos = subTree;

	}

	public void setNegTree(ClusterTree subTree) {

		this.root.neg = subTree;

	}


	//public String toString(){

	public String toString() {
 
		String string= "[";
		Stack<ClusterTree> s= new Stack<ClusterTree>();
		s.push(this);
		while (!s.isEmpty()){

			ClusterTree.DLNode b=s.pop().root;
			//s.pop();
			if ((b.concept!=null)) 
				//visito radice
				string += "]";
				else { 
					string+=b.concept+ "[ ";
					if (b.pos != null)
						//memorizzo in stack il sottoalbero destro per la visita successiva
						s.push(b.pos);

					if (b.pos != null)
						// visita sottoalb sinistro
						s.push(b.neg);
				}

			}
		return string;


//			if (root.concept==null)
//				return "{"+root.cluster.size()+"}";
//			if (root.pos == null && root.neg == null)
//				return root.toString();
//			else
//				return root.concept.toString() +"("+root.cluster.size() +") ["+root.pos.toString()+" "+root.neg.toString()+"]";
		}


		//	}

	}
