# Outputs a graphml representation of a social network graph following
# a Barabasi-Albert degree distribution.

from random import *
from sys import *
from networkx import *

degree = 5
vertices = int(sys.argv[1])
firstnames = [i.strip() for i in open(sys.argv[2])]
firstnames_l = len(firstnames)
# http://www.ssa.gov/oact/babynames/limits.html
lastnames = [i.strip() for i in open(sys.argv[3])]
lastnames_l = len(lastnames)
cities = [i.strip() for i in open(sys.argv[4])]
cities_l = len(cities)
# http://names.mongabay.com/most_common_surnames.htm

g = barabasi_albert_graph(vertices, degree)

counter = 1

for n in g.nodes_iter():
	g.node[n]['Firstname'] = firstnames[randint(0, firstnames_l-1)]
	g.node[n]['Lastname'] = lastnames[randint(0, lastnames_l-1)]
	g.node[n]['cid'] = str(counter)
	counter = counter + 1
#for e in g.edges_iter():
#	g.edge[e[0]][e[1]]['In'] = cities[randint(0, cities_l-1)]
	
g.remove_node(0)
write_graphml(g, sys.argv[5])