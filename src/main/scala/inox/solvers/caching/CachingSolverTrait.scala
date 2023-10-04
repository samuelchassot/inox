package inox
package solvers
package cachinng

trait CachingSolverT extends Solver {
  import program.trees._
  import SolverResponses._

  override def name: String = "Caching:" + super.name

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
