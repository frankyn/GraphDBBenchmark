# Outputs a graphml representation of a social network graph following
# a Barabasi-Albert degree distribution.

from random import *
from sys import *
from networkx import *
import os

degree = 5
vertices = int(sys.argv[1])

g = barabasi_albert_graph(vertices + 1, degree)

counter = 1

for n in g.nodes_iter():
	g.node[n]['cid'] = str(counter)
	counter = counter + 1
	
g.remove_node(0)
write_graphml(g, sys.argv[2])