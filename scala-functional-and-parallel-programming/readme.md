# Scala Funtional and Parallel Programming

These projects are based on the assigments of two courses [Functional Programming Principles in Scala](https://www.coursera.org/learn/progfun1) and [Parallel Programming](https://www.coursera.org/learn/parprog1) on coursera 

### 1. patmat
Huffman encoding in Scala

### 2. scalashop
Bluring picture through basic task parallelism. The Join/Fork of Java8 framework is applied. 

### 3. reductions
Some tasks on the collection such as scan is hard to be parallel processed becasue the computation of one chunk is based on previous result.
To make possible the parallelism of these tasks, a intermediate tree (somewhat like segmenttree) should be bulit in parallel (upsweep) and
the computaion of each chunk can be based on the node of the tree. Overall, more computations are required but the computations can be 
assigned in parallel

### 4. kmeans
Implementation of K-mean algorithm through parallel collection in Scala. This mini project also use K-means algorithm to compress image

### 5. barneshut
Simulation of simplifed N-bodies problerm. Parallel combainer based on [conc-tree]() is applied to improve efficiency. 
