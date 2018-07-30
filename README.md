# TCTNew
The project aims at discovering disjointness axiom through the induction of a Terminological Cluster Tree (TCT), an unsupervised model that extends first order logic tree in order to be compliant with SW representations. 

# How to run 
The project can be imported as Maven project after the repository is cloned. For the sake of comparisons, the project allows to discover disjointness axioms via 3 possible approaches:
- terminological cluster tree  
- pearson coefficient correlation
- negative association rule mining
Each method can be invoked by passing as argument on of the following value: "tct", "corr, "apriori" 

The progam can be configured modifying the file experiments.properties that contains the value of the following parameters:
- the seed for controlling the random aspects of the algorithms
- FOLDS:  the number of run required for an experiment (in order to mitigate the effectiveness of random choices)
- beam:  the number of candidate refinements generated via refinement operator and used as features of the tree
- distance: the distance measure adopted by the terminologicla cluster tree induction algorithm (admissible values: simpleDistance1,
	simpleDistance2, entropicSimpleDistance1, entropicSimpleDistance2, sqrtDistance1, sqrtDistance2)
- timeout, a timeout for stopping the induction of a TCT (0 means that the algorithm does not use a timeout)
- nResults, to trunk the results output by the extraction of a TCT ((0 means that the algorithm extracts all the disjointness axioms)
- refinementoperator, the refinement operator adopted by a TCT (admissible values: single , i.e. a single thread refinement operator, 
-spark,  i.e. the spark implementation of the previuous one)
- prototype, the kind of prototypical individuals used to split the set of individuals (admissbile values:  single, a single linkage approach to cluster the individuals, medoids, i.e. a partitioning around the medoids of two clusters of individuals)


# Publications
- Giuseppe Rizzo, Claudia d'Amato, Nicola Fanizzi, Floriana Esposito:
Terminological Cluster Trees for Disjointness Axiom Discovery. ESWC (1) 2017: 184-201

- Giuseppe Rizzo, Claudia d'Amato, Nicola Fanizzi, Floriana Esposito:
Induction of Terminological Cluster Trees. URSW@ISWC 2016: 49-60


