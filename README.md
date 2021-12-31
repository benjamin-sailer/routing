Routing
=======

Features
--------

The main goals are:
- extensibility (various edge sources including in-memory for fast access as well as lazy loading to limit footprint
  on large graphs, several algorithms, prohibited maneuvers, several ways to provide routing weight, several abort
  criteria, ...)
- speed (there should be no build-in compromises that prohibit the use of the fastest-possible algorithm)
- clean API (no blurred interfaces with too many methods, etc., so that users are not forced to break SRP)

Architecture
------------

The package `de.bsailer.routing.model` contains the basic actors: `Edge` as entity on which
the routing takes place, `Graph` as a source of edges and the two value objects `EdgeIdentifier`
(for locating an edge) and `Route` (basically a list of edges).
The package `de.bsailer.routing.traversal` contains the shortest-path-algorithms to traverse a graph.
The package `de.bsailer.routing.factory` provides factories to construct implementors of the
model interfaces.

All `Edge`, `EdgeIdentifier` and `Graph` are generified, so they can be extended with features
that are really part of the interface (example: `IndexProvidingEdgeIdentifier` that allows
for `O(0)`-access of edges).

The `Graph` object is designed to be decorated to extend its features (e.g. prohibited maneuvers,
routing to a section of an edge, hierarchical graphs, etc.).
