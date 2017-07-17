package it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler;

import java.io.Serializable;
import java.util.SortedSet;

import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.reasoning.FastInstanceChecker;

public class FISerializable extends FastInstanceChecker implements Serializable {

 
	public FISerializable(KnowledgeSource...k){
		super(k);
	
		
	} 
	 
	public SortedSet<Individual>  getInstances(Description d){
		return getIndividuals(d);
	}

}
