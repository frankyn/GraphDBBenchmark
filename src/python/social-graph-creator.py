# Outputs a graphml representation of a social network graph following
# a Barabasi-Albert degree distribution.

from random import *
from sys import *
from networkx import *

degree = 5
vertices = int(sys.argv[1])
firstnames = [i for i in open(sys.argv[2]).readlines()]
firstnames_l = len(firstnames)
# http://www.ssa.gov/oact/babynames/limits.html
lastnames = [i for i in open(sys.argv[3]).readlines()]
lastnames_l = len(lastnames)
# http://names.mongabay.com/most_common_surnames.htm

g = barabasi_albert_graph(vertices, degree)

for n in g.nodes_iter():
	g.node[n]['Firstname'] = firstnames[randint(0, firstnames_l-1)]
	g.node[n]['Lastname'] = lastnames[randint(0, lastnames_l-1)]
	g.node[n]['Age'] = str(randint(0,99))
for e in g.edges_iter():
    if random() < 0.5:
		g.edge[e[0]][e[1]]['Relationship'] = 'friend'
    else:
        g.edge[e[0]][e[1]]['Relationship'] = 'family'
	
g.remove_node(0)
write_graphml(g, sys.argv[4])