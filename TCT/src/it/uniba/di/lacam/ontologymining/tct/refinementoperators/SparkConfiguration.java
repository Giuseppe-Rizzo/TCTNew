package it.uniba.di.lacam.ontologymining.tct.refinementoperators;

import java.io.Serializable;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.OWLReasoner;



import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.FISerializable;

//import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.OWLAPIReasonerSerializable;
//import it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler.PelletReasonerSerializable;

public class SparkConfiguration implements Serializable{
	
	public static SparkConf conf;
	public  static JavaSparkContext sc;
	public Broadcast<OWLReasoner> r;
	
	
	
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
		Class[] x = {FISerializable.class,OWLDataFactory.class,OWLClassExpression.class,OWLObjectProperty.class,OWLIndividual.class,OWLNamedIndividual.class};
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