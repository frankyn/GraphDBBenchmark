from sys import *
from networkx import *
import os

g = read_edgelist(sys.argv[1])

counter = 1

for n in g.nodes_iter():
	g.node[n]['cid'] = str(counter)
	counter = counter + 1
	
write_graphml(g, sys.argv[2])