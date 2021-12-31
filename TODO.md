# Tasks Routing

## Prio 1
* SimpleGraphFactory (from csv) -> doing
* more DijkstraAborter
  * one of several edges
  * all out of a list of several edges
  * maximum weight
* Start from offset (GraphDecorator)
* Edge.weight() configurable via attributes (EdgeWeigher)
  * needs Edge with attribute list
* Locator (extract edge from graph?)
  * needs edge with location information (full edge)
* ProhibitedPaths (graph decorator, reverse projection) -> doing

## Prio 2

* A* (Estimate)
  * needs Edge with location (start / end coordinate?)
  * needs translation between distance and minimum weight
* double A*
* TileGraph (graph decorator)
  * needs Edge with tile information
* HierarchicalGraph (graph decorator)
* OpenStreetMap-Importer
  * OSM-XML https://wiki.openstreetmap.org/wiki/OSM_XML
  * PBF (protobuf) pulls a large list of dependencies (protoc, even for the maven plugin, ...)
* time-dependent routing
* EdgeWeigher dependent on used vehicle

## Further ideas

* Routing Service (SpringBoot)
* Tile Service (use existing, probably additional backend in Golang)
* Web-Frontend: Angular with JavaScript geo library
  * https://openlayers.org/
  * https://turfjs.org/
  * https://leafletjs.com/
  * https://www.npmjs.com/package/geolib
  * https://www.gislounge.com/openlayers-geospatial-javascript-library/
  * https://www.esri.com/arcgis-blog/products/developers/constituent-engagement/10-open-source-projects-every-javascript-geo-dev-should-know-about/
* Android client (Kotlin)
* Lazy Graph Tile construction with TileCache and thread safeness

## Done

* make Dijkstra abort criteria configurable
  * full
  * one edge
* Route instead of List<Edge>
* cleanup: dedicated workflow and two convenience methods
* include cost determination
* 