package it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.HermiT.model.Individual;
//import org.semanticweb.owl.model.OWLImportsDeclaration;
//import org.semanticweb.owl.util.SimpleURIMapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;



//import evaluation.Parameters;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;


/**
 *  class for implementing the knowledge base
 */
public class KnowledgeBase implements Serializable {
	static final double d = 0.3;
	//private String urlOwlFile = "file:///C:/Users/Giuseppe/Desktop//mod-biopax-example-ecocyc-glycolysis.owl";
	private String urlOwlFile = "file:///C:/Users/Giusepp/Desktop/Ontologie/GeoSkills.owl";
	private  OWLOntology ontology;
	private  OWLOntologyManager manager;
	private  OWLClass[] allConcepts;
	private  OWLObjectProperty[] allRoles;
	private  OWLDataFactory dataFactory;
	public OWLOntologyManager getManager() {
		return manager;
	}


	
	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}

	private  OWLIndividual[] allExamples;
	/* Data property: proprietà, valori e domini*/
	private OWLReasoner reasoner;
	private FISerializable r;
	
	public FISerializable getR() {
		return r;
	}



	public void setR(FISerializable r) {
		this.r = r;
	}

	
	private int[][] classifications;
	public static  Random generator = new Random(1);

	
	public KnowledgeBase(String url) {
		urlOwlFile=url;
		ontology=initKB();

		// object property  Attribut-3AForschungsgruppe



	}

	
	public   OWLOntology initKB() {

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
      
        reasoner = new ReasonerFactory().createReasoner(ontology);
 
        FISerializable fi= new FISerializable(reasoner);
        		//new OWLAPIReasonerSerializable(wrapper);
         //r = new OWLAPIReasoner(wrapper);
        
        //r= 
        System.out.println("\nClasses\n-------");
		Set<OWLClass> classList = ontology.getClassesInSignature();
		allConcepts = new OWLClass[classList.size()];
		int c=0;
		for(OWLClass cls : classList) {
			if (!cls.isOWLNothing() && !cls.isAnonymous()) {
				allConcepts[c++] = cls;
				System.out.println(c +" - "+cls);
			}	        		
		}
		System.out.println("---------------------------- "+c);

		System.out.println("\nProperties\n-------");
		Set<OWLObjectProperty> propList = ontology.getObjectPropertiesInSignature();
		allRoles = new OWLObjectProperty[propList.size()];
		int op=0;
		for(OWLObjectProperty prop : propList) {
			if (!prop.isAnonymous()) {
				allRoles[op++] = prop;
				System.out.println(prop);
			}	        		
		}
		System.out.println("---------------------------- "+op);
		
		
		System.out.println("\nIndividuals\n-----------");
		Set<OWLNamedIndividual> indList = ontology.getIndividualsInSignature();
		allExamples = new OWLIndividual[indList.size()];
		int i=0;
		Set<OWLDataProperty> pes = ontology.getDataPropertiesInSignature();
		for(OWLNamedIndividual ind : indList) {
			allExamples[i++] = ind;  
			Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataPropertyValues = new HashMap<>();
			
			}

      
        

//		reasoner.getKB().realize();
				return this.ontology;	

	}

	

	//********************METODI DI ACCESSO  ALLE COMPONENTI DELL'ONTOLOGIA*******************************//
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getRuoli()
	 */

	public OWLObjectProperty[] getRoles(){
		return allRoles;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getClasses()
	 */

	public OWLClass[] getClasses(){
		return allConcepts;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getIndividui()
	 */
	
	public OWLIndividual[] getIndividuals(){

		return allExamples;
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



	

	public OWLDataFactory getDataFactory() {
		// TODO Auto-generated method sstub
		return dataFactory;
	}



	


	public OWLOntology getOntology(){
		return ontology;
		
	}
	
	
	public void updateExamples(OWLIndividual[] individuals){

		allExamples=individuals;


	}
	
	/**
	 * Random generated concepts
	 * @return il concetto scelto
	 */
	public OWLClassExpression getRandomConcept() {
		// sceglie casualmente uno tra i concetti presenti 
		OWLClassExpression newConcept = null;

		
		//if (!Parameters.BINARYCLASSIFICATION){
			
			// case A:  ALC and more expressive ontologies
			do {
				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)];
				if (KnowledgeBase.generator.nextDouble() < d) {
					OWLClassExpression newConceptBase = getRandomConcept();
					if (KnowledgeBase.generator.nextDouble() < d) {
						
						if (KnowledgeBase.generator.nextDouble() <d) { // new role restriction
							OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
							//					OWLDescription roleRange = (OWLDescription) role.getRange;

							if (KnowledgeBase.generator.nextDouble() < d)
								newConcept = dataFactory.getOWLObjectAllValuesFrom(role, newConceptBase);//(dataFactory.getOWLObjectAllRestriction(role, newConceptBase));
							else
								newConcept = dataFactory.getOWLObjectSomeValuesFrom(role, newConceptBase);
						}
						else					
							newConcept =  dataFactory.getOWLObjectComplementOf(newConceptBase); //dataFactory.getOWLObjectComplementOf(newConceptBase);
					}
				} // else ext
				//				System.out.printf("-->\t %s\n",newConcept);
				//			} while (newConcept==null || !(reasoner.getIndividuals(newConcept,false).size() > 0));
			} while ((reasoner.getInstances(newConcept,false).getFlattened().size()<=0));

		return newConcept;				
	}



	public OWLReasoner getReasoner() {
		// TODO Auto-generated method stub
		return r;
	}



	

}
