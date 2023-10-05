/* Copyright 2009-2018 EPFL, Lausanne */

package inox
package solvers
package combinators

import inox.solvers.cachinng.CachingSolver

object CachingSolverFactory {
  def apply(p: Program)(sf: SolverFactory {
    val program: p.type
    type S <: TimeoutSolver { val program: p.type }
  }): SolverFactory {

    val program: p.type
    type S <: TimeoutSolver { val program: p.type }
  } = {
    val newSolver = sf.getNewSolver()
    class Impl(override val program: p.type)
        extends CachingSolver(p, newSolver)
        with TimeoutSolver

    SolverFactory.create(p)("E:" + sf.name, () => new Impl(p))
  }
}
