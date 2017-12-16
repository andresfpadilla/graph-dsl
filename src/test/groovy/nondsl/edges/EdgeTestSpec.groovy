package nondsl.edges

import graph.Edge
import spock.lang.Specification

class EdgeTestSpec extends BaseEdgeSpec {
    def setup() {
        emptyEdge = new Edge()
        edge = new Edge(one:'step1', two:'step2')
        equalEdge = new Edge(one: 'step1', two: 'step2', weight:10)
        bothDifferent = new Edge(one: 'step4', two: 'step3')
        firstDifferent = new Edge(one: 'step3', two: 'step2')
        secondDifferent = new Edge(one: 'step1', two: 'step3')
        switched = new Edge(one: 'step2', two: 'step1')
        falseEdges = [new Edge(), new Edge(one:''), new Edge(one:'is true'), new Edge(one:'is true', two:'')]
    }

    def 'equals with both vertices switched'() {
        expect:
        edge == switched
    }
}
