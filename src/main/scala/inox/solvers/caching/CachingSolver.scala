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

  var cache: Cache = MapCache()

  def name: String = "Caching:" + underlying.name

  def declare(vd: ValDef): Unit = underlying.declare(vd)

  def assertCnstr(expr: Expr): Unit = underlying.assertCnstr(expr)

  def check(
      config: CheckConfiguration
  ): config.Response[CachingSolver.this.Model, Assumptions] =
    underlying.check(config)
  def checkAssumptions(config: Configuration)(
      assumptions: Set[Expr]
  ): config.Response[CachingSolver.this.Model, Assumptions] =
    underlying.checkAssumptions(config)(assumptions)

  def interrupt(): Unit = underlying.interrupt()
  def free(): Unit = underlying.free()
  def reset(): Unit = underlying.reset()
  def push(): Unit = underlying.push()
  def pop(): Unit = underlying.pop()
}

trait Key {}
trait Value {}

trait Cache {
  def insert(key: Key, value: Value): Unit
  def lookup(key: Key): Option[Value]
}

class MapCache extends Cache {
  var cache: Map[Key, Value] = Map()

  def insert(key: Key, value: Value): Unit = {
    cache = cache + (key -> value)
  }

  def lookup(key: Key): Option[Value] = {
    cache.get(key)
  }
}
