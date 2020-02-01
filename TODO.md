# Tasks Routing

## Prio 1
* SimpleGraphFactory (from csv)
* more DijkstraAborter
  * one of several edges
  * all of several edges
  * maximum weight
* Start from offset (GraphDecorator)
* Edge-Weight configurable via attributes (EdgeWeigher)
* Locator (extract edge from graph?)

## Prio 2

* A* (Estimate)
* double A*
* TileGraph (GraphDecorator)
* HierarchicalGraph (GraphDecorator)
* ForbiddenManeuvers (GraphDecorator?)
* OpenStreetMap-Importer
* time-dependent routing
* EdgeWeigher dependent on used vehicle

## Done

* make Dijkstra abort criteria configurable
  * full
  * one edge
* Route instead of List<Edge>
* cleanup: dedicated workflow and two convenience methods
* include cost determination
