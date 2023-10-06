/* Copyright 2009-2018 EPFL, Lausanne */

package inox
package solvers
package combinators

import inox.solvers.caching.CachingSolver

object CachingSolverFactory {
  def apply(p: Program)(sf: SolverFactory {
    val program: p.type
    type S <: TimeoutSolver { val program: p.type }
  }): SolverFactory {
    val program: p.type
    type S <: TimeoutSolver { val program: p.type }
  } = {
    class Impl(override val program: p.type)
        extends CachingSolver(p, sf.getNewSolver())
        with TimeoutSolver

    SolverFactory.create(p)("E:" + sf.name, () => new Impl(p))
  }
}
