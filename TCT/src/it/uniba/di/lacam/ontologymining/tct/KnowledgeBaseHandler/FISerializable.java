package it.uniba.di.lacam.ontologymining.tct.KnowledgeBaseHandler;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.impl.OWLReasonerBase;
import org.semanticweb.owlapi.util.Version;

/**
 * A serialized version of an OWLReasoner
 * @author Giuseppe
 *
 */
public class FISerializable extends Object implements OWLReasoner,Serializable{
    private OWLReasoner reasoner;
 
	public FISerializable(OWLReasoner r) {
		reasoner=r;
	}
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3792022335906407316L;

	
	 
//	public SortedSet<OWLIndividual>  getInstances(OWLClassExpression d){
//		return getIndividuals(d);
//	}



	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	reasoner.dispose();
		
		
	}



	@Override
	public void flush() {
		// TODO Auto-generated method stub
		reasoner.flush();
		
	}



	@Override
	public Node<OWLClass> getBottomClassNode() {
		// TODO Auto-generated method stub
		return reasoner.getBottomClassNode();
	}



	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		// TODO Auto-generated method stub
		return reasoner.getBottomDataPropertyNode();
	}



	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		// TODO Auto-generated method stub
		return  reasoner.getBottomObjectPropertyNode();
	}



	@Override
	public BufferingMode getBufferingMode() {
		// TODO Auto-generated method stub
		return reasoner.getBufferingMode();
	}



	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getDataPropertyDomains(arg0, arg1);
	}



	@Override
	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual arg0, OWLDataProperty arg1)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getDataPropertyValues(arg0, arg1);
		
	}



	@Override
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual arg0)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getDifferentIndividuals(arg0);
	}



	@Override
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression arg0) throws ReasonerInterruptedException,
			TimeOutException, FreshEntitiesException, InconsistentOntologyException {
		// TODO Auto-generated method stub
		return reasoner.getDisjointClasses(arg0);
	}



	@Override
	public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getDisjointDataProperties(arg0);
	}



	@Override
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getDisjointObjectProperties(arg0);
	}



	@Override
	public Node<OWLClass> getEquivalentClasses(OWLClassExpression arg0)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getEquivalentClasses(arg0);
	}



	@Override
	public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getEquivalentDataProperties(arg0);
	}



	@Override
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getEquivalentObjectProperties(arg0);
	}



	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		// TODO Auto-generated method stub
		return reasoner.getFreshEntityPolicy();
	}



	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		// TODO Auto-generated method stub
		return reasoner.getIndividualNodeSetPolicy();
	}



	@Override
	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression arg0, boolean arg1)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return  reasoner.getInstances(arg0, arg1);
	}



	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getInverseObjectProperties(arg0);
	}



	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getObjectPropertyDomains(arg0, arg1);
	}



	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getObjectPropertyRanges(arg0, arg1);
	}



	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual arg0,
			OWLObjectPropertyExpression arg1) throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getObjectPropertyValues(arg0, arg1);
	}



	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		// TODO Auto-generated method stub
		return reasoner.getPendingAxiomAdditions();
	}



	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		// TODO Auto-generated method stub
		return reasoner.getPendingAxiomRemovals();
	}



	@Override
	public List<OWLOntologyChange> getPendingChanges() {
		// TODO Auto-generated method stub
		return reasoner.getPendingChanges();
	}



	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		// TODO Auto-generated method stub
		return reasoner.getPrecomputableInferenceTypes();
	}



	@Override
	public String getReasonerName() {
		// TODO Auto-generated method stub
		return reasoner.getReasonerName();
	}



	@Override
	public Version getReasonerVersion() {
		// TODO Auto-generated method stub
		return reasoner.getReasonerVersion();
	}



	@Override
	public OWLOntology getRootOntology() {
		// TODO Auto-generated method stub
		return reasoner.getRootOntology();
	}



	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getSameIndividuals(arg0);
	}



	@Override
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression arg0, boolean arg1)
			throws ReasonerInterruptedException, TimeOutException, FreshEntitiesException,
			InconsistentOntologyException, ClassExpressionNotInProfileException {
		// TODO Auto-generated method stub
		return reasoner.getSubClasses(arg0, arg1);
	}



	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getSubDataProperties(arg0, arg1);
	}



	@Override
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getSubObjectProperties(arg0, arg1);
	}



	@Override
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression arg0, boolean arg1)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getSubClasses(arg0, arg1);
	}



	@Override
	public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getSubDataProperties(arg0, arg1);
	}



	@Override
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.getSubObjectProperties(arg0, arg1);
	}



	@Override
	public long getTimeOut() {
		// TODO Auto-generated method stub
		return reasoner.getTimeOut();
	}



	@Override
	public Node<OWLClass> getTopClassNode() {
		// TODO Auto-generated method stub
		return reasoner.getTopClassNode();
	}



	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		// TODO Auto-generated method stub
		return reasoner.getTopDataPropertyNode();
	}



	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		// TODO Auto-generated method stub
		return reasoner.getTopObjectPropertyNode();
	}



	@Override
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual arg0, boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return  reasoner.getTypes(arg0, arg1);
	}



	@Override
	public Node<OWLClass> getUnsatisfiableClasses()
			throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException {
		// TODO Auto-generated method stub
		return reasoner.getUnsatisfiableClasses();
	}



	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		 reasoner.interrupt();
	}



	@Override
	public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return reasoner.isConsistent();
	}



	@Override
	public boolean isEntailed(OWLAxiom arg0) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException,
			TimeOutException, AxiomNotInProfileException, FreshEntitiesException, InconsistentOntologyException {
		// TODO Auto-generated method stub
		return reasoner.isEntailed(arg0);
	}



	@Override
	public boolean isEntailed(Set<? extends OWLAxiom> arg0)
			throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException,
			AxiomNotInProfileException, FreshEntitiesException, InconsistentOntologyException {
		// TODO Auto-generated method stub
		return reasoner.isEntailed(arg0);
	}



	@Override
	public boolean isEntailmentCheckingSupported(AxiomType<?> arg0) {
		// TODO Auto-generated method stub
		return reasoner.isEntailmentCheckingSupported(arg0);
	}



	@Override
	public boolean isPrecomputed(InferenceType arg0) {
		// TODO Auto-generated method stub
		return reasoner.isPrecomputed(arg0);
	}



	@Override
	public boolean isSatisfiable(OWLClassExpression arg0) throws ReasonerInterruptedException, TimeOutException,
			ClassExpressionNotInProfileException, FreshEntitiesException, InconsistentOntologyException {
		// TODO Auto-generated method stub
		return reasoner.isSatisfiable(arg0);
	}



	@Override
	public void precomputeInferences(InferenceType... arg0)
			throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException {
		// TODO Auto-generated method stub
	
		reasoner.precomputeInferences(arg0);
	}

	
}
