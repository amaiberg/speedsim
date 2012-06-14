package speedlab4.model.scala

import speedlab4.params.Param
import scalala.operators.Implicits._
import scalala.tensor.dense.{DenseMatrix, DenseVector}
import math._
import speedlab4.params.scala.ParamImplicits._
import speedlab4.params.scala.ParamImplicits
import scalala.tensor.dense.DenseMatrix._
import android.graphics.Color
import collection.immutable.Stream.Empty


/**
 * Speedlab 4(9.1): Avner Maiberg
 * FrogAbstractSim simulate movement of frogs in
 * a lattice with a fixed carrying capacity
 * per cell.
 */
class FrogAbstractSim(size: Int = 100) extends
ScAbstractSimModel(size, null) {

  map = Map(0 -> new State("Frogs", Color.GREEN), 1 -> new State("Empty", Color.BLACK))
  var (pTurn, kBad, kGood, pGood, numFrogs, maxSteps) = (DP("pTurn", 0.1, 0d, 1d), IP("kBad", 10, 0, 50), IP("kGood", 2, 1, 10), DP("pGood", 0.1, 0, 1), IP("numFrogs", 500, 1, 1000), 1000d)
  //delta-x and delta-y offsets for up-right-down-left
  var time: Double = 0
  val (xoffsets, yoffsets) = (Array(0, 1, 0, -1), Array(1, 0, -1, 0))
  var numDirections = xoffsets.length
  // Initial box to put frogs in.
  // Let x and y range both be center +/- 10
  var (ixbox1, ixbox2), (iybox1, iybox2) = (latticeSize / 2 - 10, latticeSize / 2 + 10)
  var (dxbox, dybox) = (ixbox2 - ixbox1 + 1, // width of initial box
    iybox2 - iybox1 + 1)
  // height of initial box
  // choose x randomly between ixbox1 and ixbox2 inclusive
  // choose y randomly between iybox1 and iybox2 inclusive
  // note that x and y should both be vectors of length "numFrogs"
  var x = (DenseVector.rand(numFrogs) * (dxbox + 1)).map(floor) + (ixbox1)
  var y = (DenseVector.rand(numFrogs) * (dxbox + 1)).map(floor) + (ixbox1)
  // Construct vector of values, each in the range 1..numDirections
  var dirs = (DenseVector.rand(numFrogs) * (numDirections)).map(floor)
  // make proportion "pGood" of the sites in K have value KGood
  // and the rest have value KBad
  var m = rand(latticeSize, latticeSize)
  var k = (kGood - kBad) :* (m :< pGood.toDouble()) :+ kBad.toDouble

  var frogsPerSite = zeros[Double](latticeSize, latticeSize)

  def getMap = {
    Map[Int, State](0 -> new State("Frogs", Color.GREEN), 1 -> new State("Empty", Color.BLACK))
  }

  for (i <- 0 until numFrogs)
    frogsPerSite(x(i).toInt, y(i).toInt) += 1


  //converts DenseMatrix to regular Java double[][]
  def matrix2array(m: DenseMatrix[Double]): Array[Array[Double]] = {
    Array.tabulate[Double](m.numCols, m.numRows)((x1: Int, y1: Int) => m(x1, y1))
  }

  def getColor(state: Int): Int = {
    if (state == 0) Color.BLACK else Color.GREEN;
  }

  //Returns the result simulating maxSteps of the model.
  def next(maxSteps: Double): Array[Array[Double]] = {
    time += maxSteps
    for (s <- 0 until maxSteps.toInt + 1) {
      // simulate given number of time steps
      // do frogs in random order each time step
      var sample = List.tabulate(numFrogs)((x1: Int) => x1)
      sample = util.Random.shuffle(sample)
      for (d <- sample) {
        // use current x, current direction, xoffsets array
        var newx = x(d) + xoffsets(dirs(d).toInt)
        // use current y, current direction, yoffsets array
        var newy = y(d) + yoffsets(dirs(d).toInt)
        newx = if (newx < 0) latticeSize - 1 else newx % latticeSize // perform wraparound on newx
        newy = if (newy < 0) latticeSize - 1 else newy % latticeSize // perform wraparound on newy
        // if there aren 't already too many frogs there, move
        if (frogsPerSite(newx.toInt, newy.toInt) < k(newx.toInt, newy.toInt)) {
          // there aren 't too many frogs at target site, so move frog:
          // 1.adjust frogsPerSite at new site

          frogsPerSite(newx.toInt, newy.toInt) += 1
          // 2.adjust frogsPerSite at old site
          frogsPerSite(x(d).toInt, y(d).toInt) -= 1
          // 3.update x and y coords of the frog to newx and newy
          x(d) = newx
          y(d) = newy
        } else {
          // too many frogs at target site, don 't move.
          // But pick new direction for next time
          dirs(d) = floor(random * (numDirections))
        }
        // with probability "pTurn", choose a new random direction
        dirs(d) = if (random < pTurn) floor(random * (numDirections)) else dirs(d)
      }
    }
    return matrix2array(frogsPerSite)
  }

  def getX(): Double = time;

  def getY(): Array[Double] = Array[Double](numFrogs.toDouble, latticeSize * latticeSize - numFrogs) :/ (latticeSize * latticeSize)

  def init() {
    latticeSize = getInteger("Lattice Size")
    numFrogs = getInteger("numFrogs")
    pGood = getDouble("pGood")
    kGood = getInteger("kGood")
    kBad = getInteger("kBad")
    pTurn = getDouble("pTurn")
    //delta-x and delta-y offsets for up-right-down-left
    // Initial box to put frogs in.
    // Let x and y range both be center +/- 10
    ixbox1 = latticeSize / 2 - 10
    ixbox2 = latticeSize / 2 + 10
    iybox1 = latticeSize / 2 - 10
    iybox2 = latticeSize / 2 + 10
    dxbox = ixbox2 - ixbox1 + 1
    dybox = iybox2 - iybox1 + 1 // width of initial box
    // height of initial box
    // choose x randomly between ixbox1 and ixbox2 inclusive
    // choose y randomly between iybox1 and iybox2 inclusive
    // note that x and y should both be vectors of length "numFrogs"
    x = (DenseVector.rand(numFrogs) * (dxbox + 1)).map(floor) + (ixbox1)
    y = (DenseVector.rand(numFrogs) * (dxbox + 1)).map(floor) + (ixbox1)
    // Construct vector of values, each in the range 1..numDirections
    dirs = (DenseVector.rand(numFrogs) * (numDirections)).map(floor)
    // make proportion "pGood" of the sites in K have value KGood
    // and the rest have value KBad
    m = rand(latticeSize, latticeSize)
    k = (kGood - kBad) :* (m :< pGood.toDouble) :+ kBad.toDouble
    frogsPerSite = zeros[Double](latticeSize, latticeSize)
    //   lattice = frogsPerSite
    for (i <- 0 until numFrogs)
      frogsPerSite(x(i).toInt % numFrogs, y(i).toInt % numFrogs) += 1


  }
}
