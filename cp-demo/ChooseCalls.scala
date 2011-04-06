import cp.Definitions._

object ChooseCalls { 
  sealed abstract class Color
  case class Red() extends Color
  case class Black() extends Color
 
  sealed abstract class Tree
  case class Empty() extends Tree
  case class Node(color: Color, left: Tree, value: Int, right: Tree) extends Tree

  def size(t: Tree) : Int = t match {
    case Empty() => 0
    case Node(_, l, v, r) => size(l) + 1 + size(r)
  }

  def isBlack(t: Tree) : Boolean = t match {
    case Empty() => true
    case Node(Black(),_,_,_) => true
    case _ => false
  }

  def redNodesHaveBlackChildren(t: Tree) : Boolean = t match {
    case Empty() => true
    case Node(Black(), l, _, r) => redNodesHaveBlackChildren(l) && redNodesHaveBlackChildren(r)
    case Node(Red(), l, _, r) => isBlack(l) && isBlack(r) && redNodesHaveBlackChildren(l) && redNodesHaveBlackChildren(r)
  }

  def blackBalanced(t : Tree) : Boolean = t match {
    case Node(_,l,_,r) => blackBalanced(l) && blackBalanced(r) && blackHeight(l) == blackHeight(r)
    case Empty() => true
  }

  def blackHeight(t : Tree) : Int = t match {
    case Empty() => 1
    case Node(Black(), l, _, _) => blackHeight(l) + 1
    case Node(Red(), l, _, _) => blackHeight(l)
  }

  def chooseTree(height : Int) : Tree = {
    choose((anInt: Int, t: Tree) => blackBalanced(t) && redNodesHaveBlackChildren(t) && isBlack(t) && size(t) == height)._2
  }

  def main(args: Array[String]) : Unit = {

    /** Printing trees */
    def indent(s: String) = ("  "+s).split('\n').mkString("\n  ")

    def print(tree: Tree): String = tree match {
      case Node(c,l,v,r) =>
        indent(print(r)) + "\n" + (if (c == Black()) "B" else "R") + " " + v.toString + "\n" + indent(print(l))
      case Empty() => "E"
    }

    val height = if (args.isEmpty) 3 else args(0).toInt
    println("The chosen tree (of height " + height + ") is : \n" + print(chooseTree(height)))
  }
}
