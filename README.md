Implemented a simulation of google's page rank algorithm.

The program removes sink nodes(web page with no links to other pages) recursively and supports random walk.

The input is a matrix of weights.

Each column of the matrix represents a node(web page). Weight depends on the number of out-links, that is, number of links in the current page, and total weight of a column sums to 1.
