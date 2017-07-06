package it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler;

import java.io.Serializable;

import org.dllearner.core.KnowledgeSource;
import org.dllearner.reasoning.FastInstanceChecker;

public class FISerializable extends FastInstanceChecker implements Serializable {

 
	public FISerializable(KnowledgeSource...k){
		super(k);
		
	} 

}
