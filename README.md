# TCTNew
The project aims at discovering disjointness axiom through the induction of a Terminological Cluster Tree, an unsupervised model that extends first order logic tree in order to be compliant with SW representations.

# How to run 
The project can be imported as Maven project after the repository is cloned. The project allows to discover disjointness axioms via 3 possible approaches:
- terminological cluster tree 
- pearson coefficient correlation
- negative association rule mining
Each method can be invoked by passing as argument on of the following value: "tct", "corr, "apriori" 

The progam can be configured modifying the file experiments.properties that contains the value of the following parameters:
- the seed for controlling the random aspects of the algorithms
- FOLDS, the number of run required for the experiments
- beam, the number of candidate refinements generated via refinement operator and used as features of the tree
- the distance measure adopted by the terminologicla cluster tree induction algorithm (admissible values: simpleDistance1,
	simpleDistance2, entropicSimpleDistance1, entropicSimpleDistance2, sqrtDistance1, sqrtDistance2)

# 
