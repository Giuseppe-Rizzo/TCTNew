package it.uniba.di.lacam.ontologymining.tct.refinementoperators;

import java.io.Serializable;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.ObjectProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;

import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.OWLAPIReasonerSerializable;
import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.PelletReasonerSerializable;

public class SparkConfiguration implements Serializable{
	
	public static SparkConf conf;
	public  static JavaSparkContext sc;
	public Broadcast<AbstractReasonerComponent> r;
	
	
	
	//public Broadcast<OWLReasoner> getR() {
//		return r;
//	}


	//public void setR(Broadcast<OWLReasoner> r) {
	//	this.r = r;
	//}


	public SparkConfiguration(){
		System.setProperty("hadoop.home.dir", "C:/Users/Utente/Downloads/");
		conf = new SparkConf().setMaster("local[3]").setAppName("ExampleKnowledgeBase");
		conf.set("spark.executor.memory", "1g");
		//conf.set("spark.driver.memory", "1g");
		conf.set("spark.driver.maxResultSize", "1g");
		conf.set("spark.rdd.compress", "true");
		conf.set( "spark.serializer", "org.apache.spark.serializer.KryoSerializer" );
		Class[] x = {OWLAPIReasonerSerializable.class,OWLDataFactory.class,org.dllearner.reasoning.FastInstanceChecker.class,AbstractReasonerComponent.class,Description.class,ObjectProperty.class,Individual.class};
		conf.registerKryoClasses(x);
		conf.set( "spark.serializer.buffer", "1GB" );
		sc = new JavaSparkContext(conf);
		sc.setLogLevel("OFF");
		

		
		
	}


	public SparkConf getConf() {
		return conf;
	}


	public void setConf(SparkConf conf) {
		this.conf = conf;
	}


	public JavaSparkContext getSc() {
		return sc;
	}


	public void setSc(JavaSparkContext sc) {
		this.sc = sc;
	}


//	public OWLObjectRenderer getRenderer() {
//		return renderer;
//	}


}