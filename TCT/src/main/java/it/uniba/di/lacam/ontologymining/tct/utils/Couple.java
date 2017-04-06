package it.uniba.di.lacam.ontologymining.tct.utils;

import org.dllearner.core.owl.Description;

public class Couple<S,T> {
	 private S firstElement;
	 private T secondElement;
	
	public  Couple(){
		
		
	}

	public Couple(S c, T d) {
		// TODO Auto-generated constructor stub
	this.firstElement=c;
	this.secondElement=d;
	}

	public S getFirstElement() {
		return firstElement;
	}

	public void setFirstElement(S firstElement) {
		this.firstElement = firstElement;
	}

	public T getSecondElement() {
		return secondElement;
	}

	public void setSecondElement(T secondElement) {
		this.secondElement = secondElement;
	}
	
	public String toString(){
		
		return "<"+firstElement.toString()+", "+secondElement+">";
	}

}
