package inox
package solvers
package caching

import inox.ast.Expressions

class CachingSolver private (
    override val program: Program,
    override val context: inox.Context
)
// Prog is an alias for `program`, as we cannot use `program` within `underlying`
(val prog: program.type)(protected val underlying: Solver {
  val program: prog.type
}) extends Solver {

  import program.trees._
  import SolverResponses._

  def this(
      p: Program,
      underlying: Solver { val program: p.type }
  ) =
    this(underlying.program, underlying.context)(underlying.program)(underlying)

  val cache: Cache = MapCache()

  var disableCache: Boolean =
    false // If one of the following methods is called, the cache is disabled for future calls: declare, interrupt, free, reset, push, pop

  var constraints: List[Expr] = Nil
  def name: String = "Caching:" + underlying.name

  def assertCnstr(expr: Expr): Unit = {
    if (!disableCache) {
      constraints = expr :: constraints
    }
    underlying.assertCnstr(expr)
  }

  def check(
      config: CheckConfiguration
  ): config.Response[CachingSolver.this.Model, Assumptions] = {
    if (!disableCache) {
      val key = constraints
      cache.lookup(key) match {
        case Some(value) =>
          value match {
            case v: config.Response[CachingSolver.this.Model, Assumptions] => v
            case _ => {
              val response = underlying.check(config)
              cache.insert(key, response)
              response
            }
          }
        case None => {
          val response = underlying.check(config)
          cache.insert(key, response)
          response
        }
      }
    } else {
      underlying.check(config)
    }

  }

  def checkAssumptions(config: Configuration)(
      assumptions: Set[Expr]
  ): config.Response[CachingSolver.this.Model, Assumptions] =
    underlying.checkAssumptions(config)(assumptions)

  // The followings functions are not supported by the cache, so once one of them is called, the cache will not be used anymore

  def declare(vd: ValDef): Unit = underlying.declare(vd)
  def interrupt(): Unit = underlying.interrupt()
  def free(): Unit = underlying.free()
  def reset(): Unit = underlying.reset()
  def push(): Unit = underlying.push()
  def pop(): Unit = underlying.pop()

  type Key = List[Expr]
  type Value = SolverResponse[CachingSolver.this.Model, Assumptions]

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

}
