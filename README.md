# graph-dsl

A groovy dsl for creating and traversing extensible graphs. The graph can be extended with plugins and traits which 
allow developers to create a graph with the desired behavior and values for their algorithm. For project build status 
check the [wiki](https://github.com/moaxcp/graph-dsl/wiki).

# Usage

```groovy
#!/usr/bin/env groovy
@Grab(group='com.github.moaxcp', module='graph-dsl', version='0.15.0')
```

## Creating a graph

The entry-point to the dsl is the `Graph.graph(Closure)` method. This method applies the closure to a new Graph object 
and returns it. 

```groovy
def graph = graph {
    edge step1, step2
}
assert graph.vertices.keySet() == ['step1', 'step2'] as Set //vertices were created!
assert graph.edges.size() == 1
assert graph.edges.first() == new Edge(one:'step1', two:'step2') //edge was created!
```

This example of a graph creates two vertices named 'step1' and 'step2' as well as an edge between them. The basic graph 
structure is held in a map of named Vertex objects and a set of Edge objects. Each Edge contains the names of the two
vertices it connects.

There are a few rules the dsl follows as it is being processed:

1. At the end of an operation all referenced vertices are created if they don't exist.
2. If an edge or vertex already exists it will be reused by and operation.

This gives the developer flexibility to create and modify the graph in many different ways.

```groovy
def workQueue = new LinkedList()
graph {
    edge (A, B) {
        traits Mapping, Weight
        queue = workQueue
        weight { queue.size() }
    }
    
    vertex A(traits:Mapping) {
        action = {
            println "processing"
            workQueue << "work"
        }
    }
    
    vertex B {
        traits Mapping
        action = {
            println "done processing ${workQueue.poll()}"
        }
    }
}
```

In this graph the edge method creates the vertices A and B as well as an edge. It also configures the edge to have a
queue and a weight that is the size of the queue. The vertices A and B are also configured to perform an action. A adds
work to the queue and B processes the work.

The Default behavior of a graph is that of an undirected graph. These graphs have a set of edges where only one edge
can connect any two vertices. An undirected graph can be changed to a directed graph at any time using 
`DirectedGraphPlugin`.

```groovy
graph {
    //lots of code
    apply DirectedGraphPlugin
    //lots of code
}
```

Traits can be added to all edges and vertices using `edgeTraits` and `vertexTraits`.

```groovy
graph {
    edgeTraits Mapping, Weight
    vertexTraits Mapping
}
```

## Traversing a graph

Once a graph is created there is a dsl for depthFirstTraversal and breadthFirstTraversal.

```groovy
graph {
    apply DirectedGraphPlugin
    vertex A {
        connectsTo 'B', 'D', 'E'
        connectsFrom 'D'
    }
    
    vertex D {
        connectsTo 'C', 'E'
        connectsFrom 'B'
    }
    
    edge B, C
    depthFirstTraversal {
        root = 'A'
        preorder { vertex ->
            println vertex.name
        }
    }
    
    breadthFirstTraversal {
        root = 'A'
        visit { vertex ->
            println "bft $vertex.name"
        }
    }
}
```

## Functional search methods

There are functional search methods build on the depthFirstTraversal and breadthFirstTraversal method. These methods 
follow the standard names in groovy: each, find, inject, findAll, collect. The methods can specify which type of 
search to perform such as `eachBfs` or `eachDfs`. When a search type is not specified the methods default to depth 
first search.

```groovy
eachBfs {
    println it.name
}
    
def vertex = findBfs {
    it.name == 'A'
}
    
def bfsOrder = collectBfs {
    it.name
}
```

Note: These methods are not yet implemented for depth first traversal. The depth first search methods will be the
defaults when a search type is not specified.

## Edge Classification

Depth first traversal supports edge classification where an edge is classified as:

* tree-edge - when the destination vertex is white
* back-edge - when the destination vertex is grey
* forward-edge - when the destination vertex is black
* cross-edge - when the destination vertex is black and in a different tree

To classify edges use the `classifyEdges(Closure)` method.

```
graph {
    //setup graph
    classifyEdges { from, to, edgeType ->
        println "$from $to is $edgeType"
    }
}
```

`classifyEdges` returns an EdgeClassification object. This object contains lists of all back edges, tree-edges,
forward edges, and cross edges. There is also a new Graph called forest that gets created. 
`EdgeClassification.forrest` contains the forrest created by tree edges. It uses the vertex and edge objects
from the original graph object.

# Plugins

Plugins provide graphs with extra functionality. They can:

1. Modify all edges and vertices by adding traits or replacing them
2. modify the factories so they create different edges and vertices 
3. perform meta-programming on the graph to change or add methods

## DirectedGraphPlugin

The DirectedGraphPlugin changes the behavior of a graph to that of a directed graph. In a directed graph edges 
become directional. Only two edges can exist between any two vertices. In that case, the edges need to go in 
opposite directions.

Traversal methods will only follow out edges from a vertex.

## EdgeWeightPlugin

This plugin applies Weight to all edges and changes all traversal methods to follow edges in order of their weight.

## EdgeMapPlugin

All edges and all future edges will have the Mapping triat.

Note: will probably be deleted in the future.

## VertexMapPlugin

All vertices and all future vertices will have the Mapping trait.

Note: will probably be deleted in the future.

# Traits

Traits are standard groovy traits that developers or plugins can apply to Vertex or Edge
delegates. The Vertex and Edge objects call their delegate when a method or property is
missing. This allows properties and methods to be added to a Vertex or Edge at runtime.

## Mapping

Allows a vertex or edge to act like a Map. When a property is missing in the delegate this
trait will return or set the value in its map.

## Weight

Adds a weight property to a vertex or map. The weight is set with the `weight` method passing
in a closure. This makes the weight lazy so it can change dynamically.

# Getting Started With Development/Contributing

## install git

Follow this guide to install git.

https://git-scm.com/book/en/v2/Getting-Started-Installing-Git

## install gitflow-avh

This project uses gitflow-avh. It is a plugin for git that provides the `git flow` commands. These commands are used to
follow the gitflow pattern for developing software. For more information see
[Workflow](https://github.com/moaxcp/graph-dsl/wiki/Workflow) in the wiki.

https://github.com/petervanderdoes/gitflow-avh/wiki/Installation

## clone the project

clone the project from github.

git clone https://github.com/moaxcp/graph-dsl.git

## build

The project uses gradle for the build system. Using gradlew, you do not need to install gradle to build the project.

`./gradlew build`

## Contributing

Contributions are welcome. Please submit a pull request to the develop branch in github.

## Ways to contribute

Even if you are just learning groovy there are many ways to contribute.

* Fix a codenarc issue. See [codenarc report](https://moaxcp.github.io/graph-dsl/reports/codenarc/main.html)
* Add/fix groovydoc
* create a wiki page
* create a plugin
* add a graph algorithm

If there are any issues contact me moaxcp@gmail.com.

# Technology

* [github](https://github.com/)
* [git](https://git-scm.com/)
* [gitflow-avh](https://github.com/petervanderdoes/gitflow-avh)
* [gradle](https://gradle.org/) 
    * [gradle-gitflow](https://github.com/amkay/gradle-gitflow)
* [codenarc](http://codenarc.sourceforge.net/)
* [groovy](http://www.groovy-lang.org/)
* [spock](http://spockframework.org/)
* [jacoco](http://www.eclemma.org/jacoco/)
* [travis-ci.org](https://travis-ci.org/moaxcp/graph-dsl)
* [oss sonatype](https://oss.sonatype.org/#welcome)

# Releases

## 0.16.0

* [#80](https://github.com/moaxcp/graph-dsl/issues/80) Removed sonarqube since it no longer supports groovy
* [#78](https://github.com/moaxcp/graph-dsl/issues/78) Changed edgesFirst and edgesSecond in dsl to connectsTo and connectsFrom.
* [#77](https://github.com/moaxcp/graph-dsl/issues/77) renameOne and renameTwo in edge methods will not add extra vertex objects to the graph.

## 0.15.0

This release combines fixes for a few issues on github.

* [#75](https://github.com/moaxcp/graph-dsl/issues/75) Vertex.name, Edge.one, and Edge.two is now @PackageScope. This only 
affects code that is @CompileStatic for now due to [GROOVY-3010](https://issues.apache.org/jira/browse/GROOVY-3010).
* [#74](https://github.com/moaxcp/graph-dsl/issues/74)Vertices and edges may now be deleted. A vertex cannot be deleted if
there are edges referencing it.
* [#73](https://github.com/moaxcp/graph-dsl/issues/73) Added EdgeWeightPlugin. This plugin adds the Weight trait to each 
edge. Traversal methods are ordered by edge weight.

There were also several other changes that were not an issue on github:

Updated gradle to 3.5. Refactored gradle script to use the plugins closure when possible. gradle-gitflow does not work 
with the closure because it is not in the gradle repository. This is another reason to update the plugin. Spock was also 
updated to 1.1.

Added edgeTraits and vertexTraits. These methods will ensure traits are applied to current and future edges and vertices.

Added tests to provide more code coverage in jacoco.

Added more usage details to README.md

## 0.14.0

Just as in 0.13.0, where the config closure was removed from VertexSpec, this release removes the config closure from 
EdgeSpec. Traits can be added and configured for an Edge all within the same closure.

```groovy
graph.with {
    edge (step1, step2) {
        traits Mapping
        message = "hello from edge between $one and $two"
        queue = [] as LinkedList
        traits Weight
        weight { queue.size() }
    }
}
```

As in 0.13.0 this represents a major step in finalizing the edge api.

## 0.13.0

This release removes the need for the config closure when creating a vertex. It is now possible to apply traits and configure
them all within the closure passed to vertex methods.

```groovy
graph.with {
   vertex step1 {
       traits Mapping
       message = 'hello from step1'
       queue = [] as LinkedList
       traits Weight
       weight { queue.size() }
   }
}
```

This change represents a major step in finalizing the vertex api.

## 0.12.0

This release introduces a new way of creating a Vertex. The new methods allow vertices to be created without using strings for names.
Vertices can now be added with the following syntax.

```groovy
graph.with {
    vertex step1
    vertex step2(traits:Mapping)
    vertex step3 {
        traits Mapping
        config {
            label = 'step3'
        }
    }
    vertex step4(traits:Mapping) {
        config {
            label = 'step4'
        }
    }
}
```

This was achieve by adding propertyMissing and methodMissing to Graph which return a VertexSpec. A vertex(VertexSpec) method was
added to allow this syntax.

## 0.11.0

Removing connectsTo from VertexSpec and replacing it with edgesFirst and edgesSecond. This helps in creating directed graphs
using the vertex methods. It also helps when creating vertices first then applying the DirectedGraphPlugin.

## 0.10.3

Publishing of gh-pages was still broken. This release fixed the issue. Also added reports to gh-pages so we can now view
test results, code coverage, and static analysis.

## 0.10.2

Fixed publishing of gh-pages.

## 0.10.1

Release to maven central for 0.10.0 failed. Upgraded gradle-nexus-staging-plugin which fixed the problem.

## 0.10.0

This release features breadth first search methods. These are methods that follow the standard collection methods
that come with groovy: eachBfs, findBfs, findAllBfs, injectBfs, and collectBfs. There are two overrides for each.
The first takes a closure and starts at the first vertex in the vertices map. The second can specify the root to
start at in the breadth first traversal. Here is a short example using findBfs:

```groovy
findBfs {
    it.work > 0
}
```

Finds the first vertex with `work > 0`

```groovy
findBfs('step4') {
    it.work > 0
}
```

Finds the first vertex with `work > 0` starting at step4.

## 0.9.0

Added reversePostOrder to DirectedGraphPlugin. This will allow a DirectedGraph to perform a
closure on each vertex in reverse post order (topological order). Also added reversePostOrderSort which
returns the vertex names in reverse post order of a graph.

DirectedGraphPlugin was reorganized making it possible to javadoc methods dynamically added to the graph.
The methods added are static in DirectedGraphPlugin. They are added to Graph's metaClass using method pointers
while currying the graph param to the apply method. This could be used in future plugins.

## 0.8.2

* fixing publishing of javadoc and groovydoc. gh-pages branch must exist.

## 0.8.1

* Automatically publishing javadoc and groovydoc to gh-pages.

## 0.8.0

This is a major update to the api. It focuses on the edge and vertex methods in Graph. These methods
provide developers with the flexibility to create and configure the objects as they see fit. All edge
and vertex methods return the object which allows the object to be configured with the `<<` operator.
The methods use *Spec objects to store specifications for the edge or vertex and then applies it to the
graph. See the groovydoc for more details.

* upgraded to groovy-2.4.10
* Adding edge and vertex methods to Graph which provides many new ways to manipulate a graph.
* Vertices can be renamed and edges can be moved to different vertices.
* Anytime a vertex is referenced it will be created.

## 0.7.0

* Added support for classifying edges. This can be used to detect cycles in a graph.
* Added test coverage.
* Added javadoc.

## 0.6.0

* fixed issue with logging when optimizations are turned off
* updating gradle to 3.4.1
* fixed many codenarc issues
* added support for breadth first traversal

## 0.5.1

* Added warning when optimizations are turned off
* Adjusting retry time for nexus-staging-plugin

## 0.5.0

* Fixed vertex and edge methods so they can modify existing objects
* Added entry point to dsl though graph method
* Added trait key to vertex and edge map. This makes it easier to add a single trait.

## 0.4.4

* Following suggestions from [#5](https://github.com/Codearte/gradle-nexus-staging-plugin/issues/5) to fix nexus release issue

## 0.4.3

* Adding fix for closeAndPromoteRepository

## 0.4.2

* Switched from maven-publish to maven plugin

## 0.4.1

* Fixes for maven-publish plugin.

## 0.4.0

* Artifacts publish to nexus automatically

## 0.3.0

* Added delegate support for Edges and Vertices
* Added VertexMapPlugin and EdgeMapPlugin

## 0.2.0

* Optimized codenarc configuration using the ruleset method.
* Updated gradle to version 3.3.
* Added Plugin interface for all Plugins to implement.
* Added plugins property to Graph which contains all plugins applied to the Graph.
* Adde DirectedGraph plugin which converts a Graph into a DirectedGraph.

## 0.1.2

sonarqube.com doesn't seem to show any issues for groovy projects. This release adds codenarc support for local development. This allows developers to view issues right away without worrying about sonar.

## 0.1.1

* Changed travis.sh so master gets published to sonar instead of develop.

## 0.1.0

* initial plugin support
* DirectedGraph plugin

## 0.0.0

* Support for vertices, edges, and depth first traversal

# License

The license can be found in the LICENSE file.

MIT License

Copyright (c) 2017 John Mercier
