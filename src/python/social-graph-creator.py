# Generate Artificial Graphs
# Alex Averbuch (alex.averbuch@gmail.com)
# Modified by Valentin Vansteenberghe (val.morro@gmail.com)

from igraph import *
from random import *
from sys import *

degree = int('5')
vertices = int(sys.argv[1])
firstnames = [i for i in open(sys.argv[2]).readlines()]
firstnames_l = len(firstnames)
# http://www.ssa.gov/oact/babynames/limits.html
lastnames = [i for i in open(sys.argv[3]).readlines()]
lastnames_l = len(lastnames)
# http://names.mongabay.com/most_common_surnames.htm

g = Graph.Barabasi(n=vertices, m=degree, power=1, directed=False, zero_appeal=8)

for v in g.vs:
	g.vs[v.index]['Firstname'] = firstnames[randint(0, firstnames_l-1)]
	g.vs[v.index]['Lastname'] = lastnames[randint(0, lastnames_l-1)]
	g.vs[v.index]['Age'] = str(randint(0,99))
for e in g.es:
    if random() < 0.5:
        g.es[e.index]["relationship"] = 'friend'
    else:
        g.es[e.index]["relationship"] = 'family'
g.write_graphml(sys.argv[4])