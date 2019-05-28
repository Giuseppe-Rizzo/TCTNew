package it.uniba.di.lacam.ontologymining.tct.distances;


//import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;







/**
 * 
 * @author Nicola Fanizzi
 * 
 */
public class FeaturesDrivenDistance {

	public  static short[][] pi;
	private static double[] featureEntropy;
	private static OWLDataFactory df= new OWLDataFactoryImpl();

	public static void computeFeatureEntropies(OWLReasoner reasoner, OWLClassExpression[] features) {

		int numIndA = reasoner.getRootOntology().getIndividualsInSignature().size();
		featureEntropy = new double[features.length];
		double sum = 0;

		for (int f=0; f<features.length; f++) {

			OWLClassExpression complFeature =  null;

			int numPos = reasoner.getInstances(features[f], false).getFlattened().size();
			int numNeg = reasoner.getInstances(complFeature, false).getFlattened().size();
			int numBoh = numIndA - numPos - numNeg;

			double prPos = (numPos>0 ? (double)numPos/numIndA : Double.MIN_VALUE);
			double prNeg = (numNeg>0 ? (double)numNeg/numIndA : Double.MIN_VALUE);
			double prBoh = (numBoh>0 ? (double)numBoh/numIndA : Double.MIN_VALUE);        	

			featureEntropy[f] = -(prPos * Math.log(prPos) + prNeg * Math.log(prNeg) + prBoh * Math.log(prBoh));
			sum += featureEntropy[f];

		}		

		for (int f=0; f<features.length; f++) 
			featureEntropy[f] = featureEntropy[f]/sum;

	}

	/**
	 * 
	 * @param ind1 first individual index
	 * @param ind2 second individual index
	 * @param dim dimension of the comparison

	 * @return the (semi-)distance measure between the individuals
	 */	
	public static double sqrDistance2(int ind1, int ind2) {
		double acc = 0;
		for (int h=0; h<pi.length; h++) {	
			acc += Math.pow(1-(pi[h][ind1]* pi[h][ind2]), 2); 
		}
		return (double)Math.sqrt(acc)/(2*pi.length);
	} // distance


	/**
	 * 
	 * @param ind1 first individual index
	 * @param ind2 second individual index
	 * @param dim dimension of the comparison

	 * @return the (semi-)distance measure between the individuals
	 */	
	public static double sqrDistance1(int ind1, int ind2) {
		double acc = 0;
		for (int h=0; h<pi.length; h++) {	
			acc += Math.pow(pi[h][ind1] - pi[h][ind2], 2); 
		}
		return (double)Math.sqrt(acc)/(2*pi.length);
	} // distance



	/**
	 * 
	 * @param ind1 index of the 1st individual
	 * @param ind2 index of the 2nd individual
	 * @param dim no dimensions 
	 * @return
	 */
	public static double simpleDistance1(int ind1, int ind2) {
		double acc = 0;

		for (int f=0; f<pi.length; f++) {	
			acc += Math.abs(pi[f][ind1] - pi[f][ind2]); 
		}
		return acc/(2*pi.length); // divisione per 2 perche' doppi in pi
	} // distance


	public static double simpleDistance2(int ind1, int ind2) {
		double acc = 0;

		for (int f=0; f<pi.length; f++) {	
			acc += Math.abs(1-(pi[f][ind1]* pi[f][ind2])); 
		}
		return acc/(2*pi.length); // divisione per 2 perche' doppi in pi
	} // distance


	public static double simpleEntropyDistance(int ind1, int ind2) {
		double acc = 0;

		for (int f=0; f<pi.length; f++) {	
			acc += featureEntropy[f] * Math.abs(pi[f][ind1] - pi[f][ind2]); 
		}
		return acc/(2*pi.length); // divisione per 2 perche' doppi in pi 
	} // distance


	public static double simpleEntropyDistance2(int ind1, int ind2) {
		double acc = 0;

		for (int f=0; f<pi.length; f++) {	
			acc += featureEntropy[f] * (1-Math.abs(pi[f][ind1]* pi[f][ind2])); 
		}
		return acc/(2*pi.length); // divisione per 2 perche' doppi in pi 
	} // distance


	public static double distance(Distances d, int ind1, int ind2){
		if (d== Distances.simpleDistance1)
			return simpleDistance1(ind1,ind2);
		if (d== Distances.simpleDistance2)
			return simpleDistance2(ind1,ind2);

		if (d==Distances.entropicSimpleDistance1)
			return simpleEntropyDistance(ind1,ind2);
		if (d==Distances.entropicSimpleDistance2)
			return simpleEntropyDistance2(ind1,ind2);
		if (d==Distances.sqrtDistance1)
			return sqrDistance1(ind1,ind2);
		//if (d==Distances.sqrtDistance2)
		return sqrDistance2(ind1,ind2);
		//if (s.compareToIgnoreCase("entropicDistance1")==0)
		//return entropicSimpleDistance;
		//if (s.compareToIgnoreCase("entropicDistance2")==0)
		//return entropicSimpleDistance2;

		//return null;
	}


	static void saveProjections(File oFile) {

		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(oFile));
			oos.writeObject(pi);
			oos.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	static void readProjections(File iFile) {

		ObjectInputStream ois;
		try {			
			ois = new ObjectInputStream(new FileInputStream(iFile));
			pi = (short[][]) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();			
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}		
	}

	public static void preLoadPi(String urlOwlFile,
			OWLReasoner reasoner,
			OWLClassExpression[] features, OWLIndividual[] allExamples) {

		String path = "";
		try {
			URI upath = new URI(urlOwlFile);
			path = upath.getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace(); 
		}
		File projFile = new File(path+".dat");
		if (projFile.exists()) {
			System.out.printf("Reading pi elements from file: %s\n",projFile);
			readProjections(projFile);
			System.out.println(pi.length);

		}
		else {
			System.out.printf("Pre-computing %d x %d pi elements \n", features.length, allExamples.length);
			pi = new short[features.length][allExamples.length];

			for (int f=0; f < features.length; ++f) {
				System.out.printf("%4d. %50s", f, features[f]);

				OWLClassExpression negfeature =   df.getOWLObjectComplementOf(features[f]);

				for (int i=0; i < allExamples.length; i++) {
					// case: ind is not an instance of h
					//try {
					if 	(reasoner.isEntailed(df.getOWLClassAssertionAxiom(features[f],allExamples[i]))) 
						pi[f][i] = 0;
					else {
						// case: ind is not an instance of h
						if (reasoner.isEntailed(df.getOWLClassAssertionAxiom(negfeature,allExamples[i])))	
							pi[f][i] = 2;
						else
							// case unknown membership
							pi[f][i] = 1;
					}

				

				//						} catch (OWLReasonerException e) {
				//							e.printStackTrace();
				//						}
				//					System.out.print(".");
			}
			System.out.printf(" | completed. %5.1f%% \n", 100.0*(f+1)*allExamples.length / (features.length*allExamples.length)); 
		}
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		saveProjections(projFile);
		//			System.out.printf("Saved pi elements to file: %s\n",projFile);
	}

}



public static void preLoadPi(OWLReasoner reasoner,
		OWLClassExpression[] features, OWLIndividual[] individuals) {

	System.out.printf("Pre-computing %d x %d pi elements \n", features.length, individuals.length);
	pi = new short[features.length][individuals.length];
	
	for (int f=0; f < features.length; ++f) {
		OWLClassExpression negatedConcept = df.getOWLObjectComplementOf(features[f]);//OWLObjectComplementOfImpl(dataFactory, pool[f]);
		
		

			for (int i=0; i < individuals.length; i++) {
				// case: ind is not an instance of h
					if 	(reasoner.isEntailed(df.getOWLClassAssertionAxiom(features[f],individuals[i]))) 
						pi[f][i] = 0;
					else {
						// case: ind is not an instance of h
						if (reasoner.isEntailed(df.getOWLClassAssertionAxiom(negatedConcept,individuals[i])))	
							pi[f][i] = 2;
						else
							// case unknown membership
							pi[f][i] = 1;
					}
			}
			System.out.printf(" | completed. %5.1f%% \n", 100.0*(f+1)*individuals.length / (features.length*individuals.length));
			
	}
	System.out.println("-----------------------------------------------------------------------------------------------------------");			
}

}	// class


