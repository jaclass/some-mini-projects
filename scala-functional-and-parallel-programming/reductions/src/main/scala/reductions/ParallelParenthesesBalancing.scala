package reductions

import scala.annotation._
import org.scalameter._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime.value / fjtime.value}")
  }
}

object ParallelParenthesesBalancing extends ParallelParenthesesBalancingInterface {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  override def balance(chars: Array[Char]): Boolean = {
    def balanceRec(i: Int, opens: Int): Boolean = {
      if (i == chars.length) opens == 0
      else if (chars(i) == ')')
        if(opens == 0)  false
        else balanceRec(i+1, opens - 1)
      else if (chars(i) == '(')
        balanceRec(i + 1, opens + 1)
      else balanceRec(i + 1, opens)
    }
    balanceRec(0, 0)
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {
    /** consider the inputs: (   )   )   (   )   (
     *                               ----(1, 1)----
     *                            /                  \
     *                          /                     \
     *                 --  (1, 1) --                   \
     *              /                 \                 \
     *           (0, 0)            (1, 1)            (1, 1)
     *        /        \        /        \        /        \
     *     (0, 1)   (1, 0)   (1, 0)   (0, 1)   (1, 0)   (0, 1)
     */
    def traverse(idx: Int, until: Int, remainClosePares: Int, openPares: Int): (Int, Int)  = {
      if(idx == until)
        (remainClosePares, openPares)
      else if(chars(idx) == '(')
        traverse(idx+1, until, remainClosePares, openPares+1)
      else if(chars(idx) == ')')
        if(openPares <= 0) traverse(idx+1, until, remainClosePares+1, openPares)
        else traverse(idx+1, until, remainClosePares, openPares-1)
      else
        traverse(idx+1, until, remainClosePares, openPares)
    }

    def reduce(from: Int, until: Int): (Int, Int)  = {
      if(until - from <= threshold){
        traverse(from, until, 0, 0)
      }else{
        val mid = (from + until) / 2
        val (left, right) = parallel(reduce(from, mid), reduce(mid, until))
        (left._1 + Math.max(right._1 - left._2, 0), Math.max(left._2 - right._1, 0) + right._2)
      }
    }
    reduce(0, chars.length) == (0, 0)
  }


}
