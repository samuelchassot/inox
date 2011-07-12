import cp.Definitions._
import cp.Terms._
import cp.LTrees._

object PaperExamples extends App {
  def p(str : String, a : =>Any) : Unit = {
    println(str + " : " + a.toString)
  }

  trait Demo {
    val name : String
    def action : Unit
    def run : Unit = {
      println("*** Running " + name + " ***")
      action
    }
  }

  object Initial extends Demo {
    val name = "first examples"
    val c1 : Constraint2[Int,Int] = ((x : Int, y : Int) => 2 * x + 3 * y == 10 && x >= 0 && y >= 0)
    def action : Unit = {
      p("c1(2,1)", c1(2,1))
      p("c1(5,0)", c1(5,0))
      p("c1.solve", c1.solve)
      p("c1.findAll.toList", c1.findAll.toList)
      p("bounded(3).findAll.toList", bounded(3).findAll.toList)
    }
  }
  Initial.run

  def bounded(m : Int) : Constraint1[Int] = ((x : Int) => x >= 0 && x <= m)

  object Matrices extends Demo {
    val name = "unimodular matrices"
    def boundedQ(m : Int) : Constraint4[Int,Int,Int,Int] = {
      ((x : Int, y : Int, z : Int, u : Int) =>
        x >= 0 && x <= m &&
        y >= 0 && y <= m &&
        z >= 0 && z <= m &&
        u >= 0 && u <= m)
    }
  
    @spec def det(a : Int, b : Int, c : Int, d : Int) : Int = a*d - b*c
    @spec def isUnimodular(a : Int, b : Int, c : Int, d : Int) : Boolean = {
      val dt = det(a,b,c,d)
      dt == 1 || dt == -1
    }
  
    def inverse(a : Int, b : Int, c : Int, d : Int) : Option[(Int,Int,Int,Int)] = 
      ((x: Int, y: Int, z: Int, u: Int) =>
        a*x+b*z == 1 && 
        a*y+b*u == 0 &&
        c*x+d*z == 0 &&
        c*y+d*u == 1).find
  
    def boundedUnimodular(m : Int) = {
      boundedQ(m) &&
      ((a : Int, b : Int, c : Int, d : Int) => isUnimodular(a,b,c,d)) // TODO would be great if we could write this as isUnimodular _ :)
    }
  
    def action : Unit = {
      p("Unimodular count", (0 to 5).map(boundedUnimodular(_).findAll.size).toList)
      p("boundedUnimodular(2).findAll.take(5).foreach.println(_))", boundedUnimodular(2).findAll.take(5).toList.map(_.toString))
      p("inverse of(4,5,3,4): ", inverse(4,5,3,4))
      p("inverse of(4,5,3,0): ", inverse(4,5,3,0))
    }
  }
  Matrices.run

  object SatSolving extends Demo {
    val name = "sat solving"
    def satSolve(problem : Seq[Seq[Int]]) = {
      problem.map(l => l.map(i => {
        val id = scala.math.abs(i)
        val isPos = i > 0
        val c : Constraint1[Map[Int,Boolean]] = ((m : Map[Int,Boolean]) => m(id) == isPos)
        c
      }).reduceLeft(_ || _)).reduceLeft(_ && _).find
    }

    def action : Unit = {
      println("satSolve(p)", satSolve(Seq(Seq(1,-2,-3), Seq(2,3,4), Seq(-1,-4))))
      println("satSolve(Seq(Seq(1,2), Seq(-1), Seq(-2)))", satSolve(Seq(Seq(1,2), Seq(-1), Seq(-2))))
    }
  }
  SatSolving.run

  object Knapsack extends Demo {
    val name = "knapsack"

    def action : Unit = {
      val values  : List[Int] = List(4, 2, 2, 1, 10)
      val weights : List[Int] = List(12, 1, 2, 1, 4)
      val maxWeight : Int = 15

      def conditionalSumTerm(values : List[Int]) : IntTerm1[Map[Int,Boolean]] = {
        values.zipWithIndex.map(pair => {
          val (value,index) = pair
          ((m : Map[Int,Boolean]) => (if(m(index)) value else 0)).i
        }).reduceLeft(_ + _)
      }
      val valueFunction = conditionalSumTerm(values.map(x => -x))
      val weightFunction = conditionalSumTerm(weights)
      val answer = ((x : Int) => x <= maxWeight)
        .compose0(weightFunction)
        .minimizing(valueFunction)
        .find

      p("Knapsack map", answer)
    }
  }
  Knapsack.run

  object Primes extends Demo {
    val name = "prime numbers"

    @spec def divides(i : Int, j : Int) : Boolean = i * (j / i) == j
    @spec def noneDivides(from : Int, j : Int) : Boolean = {
      if(from == j) {
        true
      } else {
        !divides(from, j) && noneDivides(from+1, j)
      }
    }
    @spec def isPrime(i : Int) : Boolean = (i >= 2 && noneDivides(2, i))
  
    val primes = (isPrime(_:Int)) minimizing((x:Int) => x) findAll
  
    def action : Unit = {
      println("Primes")
      primes.take(10).foreach(println(_))
    }
  }
  Primes.run

  object SendMoreMoney extends Demo {
    val name = "SEND+MORE=MONEY"

    def asserting(c : Constraint0) : Unit = {
      var entered = false
      for(i <- c.lazyFindAll) {
        entered = true
      }
      if(!entered) { throw new Exception("Asserting failed.") }
    }

    def action : Unit = {
      val anyInt : Constraint1[Int] = ((n : Int) => true)

      val letters @ Seq(s,e,n,d,m,o,r,y) = Seq.fill(8)(anyInt.lazySolve)

      for(l <- letters) {
        asserting(l >= 0 && l <= 9) 
      }

      asserting(s >= 1)
      asserting(m >= 1) 

      when(distinct[Int](s,e,n,d,m,o,r,y)) {
        println("Letters now have distinct values.")
      } otherwise {
        println("Letters can't have distinct values.")
      }

      val fstLine = anyInt.lazySolve
      val sndLine = anyInt.lazySolve
      val total = anyInt.lazySolve

      asserting(fstLine == 1000*s + 100*e + 10*n + d)
      asserting(sndLine == 1000*m + 100*o + 10*r + e)
      asserting(total   == 10000*m + 1000*o + 100*n + 10*e + y)
      when(total == fstLine + sndLine) {
        println("The puzzle has a solution : " + letters.map(_.value) + " (" + fstLine.value + " + " + sndLine.value + " = " + total.value + ")")
      } otherwise {
        println("The puzzle has no solution.")
      }
    }
  }
  SendMoreMoney.run

}

// Unused: 

//  object Graphs {
//    @spec sealed abstract class List
//    @spec case class Cons(head : Int, tail : List) extends List
//    @spec case class Nil() extends List
//
//    @spec sealed abstract class OptInt
//    @spec case class IntSome(value : Int) extends OptInt
//    @spec case class None() extends OptInt
//
//    @spec def contains(l : List, e : Int) : Boolean = l match {
//      case Nil() => false
//      case Cons(x,xs) => x == e || contains(xs,e)
//    }
//
//    //type Graph = Map[Int,List]
//    @spec def hasEdge(g : Map[Int,List], src : Int, dst : Int) : Boolean = {
//      if(g.isDefinedAt(src)) contains(g(src), dst) else false
//    }
//
//    @spec def validPath(g : Map[Int,List], path : List) : Boolean = path match {
//      case Nil() => true
//      case Cons(x, Nil()) => true
////      case Cons(x, xs @ Cons(y, _)) if (x == y) => validPath(g, xs)
//      case Cons(x, xs @ Cons(y, _)) => hasEdge(g, x, y) && validPath(g, xs)
//    }
//
//    @spec def length(l : List) : Int = (l match {
//      case Nil() => 0
//      case Cons(_,xs) => 1 + length(xs)
//    }) ensuring(_ >= 0)
//
//    @spec def head(l : List) : OptInt = l match {
//      case Nil() => None()
//      case Cons(x,_) => IntSome(x)
//    }
//
//    @spec def tail(l : List) : OptInt = l match {
//      case Nil() => None()
//      case Cons(x,Nil()) => IntSome(x)
//      case Cons(x,xs) => tail(xs)
//    }
//
//    def paths(g : Map[Int,List], src : Int, dst : Int) = {
//      ((path : List) => 
//        head(path) == IntSome(src) &&
//        tail(path) == IntSome(dst) &&
//        validPath(g, path)).minimizing(length(_ : List)).findAll
//    }
//
//    def run : Unit = {
//      def mkList(vals : Int*) : List = vals.foldRight[List](Nil())((v,l) => Cons(v,l))
//      val graph : Map[Int,List] = Map(
//        1 -> mkList(2,3),
//        2 -> mkList(3),
//        3 -> mkList(4),
//        4 -> mkList(1))
//
//      // That crashes somehow...
//      //p("Paths from 1 to 4, in order of length", paths(graph, 1, 4).toList)
//
//      ((g : Map[Int,List], p : List) => validPath(g, p) && length(p) == 10).find
//    }
//  }
//
//  //Graphs.run
