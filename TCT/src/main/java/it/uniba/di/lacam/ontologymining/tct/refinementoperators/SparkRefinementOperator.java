package it.uniba.di.lacam.ontologymining.tct.refinementoperators;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.storage.StorageLevel;
import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Intersection;
import org.dllearner.core.owl.Negation;
import org.dllearner.core.owl.ObjectAllRestriction;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.core.owl.ObjectSomeRestriction;
//import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;
//import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.*;
//mport it.uniba.di.lacam.ontologymining.tct.utils.SparkConfiguration;
//import it.uniba.di.lacam.ontologymining.tct.utils.SparkConfiguration;
//import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;
//import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;
import scala.Serializable;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
//import evaluation.Parameters;
//import knowledgeBasesHandler.KnowledgeBase;

/**
 * The refinement operator adopted for generating candidates
 * The operator introduces a new concept name or it replaces a subdescription by using
 * either an existential restriction or an universal restriction. For being used in a distributed framework the method should implement the interface scala.Serializable  
 * @author Giuseppe Rizzo
 *
 */

public class SparkRefinementOperator  extends RefinementOperator implements Serializable{

	private static Logger logger= LoggerFactory.getLogger(SparkRefinementOperator.class);

	//private KnowledgeBase kb;
	private static final double d = 0.5;
	private ArrayList<Description> allConcepts;
	private ArrayList<ObjectProperty> allRoles;
	private JavaRDD<Description> rddConcepts;
	private Random generator;
	private AbstractReasonerComponent r;

	//private OWLClassExpression expressio
	//private OWLReasoner r;
	protected OWLDataFactory dataFactory = new OWLDataFactoryImpl();
	Broadcast<OWLDataFactory> broadcastVar;
	//public static OWLParser p= null;//new OWLParser(ExampleKnowledgeBase.k);
	//private JavaSparkContext conf;
	private int beam;

	private Set<Description> refinements;

	private boolean universalRestriction;

	private boolean existentialRestriction;
	private boolean complement;

	//	
	//
	//	

	//	public RefinementOperator(SparkConfiguration conf, KnowledgeBase k) {
	//		p= new OWLParser(k);
	//		this.conf=conf;
	//		generator= new Random(2);
	//	}


	public SparkRefinementOperator (AbstractReasonerComponent k) {
		super();
		// TODO Auto-generated constructor stub
		//p= new OWLParser(k);
	//	System.out.println("P: "+(p==null));
		generator= new Random();
		//r=reasoner;
		//System.out.println("is Reasoner null? "+reasoner==null);
		//kb=k;
		this.beam=100; // set the maximum number of candidates that can be generated
		allConcepts= new ArrayList<Description>();
		//allConcepts.addAll(k.getClasses());
		this.allRoles = new ArrayList<ObjectProperty>();
		//this.allRoles.addAll(roles);
		this.beam= beam;
		ArrayList<Description> allConcepts2 = new ArrayList<Description>();
		for (Description string : k.getAtomicConceptsList()) {
			allConcepts.add(string);

		}
		this.r=k;

		//allConcepts2.addAll(allConcepts.subList(0, 50));
		rddConcepts= SparkConfiguration.sc.parallelize(allConcepts2, 4).persist(StorageLevel.MEMORY_ONLY());
		//System.out.println("RDD Number of partitions: "+rddConcepts.partitions());
		generator= new Random(2);
		// broadcastVar = ExampleKnowledgeBase.sc.broadcast(dataFactory);

		//broadcastVar.value();

	}








	public SortedSet<Description> getAllConcepts() {
		return new TreeSet(allConcepts);
	}


	public void setAllConcepts(SortedSet<Description> allConcepts) {
		this.allConcepts= new ArrayList<Description>();
		this.allConcepts.addAll(allConcepts);
	}


	public SortedSet<ObjectProperty> getAllRoles() {
		return new TreeSet(allRoles);
	}


	public void setAllRoles(SortedSet<ObjectProperty> allRoles) {
		this.allRoles = new ArrayList<ObjectProperty>();
		this.allRoles.addAll(allRoles);
	}


	public JavaRDD<Description> getPallelizedConcept(Description definition, SortedSet<Individual> posExs, SortedSet<Individual> negExs ) {
		double rate = (double)getBeam()/rddConcepts.count();
		double l = rate>1?1:rate;
		//System.out.println("Number of concepts"+l);

		JavaRDD<Description> filter = rddConcepts; //.filter(f->!f.contains("?"));
		JavaRDD<Description> baseCaserefinements = filter.sample (true, l);
		if (rate>1)
			baseCaserefinements= baseCaserefinements.union(filter.sample(true, rate-1));
		//JavaRDD<String> baseCasesrefinementString= baseCaserefinements.map(f->ExampleKnowledgeBase.renderer.render(f));
		//sampling rate
		//rddConcepts.persist();

		String debugString = baseCaserefinements.toDebugString();
		System.out.println(debugString);


		//		JavaRDD<OWLClassExpression> complexRefinement=baseCaserefinements.mapPartitions(new FlatMapFunction<Iterator<OWLClass>, OWLClassExpression>() {
		//			@Override
		//			public Iterator<OWLClassExpression> call(Iterator<OWLClass> i) throws Exception {
		//				System.out.println("---->");
		//				ArrayList<OWLClassExpression> refs= new ArrayList<OWLClassExpression>();
		//				while(i.hasNext()){
		//					OWLClassExpression newConcept= null;
		//					OWLClassExpression newConceptBase=null;
		//					boolean emptyIntersection= false;
		//					newConceptBase=i.next();
		//					System.out.println("xxxx");
		//					do{
		//							// base case
		//						if (generator.nextDouble() < d) {
		//
		//							if (allRoles.size()>0){ // for tackling the absence of roles
		//							if (generator.nextDouble() <d) { // new role restriction
		//									OWLObjectProperty role = allRoles.get(generator.nextInt(allRoles.size()));
		//														//OWLDescription roleRange = (OWLDescription) role.getRange
		//									if (generator.nextDouble() < d)
		//									newConceptBase = dataFactory.getOWLObjectAllValuesFrom(role, newConceptBase);
		//									else
		//										newConceptBase = dataFactory.getOWLObjectSomeValuesFrom(role, newConceptBase);
		//								}
		//								else					
		//									newConceptBase = dataFactory.getOWLObjectComplementOf(newConceptBase);
		//							}
		//							else					
		//								newConceptBase = dataFactory.getOWLObjectComplementOf(newConceptBase);
		//						}
		//
		//						newConcept = dataFactory.getOWLObjectIntersectionOf(definition,newConceptBase);
		//						//			System.out.println(c+"-  New Concept: "+newConcept);
		//						//NodeSet<OWLNamedIndividual> individuals;
		//						//
		//						//individuals = (r.getInstances(newConcept, false));
		//						//Stream<OWLNamedIndividual> instIterator = individuals.entities().parallel();
		//						//emptyIntersection=instIterator.anyMatch(t->!posExs.contains(t)&& !negExs.contains(t));
		//					}while(!(r.isSatisfiable(newConcept)));
		//					refs.add(newConcept);
		//					
		//				}
		//				return refs.iterator();
		//			}});

		JavaRDD<Description> complexRefinement=baseCaserefinements.map(f-> {
			   AbstractReasonerComponent reasoner = r;   
			Description newConcept= definition;
			Description newConceptBase= f;
			//System.out.println("base concept: "+newConceptBase);
			boolean b = false;//true;//!(p.getKb().getReasoner().isSatisfiable(p.parseClassExpression(newConcept)));
			boolean emptyIntersection= false;
			do{
				// base case
				//System.out.println("definition: "+(definition));
				double nextDouble = generator.nextDouble();
				if (nextDouble < 0.3) {
					//	System.out.println("-x--->"+nextDouble);

					//if (allRoles.size()>0){ // for tackling the absence of roles
					//
					//	System.out.println("----->"+nextDouble);
					nextDouble = generator.nextDouble();
					//	System.out.println("----->"+nextDouble);
					//	System.out.println("--- ROLES"+allRoles.size());
					if (nextDouble <0.4) {
						//	System.out.println("vvvvv");// new role restriction
						ObjectProperty role = allRoles.get(generator.nextInt(allRoles.size()));
						//OWLDescription roleRange = (OWLDescription) role.getRange
						nextDouble = generator.nextDouble();
						//System.out.println("----->"+nextDouble);
						if(existentialRestriction){
							if (nextDouble < 0.2){
								//System.out.println("xxxxxx");
								newConceptBase =    new ObjectSomeRestriction(role, newConceptBase); //"("+role +" some "+ newConceptBase+") "; //dataFactory.getOWLObjectAllValuesFrom(role, newConceptBase);
								//	System.out.println("1"+newConceptBase);
							}else if(universalRestriction){
								newConceptBase =   new ObjectAllRestriction(role, newConceptBase);//"(" +role +" all "+ newConceptBase+") "; //dataFactory.getOWLObjectSomeValuesFrom(role, newConceptBase);
								//System.out.println("2"+newConceptBase);
							}
						}
					}else{					
						if (complement){
							newConceptBase =   new Negation(newConceptBase); 
						}
					}
				}

				//if (!newConceptBase.contains("?"))
					newConcept =  new Intersection (definition, newConceptBase);//dataFactory.getOWLObjectIntersectionOf(definition,newConceptBase);
			//	try {

				//	OWLClassExpression parseClassExpression = p.parseClassExpression(newConcept);
					//System.out.println(parseClassExpression);
					//b = !(reasoner.isSatisfiable(newConcept));
					  SortedSet<Individual> individuals = reasoner.getIndividuals(newConcept);
					Stream<Individual> instIterator = individuals.parallelStream();
					emptyIntersection=instIterator.anyMatch(t->!posExs.contains(t)&& !negExs.contains(t));
					b= b & emptyIntersection; // the concept must be both satisfiable and with instances in the training set

					//	System.out.println("check: "+b);
				//} catch (Exception e) {


				//}


			}while(emptyIntersection);
			//				
			return	newConcept;
			//					

			//	
		});

		return complexRefinement; //complexRefinement;
	}



	//	/**
	//	 * Random concept generation
	//	 * @return 
	//	 */
	/*public OWLClassExpression getRandomConcept() {

		OWLClassExpression newConcept = null;

		//System.out.println("*********"+ generator);
		// case A:  ALC and more expressive ontologies
		do {

			//System.out.println("No of classes: "+allConcepts.isEmpty());
			int nextInt = generator.nextInt(allConcepts.size());
			newConcept = allConcepts.get(nextInt); 

			if (generator.nextDouble() < d) {
				OWLClassExpression newConceptBase =   getRandomConcept();

				if (generator.nextDouble() < d) {

					if (allRoles.size()>0){ // for tackling the absence of roles
						if (generator.nextDouble() <d) { // new role restriction
							OWLObjectProperty role = allRoles.get(generator.nextInt(allRoles.size()));
							//					OWLDescription roleRange = (OWLDescription) role.getRange
							if (generator.nextDouble() < d)
								newConcept = dataFactory.getOWLObjectAllValuesFrom(role, newConceptBase);
							else
								newConcept = dataFactory.getOWLObjectSomeValuesFrom(role, newConceptBase);
						}
						else					
							newConcept = dataFactory.getOWLObjectComplementOf(newConceptBase);
					}
					else					
						newConcept = dataFactory.getOWLObjectComplementOf(newConceptBase);
				}
			}

		} while (!(p.getKb().getReasoner().isSatisfiable(newConcept))); //not only a satisfiable concept but also with some instances in the Abox


		//System.out.println("*********");
		return newConcept;				
	}
*/


	// public JavaRDD<OWLClassExpression> generateParallelNewConcepts(OWLClassExpression definition, SortedSet<OWLNamedIndividual> posExs, SortedSet<OWLNamedIndividual> negExs, boolean seed) {

	//		logger.info("Generating node concepts ");
	//		TreeSet<OWLClassExpression> rConcepts = new TreeSet<OWLClassExpression>();
	//		System.out.println("Generating node concepts ");
	//		OWLClassExpression newConcept=null;
	//		boolean emptyIntersection;
	//		for (int c=0; c<beam; c++) {
	//
	//			do {
	//				emptyIntersection =  false;
	//				//System.out.println("Before the try");
	//				//					try{
	//				
	//				newConcept = dataFactory.getOWLObjectIntersectionOf(definition,getRandomConcept());
	//				System.out.println(c+"-  New Concept: "+newConcept);
	//				NodeSet<OWLNamedIndividual> individuals;
	//
	//				individuals = (r.getInstances(newConcept, false));
	//				Stream<OWLNamedIndividual> instIterator = individuals.entities().parallel();
	//				emptyIntersection=instIterator.anyMatch(t->!posExs.contains(t)&& !negExs.contains(t));
	//				
	//				
	//			} while (emptyIntersection);
	//			//if (newConcept !=null){
	//			//System.out.println(newConcept==null);
	//			rConcepts.add(newConcept);
	//			//}
	//
	//		}
	//		System.out.println();


	//		return null;
	//	}




//	public SortedSet<OWLClassExpression>generateNewConcepts(OWLClassExpression definition, SortedSet<OWLNamedIndividual> posExs, SortedSet<OWLNamedIndividual> negExs, boolean seed) {
//
//		logger.info("Generating node concepts ");
//		TreeSet<OWLClassExpression> rConcepts = new TreeSet<OWLClassExpression>();
//		System.out.println("Generating node concepts ");
//		OWLClassExpression newConcept=null;
//		boolean emptyIntersection;
//		for (int c=0; c<beam; c++) {
//
//			do {
//				emptyIntersection =  false;
//				//System.out.println("Before the try");
//				//					try{
//
//				newConcept = dataFactory.getOWLObjectIntersectionOf(definition,getRandomConcept());
//				//System.out.println(c+"-  New Concept: "+newConcept);
//				NodeSet<OWLNamedIndividual> individuals;
//
//				individuals = (p.getKb().getReasoner().getInstances(newConcept, false));
//				Stream<OWLNamedIndividual> instIterator = individuals.getFlattened().parallelStream();
//				emptyIntersection=instIterator.anyMatch(t->!posExs.contains(t)&& !negExs.contains(t));
//
//
//			} while (emptyIntersection);
//			//if (newConcept !=null){
//			//System.out.println(newConcept==null);
//			rConcepts.add(newConcept);
//			//}
//
//		}
//		System.out.println();
//
//		logger.debug(""+rConcepts.size());
//		return rConcepts;
//	}







	//	
	//		public void setReasoner(OWLReasoner reasoner) {
	//			// TODO Auto-generated method stub
	//			this.r= reasoner;
	//			allConcepts=new ArrayList<OWLClass>();
	//
	//		}







	public JavaRDD<Description> refine(Description definition, SortedSet<Individual> instances,
			SortedSet<Individual> neginstances, boolean complement, boolean exRestr,boolean univRestr) {
		//JavaRDD<OWLNamedIndividual> pExsRDD = ExampleKnowledgeBase.sc.p


		this.complement=complement;
		this.existentialRestriction=exRestr;
		this.universalRestriction=univRestr;
		return getPallelizedConcept(definition, instances, neginstances);

		//(generateConcepts(definition,posExs, negExs, false));

	}





	public void setBeam(int i) {
		// TODO Auto-generated method stub
		beam=i;

	}





	public int getBeam() {
		return beam;
	}





}