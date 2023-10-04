package inox
package solvers
package cachinng

class CachingSolver private (
    override val context: inox.Context
)(protected val underlying: Solver)
    extends Solver {
  override val program: underlying.program.type = underlying.program

  import program.trees._
  import SolverResponses._

  def this(
      underlying: Solver
  ) =
    this(underlying.context)(underlying)

  def name: String = "Caching:" + underlying.name

  def declare(vd: ValDef) = underlying.declare(vd)

  def assertCnstr(expr: Expr) = underlying.assertCnstr(expr)

  def check(config: CheckConfiguration) =
    underlying.check(config)
  def checkAssumptions(config: Configuration)(assumptions: Set[Expr]) =
    underlying.checkAssumptions(config)(assumptions)

  def interrupt() = underlying.interrupt()
  def free() = underlying.free()
  def reset() = underlying.reset()
  def push() = underlying.push()
  def pop() = underlying.pop()
}
