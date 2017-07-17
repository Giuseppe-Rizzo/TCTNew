package it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.owl.Constant;
import org.dllearner.core.owl.DatatypeProperty;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Negation;
import org.dllearner.core.owl.ObjectAllRestriction;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.core.owl.ObjectSomeRestriction;
import org.dllearner.kb.OWLAPIOntology;
import org.dllearner.reasoning.FastInstanceChecker;
import org.dllearner.reasoning.OWLAPIReasoner;
//import org.semanticweb.owl.model.OWLImportsDeclaration;
//import org.semanticweb.owl.util.SimpleURIMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;

//import evaluation.Parameters;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;


/**
 *  class for implementing the knowledge base
 */
public class KnowledgeBase implements Serializable {
	static final double d = 0.3;
	//private String urlOwlFile = "file:///C:/Users/Giuseppe/Desktop//mod-biopax-example-ecocyc-glycolysis.owl";
	private String urlOwlFile = "file:///C:/Users/Giusepp/Desktop/Ontologie/GeoSkills.owl";
	private  OWLAPIOntology ontology;
	private  OWLOntologyManager manager;
	private  NamedClass[] allConcepts;
	private  ObjectProperty[] allRoles;
	private  OWLDataFactory dataFactory;
	public OWLOntologyManager getManager() {
		return manager;
	}


	
	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}

	private  Individual[] allExamples;
	/* Data property: proprietà, valori e domini*/
	private AbstractReasonerComponent reasoner;
	private PelletReasonerSerializable r;
	
	public PelletReasonerSerializable getR() {
		return r;
	}



	public void setR(PelletReasonerSerializable r) {
		this.r = r;
	}

	private  DatatypeProperty[] properties;
	private  Individual[][] domini;
	private int[][] classifications;
	public static  Random generator = new Random(2);;
	private  Random sceltaDataP= new Random(1);
	private  Random sceltaObjectP= new Random(1);
	private Constant[][] dataPropertiesValue;
	public KnowledgeBase(String url) {
		urlOwlFile=url;
		ontology=initKB();

		// object property  Attribut-3AForschungsgruppe



	}

	
	public   OWLAPIOntology initKB() {

		manager = OWLManager.createOWLOntologyManager();        
		 OWLDataFactoryImpl owlDataFactoryImpl = new OWLDataFactoryImpl();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager(owlDataFactoryImpl);
        OWLOntology ontology= null;
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new FileInputStream(urlOwlFile));
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( FileNotFoundException e){
			e.printStackTrace();
		}
        IRI ontologyIRI = manager.getOntologyDocumentIRI(ontology);
        OWLAPIOntology wrapper= new OWLAPIOntology(ontology);
        
        FISerializable fi= new FISerializable(wrapper);
        		//new OWLAPIReasonerSerializable(wrapper);
         //r = new OWLAPIReasoner(wrapper);
        
        //r= 
        
        reasoner =fi;
        try {
			reasoner.init();
			//r.init();
		} catch (ComponentInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

//		reasoner.getKB().realize();
		System.out.println("\nClasses\n-------");
		List<NamedClass> classList = fi.getAtomicConceptsList();
		allConcepts = new NamedClass[classList.size()];
		int c=0;
		for(NamedClass cls : classList) {
//			if (!fi. && !cls.isAnonymous()) {
				allConcepts[c++] = cls;
				System.out.println(c +" - "+cls);
//			}	        		
		}
		System.out.println("---------------------------- "+c);

		System.out.println("\nProperties\n-------");
		List<ObjectProperty> propList = fi.getAtomicRolesList();
		allRoles = new ObjectProperty[propList.size()];
		int op=0;
		for(ObjectProperty prop : propList) {
			
				allRoles[op++] = prop;
				System.out.println(op+"-"+prop);     		
		}
		System.out.println("---------------------------- "+op);

		System.out.println("\nIndividuals\n-----------");
		Set<Individual> indList = fi.getIndividuals();
		allExamples = new Individual[indList.size()];
		int i=0;
		for(Individual ind : indList) {
			
				allExamples[i++] = ind;
				//				System.out.println(ind);
			       		
		}
		System.out.println("---------------------------- "+i);

		System.out.println("\nKB loaded. \n");	
		return this.ontology;	

	}

	

	//********************METODI DI ACCESSO  ALLE COMPONENTI DELL'ONTOLOGIA*******************************//
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getRuoli()
	 */

	public ObjectProperty[] getRoles(){
		return allRoles;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getClasses()
	 */

	public NamedClass[] getClasses(){
		return allConcepts;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getIndividui()
	 */
	
	public Individual[] getIndividuals(){

		return allExamples;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getDataProperties()
	 */
	
	public DatatypeProperty[] getDataProperties(){
		return properties;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getDomini()
	 */

	public Individual[][] getDomains(){
		return domini;
	}
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getDataPropertiesValue()
	 */

	public Constant[][] getDataPropertiesValue(){
		return dataPropertiesValue;

	}
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getURL()
	 */
	public String getURL(){
		return urlOwlFile;
	}





	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getRandomProperty(int)
	 */

//	public int[] getRandomProperty(int numQueryProperty){
//
//		int[] queryProperty= new int[numQueryProperty];
//		int dataTypeProperty=0;
//		while(dataTypeProperty<numQueryProperty ){
//
//			int query=sceltaDataP.nextInt(properties.length);
//			if (domini[query].length>1){
//				queryProperty[dataTypeProperty]=query ;	// creazione delle dataProperty usate per il test
//				dataTypeProperty++;
//
//			}
//
//		}
//		return queryProperty;
//	}
	/* (non-Javadoc)
	 * 
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getRandomRoles(int)
	 */
	
//	public int[] getRandomRoles(int numRegole){
//		int[] regoleTest= new int[numRegole];
//		// 1-genero casualmente un certo numero di regole sulla base delle
//		//quali fare la classificazione
//		for(int i=0;i<numRegole;i++)
//			regoleTest[i]=sceltaObjectP.nextInt(numRegole);
//		return regoleTest;
//
//	}



	public AbstractReasonerComponent getReasoner(){

		return reasoner;
	}

	public OWLDataFactory getDataFactory() {
		// TODO Auto-generated method sstub
		return dataFactory;
	}



	


	public OWLAPIOntology getOntology(){
		return ontology;
		
	}
	
	
	public void updateExamples(Individual[] individuals){

		allExamples=individuals;


	}
	
	/**
	 * Random generated concepts
	 * @return il concetto scelto
	 */
	public Description getRandomConcept() {
		// sceglie casualmente uno tra i concetti presenti 
		Description newConcept = null;

		
		//if (!Parameters.BINARYCLASSIFICATION){
			
			// case A:  ALC and more expressive ontologies
			do {
				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)];
				if (KnowledgeBase.generator.nextDouble() < d) {
					Description newConceptBase = getRandomConcept();
					if (KnowledgeBase.generator.nextDouble() < d) {
						
						if (KnowledgeBase.generator.nextDouble() <d) { // new role restriction
							ObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
							//					OWLDescription roleRange = (OWLDescription) role.getRange;

							if (KnowledgeBase.generator.nextDouble() < d)
								newConcept = new ObjectAllRestriction(role, newConceptBase);//(dataFactory.getOWLObjectAllRestriction(role, newConceptBase));
							else
								newConcept = new ObjectSomeRestriction(role, newConceptBase);
						}
						else					
							newConcept =  new Negation(newConceptBase); //dataFactory.getOWLObjectComplementOf(newConceptBase);
					}
				} // else ext
				//				System.out.printf("-->\t %s\n",newConcept);
				//			} while (newConcept==null || !(reasoner.getIndividuals(newConcept,false).size() > 0));
			} while ((reasoner.getIndividuals(newConcept).size()<=0));
//		}else{
//			// for less expressive ontologies ALE and so on (complemento solo per concetti atomici)
//			do {
//				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)];
//				if (KnowledgeBase.generator.nextDouble() < d) {
//					Description newConceptBase = getRandomConcept();
//					if (KnowledgeBase.generator.nextDouble() < d)
//						if (KnowledgeBase.generator.nextDouble() < 0.1) { // new role restriction
//							ObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
//							//					OWLDescription roleRange = (OWLDescription) role.getRange;
//
//							if (KnowledgeBase.generator.nextDouble() < d)
//								newConcept = new ObjectAllRestriction(role, newConceptBase);
//							else
//								newConcept = new ObjectSomeRestriction(role, newConceptBase);
//						}
//				} // else ext
//				else{ //if (KnowledgeBase.generator.nextDouble() > 0.8) {					
//					newConcept = new Negation(newConcept);
//				}
//				//				System.out.printf("-->\t %s\n",newConcept);
//				//			} while (newConcept==null || !(reasoner.getIndividuals(newConcept,false).size() > 0));
//			} while ((reasoner.getIndividuals(newConcept).size()<=0));
			
			
			
//		}

		return newConcept;				
	}


}
