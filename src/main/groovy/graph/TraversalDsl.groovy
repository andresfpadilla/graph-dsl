package graph

trait TraversalDsl {

    /**
     * executes closure on each {@link Vertex} in breadth first order starting at the given root {@link Vertex}. See
     * {@link #breadthFirstTraversal} for details.
     * @param root  vertex to start breadth first traversal
     * @param closure  execute on each {@link Vertex}
     */
    void eachBfs(String root = null, Closure closure) {
        breadthFirstTraversal {
            getDelegate().root = root
            visit { vertex ->
                closure(vertex)
                null
            }
        }
    }

    /**
     * Executes closure on each {@link Vertex} in breadth first order starting at root. If the closure returns true the
     * {@link Vertex} is returned.
     * @param root  vertex to start breadth first traversal
     * @param closure  execute on each {@link Vertex}
     * @return first {@link Vertex} where closure returns true
     */
    Vertex findBfs(String root = null, Closure closure) {
        Vertex result = null
        breadthFirstTraversal {
            getDelegate().root = root
            visit { vertex ->
                if (closure(vertex)) {
                    result = vertex
                    return Traversal.STOP
                }
            }
        }
        result
    }

    /**
     * Executes closure on each vertex in breadth first order starting at root. object is the initial value passed to
     * the closure. Each returned value from the closure is passed to the next call.
     * @param root  vertex to start breadth first traversal
     * @param object  initial value passed to the closure
     * @param closure  execute on each {@link Vertex}
     * @return object returned from the final call to closure.
     */
    Object injectBfs(String root = null, Object object, Closure closure) {
        Object result = object
        breadthFirstTraversal {
            getDelegate().root = root
            visit { vertex ->
                result = closure(result, vertex)
            }
        }
        result
    }

    /**
     * Runs closure on each vertex in breadth first order starting at root. The vertices where closure returns true are
     * returned.
     * @param root  vertex to start breadth first traversal
     * @param closure  execute on each {@link Vertex}
     * @return the vertices where closure returns true
     */
    List<? extends Vertex> findAllBfs(String root = null, Closure closure) {
        Closure inject = null
        if (root) {
            inject = this.&injectBfs.curry(root)
        } else {
            inject = this.&injectBfs
        }
        (List<? extends Vertex>) inject([]) { result, vertex ->
            if (closure(vertex)) {
                result << vertex
            }
            result
        }
    }

    /**
     * Runs closure on each vertex in breadth first order, starting at root, collecting returned values from the
     * closure.
     * @param root  vertex to start breadth first traversal
     * @param closure  execute on each {@link Vertex}
     * @return values returned from each execution of closure
     */
    List<?> collectBfs(String root = null, Closure closure) {
        Closure inject = null
        if (root) {
            inject = this.&injectBfs.curry(root)
        } else {
            inject = this.&injectBfs
        }
        (List<? extends Vertex>) inject([]) { result, vertex ->
            result << closure(vertex)
        }
    }
}