package it.uniba.di.lacam.ontologymining.tct.refinementoperators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.storage.StorageLevel;

import org.semanticweb.owlapi.model.OWLClassExpression;
//import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.KnowledgeBase;
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
 * The operator introduces a new concept name or it replaces a subOWLClassExpression by using
 * either an existential restriction or an universal restriction. For being used in a distributed framework the method should implement the interface scala.Serializable  
 * @author Giuseppe Rizzo
 *
 */

public class SparkRefinementOperator  extends RefinementOperator implements Serializable{

	private static Logger logger= LoggerFactory.getLogger(SparkRefinementOperator.class);

	//private KnowledgeBase kb;
	private static final double d = 0.5;
	private ArrayList<OWLClassExpression> allConcepts;
	private ArrayList<OWLObjectProperty> allRoles;
	private JavaRDD<OWLClassExpression> rddConcepts;
	
	private Random generator;
	private OWLReasoner r;
	private KnowledgeBase kb;

	//private OWLClassExpression expressio
	//private OWLReasoner r;
	protected OWLDataFactory dataFactory = new OWLDataFactoryImpl();
	Broadcast<OWLDataFactory> broadcastVar;
	//public static OWLParser p= null;//new OWLParser(ExampleKnowledgeBase.k);
	//private JavaSparkContext conf;
	private int beam;

	private Set<OWLClassExpression> refinements;

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


	public SparkRefinementOperator (KnowledgeBase k) {
		super();
		// TODO Auto-generated constructor stub
		//p= new OWLParser(k);
		//	System.out.println("P: "+(p==null));
		generator= new Random();
		//r=reasoner;

		this.kb=k;
		//System.out.println("is Reasoner null? "+reasoner==null);
		//kb=k;
		this.beam=100; // set the maximum number of candidates that can be generated
		allConcepts= new ArrayList<OWLClassExpression>();
		//allConcepts.addAll(k.getClasses());
		this.allRoles = new ArrayList<OWLObjectProperty>();

		//this.allRoles.addAll(roles);
		this.beam= beam;
		ArrayList<OWLClassExpression> allConcepts2 = new ArrayList<OWLClassExpression>();
		for (OWLClassExpression string : k.getClasses()) {
			System.out.println("--"+string);
			allConcepts.add(string);

		}

		allRoles.addAll(Arrays.asList(k.getRoles()));
		//this.r=k;

		//allConcepts2.addAll(allConcepts.subList(0, 50));
		rddConcepts= SparkConfiguration.sc.parallelize(allConcepts, 4).persist(StorageLevel.MEMORY_ONLY());
		//System.out.println("RDD Number of partitions: "+rddConcepts.count());
		generator= new Random(2);
		// broadcastVar = ExampleKnowledgeBase.sc.broadcast(dataFactory);

		//broadcastVar.value();

	}








	public SortedSet<OWLClassExpression> getAllConcepts() {
		return new TreeSet(allConcepts);
	}


	public void setAllConcepts(SortedSet<OWLClassExpression> allConcepts) {
		this.allConcepts= new ArrayList<OWLClassExpression>();
		this.allConcepts.addAll(allConcepts);
	}


	public SortedSet<OWLObjectProperty> getAllRoles() {
		return new TreeSet(allRoles);
	}


	public void setAllRoles(SortedSet<OWLObjectProperty> allRoles) {
		this.allRoles = new ArrayList<OWLObjectProperty>();
		this.allRoles.addAll(allRoles);
	}


	public JavaRDD<OWLClassExpression> getPallelizedConcept(OWLClassExpression definition, ArrayList<OWLNamedIndividual> posExs, ArrayList<OWLNamedIndividual> negExs ) {
		double rate = (double)getBeam()/rddConcepts.count();
		double l = rate>1?1:rate;
		System.out.println("Current definition "+definition);
		OWLReasoner reasoner = (OWLReasoner)kb.getR();

		JavaRDD<OWLClassExpression> filter = rddConcepts.filter(f->!f.isOWLThing()); //.map(f->{return OWLAPIConverter.getOWLAPIOWLClassExpression(f);});
		JavaRDD<OWLClassExpression> baseCaserefinements = filter.sample (false,l);
		
		baseCaserefinements.foreach(f->System.out.println("Concept: "+f));
		if (rate>1)
			baseCaserefinements= baseCaserefinements.union(filter.sample(true, rate-1));
		//JavaRDD<String> baseCasesrefinementString= baseCaserefinements.map(f->ExampleKnowledgeBase.renderer.render(f));
		//sampling rate
		//rddConcepts.persist();

		//String debugString = baseCaserefinements.toDebugString();
		System.out.println();

		JavaRDD<OWLClassExpression> complexRefinement=baseCaserefinements.map(f ->{
			//public Iterator<OWLClassExpression> call(Iterator<OWLClassExpression> i) throws Exception {
			
			//ArrayList<OWLClassExpression> refs= new ArrayList<OWLClassExpression>();
			//while(i.hasNext()){
			OWLClassExpression newConcept= null;
			OWLClassExpression newConceptBase=f;
			boolean emptyIntersection= true;
			newConceptBase= f; //i.next();

			System.out.println("Potential new conjunct: "+newConceptBase);
			do{
				// base case
				if (generator.nextDouble() > d) {

					if (allRoles.size()>0){ // for tackling the absence of roles
						if (generator.nextDouble() <d) { // new role restriction
							OWLObjectProperty role = allRoles.get(generator.nextInt(allRoles.size()));
							//OWLOWLClassExpression roleRange = (OWLOWLClassExpression) role.getRange
							if (generator.nextDouble() < d)
								newConceptBase = dataFactory.getOWLObjectAllValuesFrom(role, newConceptBase);
							else
								newConceptBase = dataFactory.getOWLObjectSomeValuesFrom(role, newConceptBase);;//new ObjectAllRestriction (role, newConceptBase);
						}
						else					
							newConceptBase =  dataFactory.getOWLObjectComplementOf(newConceptBase);
					}
					else					
						newConceptBase =  dataFactory.getOWLObjectComplementOf(newConceptBase);
				}

				//System.out.println(reasoner.getOWLIndividuals(definition));
				newConcept = newConceptBase;//reasoner.getSubClasses(definition).contains(newConceptBase)? definition:
	//new Intersection(definition,newConceptBase);
				System.out.println("     New Concept: "+newConcept);
				//NodeSet<OWLNamedOWLIndividual> OWLIndividuals;
								try{
									System.out.println("xxxxxxxxxxxx");
					Set<OWLNamedIndividual> OWLIndividuals =reasoner.getInstances(newConcept,false).getFlattened();
					System.out.println("Evaluating  : "+ newConcept +" - "+OWLIndividuals.size());
					if (OWLIndividuals.size()==0)
						emptyIntersection= true;
					else{
						boolean emptyIntersectionP=true;
						for (OWLNamedIndividual OWLIndividual : OWLIndividuals) {

							if (posExs.contains(OWLIndividual))
								emptyIntersectionP= emptyIntersectionP && false;

						}

						boolean emptyIntersectionN=true;
						for (OWLNamedIndividual oWLIndividual : OWLIndividuals) {

							if (posExs.contains(oWLIndividual))
								emptyIntersectionN= emptyIntersectionN && false;

						}

						emptyIntersection= emptyIntersectionP || emptyIntersectionN;
					}
				}catch(Exception e){
					emptyIntersection=true;
				}
						
		}while(!emptyIntersection);
		//refs.add(newConcept);

		//}
		return   newConcept; //refs.iterator();
	});

		//		JavaRDD<OWLClassExpression> complexRefinement=baseCaserefinements.map(f-> {
		//			System.out.println("f"+f);
		//			//FISerializable reasoner = ;
		//			FISerializable fiSerializable = (FISerializable)kb.getReasoner();
		//			SortedSet<OWLIndividual> i=fiSerializable.getOWLIndividuals(f);
		//
		//			OWLClassExpression newConcept= definition;
		//			OWLClassExpression newConceptBase= f;
		//			//System.out.println("to be refined concept: " +definition +"---"+i.size());
		//			boolean b = false;//true;//!(p.getKb().getReasoner().isSatisfiable(p.parseClassExpression(newConcept)));
		//			boolean emptyIntersection= false;
		//			do{
		//				// base case
		//				//System.out.println("definition: "+(definition));
		//				double nextDouble = generator.nextDouble();
		//				if (nextDouble < 0.3) {
		//					//	System.out.println("-x--->"+nextDouble);
		//
		//					//if (allRoles.size()>0){ // for tackling the absence of roles
		//					//
		//					//	System.out.println("----->"+nextDouble);
		//					nextDouble = generator.nextDouble();
		//					//	System.out.println("----->"+nextDouble);
		//					//	System.out.println("--- ROLES"+allRoles.size());
		//					if (nextDouble <0.4) {
		//						//	System.out.println("vvvvv");// new role restriction
		//						ObjectProperty role = allRoles.get(generator.nextInt(allRoles.size()));
		//						//OWLOWLClassExpression roleRange = (OWLOWLClassExpression) role.getRange
		//						nextDouble = generator.nextDouble();
		//						//System.out.println("----->"+nextDouble);
		//						if(existentialRestriction){
		//							if (nextDouble < 0.2){
		//								//System.out.println("xxxxxx");
		//								newConceptBase =    new ObjectSomeRestriction(role, newConceptBase); //"("+role +" some "+ newConceptBase+") "; //dataFactory.getOWLObjectAllValuesFrom(role, newConceptBase);
		//								System.out.println("1"+newConceptBase);
		//							}else if(universalRestriction){
		//								newConceptBase =   new ObjectAllRestriction(role, newConceptBase);//"(" +role +" all "+ newConceptBase+") "; //dataFactory.getOWLObjectSomeValuesFrom(role, newConceptBase);
		//								System.out.println("2"+newConceptBase);
		//							}
		//						}
		//					}else{					
		//						//if (complement){
		//						newConceptBase =   new Negation(newConceptBase); 
		//						//}
		//					}
		//				}
		//
		//
		//				//if (!newConceptBase.contains("?"))
		//				newConcept = newConceptBase;//dataFactory.getOWLObjectIntersectionOf(definition,newConceptBase);
		//
		//				System.out.println(newConceptBase);
		//				try {
		//
		//					//	OWLClassExpression parseClassExpression = p.parseClassExpression(newConcept);
		//					//System.out.println(parseClassExpression);
		//					//b = !(reasoner.isSatisfiable(newConcept));
		//
		//					SortedSet<OWLIndividual> OWLIndividuals = fiSerializable.getInstances(newConceptBase);
		//
		//					// System.out.println("New OWLIndividuals  null? "+(OWLIndividuals==null));
		//					Stream<OWLIndividual> instIterator = OWLIndividuals.parallelStream();
		//					emptyIntersection=instIterator.anyMatch(t->!posExs.contains(t));
		//					//b= b & emptyIntersection; // the concept must be both satisfiable and with instances in the training set
		//
		//
		//					System.out.println("check: "+b);
		//				} catch (Exception e) {
		//					emptyIntersection =false;
		//
		//				}
		//
		//
		//				}while(emptyIntersection);
		//				//			
		//				//ArrayList<OWLClassExpression> arrayList = new ArrayList<OWLClassExpression>();
		//				//arrayList.add(newConcept);
		//				//return	SparkConfiguration.sc.parallelize(arrayList);
		//				//					
		//				return  newConcept; 
		//				//	
		//			});

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
							//					OWLOWLClassExpression roleRange = (OWLOWLClassExpression) role.getRange
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


// public JavaRDD<OWLClassExpression> generateParallelNewConcepts(OWLClassExpression definition, SortedSet<OWLNamedOWLIndividual> posExs, SortedSet<OWLNamedOWLIndividual> negExs, boolean seed) {

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
//				NodeSet<OWLNamedOWLIndividual> OWLIndividuals;
//
//				OWLIndividuals = (r.getInstances(newConcept, false));
//				Stream<OWLNamedOWLIndividual> instIterator = OWLIndividuals.entities().parallel();
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




//	public SortedSet<OWLClassExpression>generateNewConcepts(OWLClassExpression definition, SortedSet<OWLNamedOWLIndividual> posExs, SortedSet<OWLNamedOWLIndividual> negExs, boolean seed) {
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
//				NodeSet<OWLNamedOWLIndividual> OWLIndividuals;
//
//				OWLIndividuals = (p.getKb().getReasoner().getInstances(newConcept, false));
//				Stream<OWLNamedOWLIndividual> instIterator = OWLIndividuals.getFlattened().parallelStream();
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







public JavaRDD<OWLClassExpression> refine(OWLClassExpression definition, ArrayList<OWLNamedIndividual> instances,
		ArrayList<OWLNamedIndividual> neginstances, boolean complement, boolean exRestr,boolean univRestr) {
	//JavaRDD<OWLNamedOWLIndividual> pExsRDD = ExampleKnowledgeBase.sc.p

	
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