package speedlab4.model.scala


import scalala.tensor.dense.DenseVector._
import scalala.operators.Implicits._
import scalala.tensor.dense.{DenseMatrix, DenseVector}
import scalanlp.stats.distributions._
import speedlab4.params.scala.ParamImplicits._

import scala.util.Random._
import android.graphics.Color

import scala.Array
import speedlab4.params.{ParamGroupDoubleUnity, Param}

class SIRS(al: Int = 100) extends ScAbstractSimModel(al, null) {
  // epidemiological parameters
  // epidemiological parameters
  //phi = 6.2  per-capita contact rate
  //mu = 1.5   per-capita recovery rate
  //L size of lattice
  // what proportion of infection attempts are long-distance
  //alpha = 0
  // how many sites to make infected initially
  //numI0 = 50
  def getMap = {
    Map[Int, State](0 -> new State("Recovered", Color.BLACK), 1 -> new State("Infected", Color.RED), 2 -> new State("Suceptible", Color.BLUE))
  }

  def this() = this(100)

  var (phi, mu, alpha, gamma, numI0) = (DP("Phi", 6.2, 1, 15), DP("Mu", 1.5, 0, 10), DP("Alpha", 0, 0, 1), DP("Gamma", 0.3, 0, 1), IP("numI0", 50, 0, 100))

  var n = latticeSize * latticeSize
  //Sp//set.seed(17)atial SIR epidemiological model
  // set the random number seed, to ensure reproducibility
  setSeed(17)
  //debug settings
  val isRecording = false

  // how much time to let go by before recording state of system
  //recordDeltaT = 0.05
  // how many record-saving updates to do before drawing graphics
  //displayEvery = 1
  // we'll grow our vectors by this many elements at a time
  //chunksize = 500
  val (recordDeltaT, chunkSize) = (0.05, 500)
  //Black, red, and blue (SIR)
  // offsets used for local interactions
  val xOffsets = Array(0, 0, -1, 1)
  val yOffsets = Array(1, -1, 0, 0)
  // keeps track of how long are state vectors currently are
  var vectorlengths = chunkSize
  // Create vectors
  var s, i, r, et = zeros[Double](chunkSize)
  // stateArray will contain three values:
  //   0=susceptible, 1=infectious, 2=recovered
  var stateArray = DenseMatrix.zeros[Double](latticeSize, latticeSize)
  lattice = stateArray
  // vectors of x and y coordinates of infectious individuals
  var ixv, iyv = zeros[Int](n.toInt)
  // vectors of x and y coordinates of resistant individuals
  var rxv, ryv = zeros[Int](n.toInt)
  var currentI = 0
  // how many individuals are currently infectious
  // set up the initial population of infectious individuals
  for (i <- 0 until numI0) {
    val x, y = (nextDouble() * latticeSize).toInt
    stateArray(x, y) = 1
    ixv(i) = x
    iyv(i) = y
    currentI += 1
  }
  //figure(1)
  // display image of initial lattice
  //image(stateArray)
  // Initialize our record-keeping vectors.
  i(0) = currentI
  s(0) = latticeSize * latticeSize - i(0)
  //println("I" + i )
  // initialize variables to be used in the core simulation
  // currentI was already set, above
  var (currentTime, currentS, currentR, maxTime) = (0d, s(0), 0, 15d)
  // Keeps track of the last time we recorded the state of the
  // system.  Start out with a big enough negative value to force
  // another update right away after the first event.
  var lastTimeRecorded = -2 * recordDeltaT

  var ind = 0


  def getColor(state: Int): Int = {
    state match {
      case 0 => Color.BLACK
      case 1 => Color.RED
      case _ => Color.BLUE
    }

  }

  def expandVectors() {
    vectorlengths *= 2 // Double vector for faster amortized run-time
    def copy = (i: Int, d: DenseVector[Double]) => (if (i < d.length - 1) d(i) else 0)
    et = tabulate[Double](vectorlengths)(copy(_, et))
    s = tabulate[Double](vectorlengths)(copy(_, s))
    i = tabulate[Double](vectorlengths)(copy(_, i))
    r = tabulate[Double](vectorlengths)(copy(_, r))
  }

  def next(time: Double): Array[Array[Double]] = {
    maxTime += time
    while (((currentTime < maxTime) && (currentR > 0 || currentI > 0))) {
      // while infectious individuals remain
      val rIcontacts = phi * currentI // total contact rate
      val rIR = mu * currentI // total recovery rate
      val rIresist = gamma * currentR
      val totalRate = rIcontacts + rIR + rIresist //+ rIresist // total rate of all events
      val rexp = Exponential(totalRate)
      // time of next event
      currentTime += rexp.draw
      // Choose coords of the infectious site producing the event
      // (all events originate at infectious sites).
      // random index into coord vectors:
      val coordInd = (nextDouble * currentI).toInt
      val rcoordInd = (nextDouble * currentR).toInt
      val x = ixv(coordInd)
      val y = iyv(coordInd)
      val xr = rxv(rcoordInd)
      val yr = ryv(rcoordInd)
      val eventProb = nextDouble()
      if (eventProb <= (rIcontacts / totalRate)) {
        // It's a contact / attempted infection.
        // Choose the target individual being contacted
        var otherx = 0
        var othery = 0

        if (nextDouble() < alpha) {
          // long-distance contact
          // choose random coords between 1 and L
          otherx = (nextDouble() * latticeSize).toInt
          othery = (nextDouble() * latticeSize).toInt
        } else {
          // local contact
          // choose a random index from 1 to 4
          val randInd = (nextDouble * 4).toInt
          // then use as index into the xoffsets and yoffsets vector,
          // with x and y to compute otherx and othery (w/ wraparound)
          otherx = x + xOffsets(randInd)
          othery = y + yOffsets(randInd)
          otherx = (otherx + latticeSize) % latticeSize
          othery = (othery + latticeSize) % latticeSize
        }

        if (stateArray(otherx, othery) == 0) {
          // if (random > (rIresist/totalRate)){
          // target susceptible
          // adjust numbers of individuals in the various states
          currentS -= 1
          currentI += 1
          // (don't need to change currentR)
          // modify stateArray to indicate site is infectious
          stateArray(otherx, othery) = 1 //}
          // add coordinates of newly-infected site to Ixv and Iyv
          ixv(currentI) = otherx
          iyv(currentI) = othery
        } // end "if".  If it wasn't true, we tried to infect a
        // non-susceptible, and so we don't need to do anything
      } else if (eventProb <= ((rIR + rIcontacts) / totalRate)) {
        // an infectious individual is recovering/dying
        // we've already chosen the x,y coords of the site recovering
        // modify stateArray to indicate site is recovered/removed
        stateArray(x, y) = 2
        // remove coordinates of newly-recovered site from Ixv
        // and Iyv, by copying the coordinates from the ends of the
        // vectors up to fill in the "hole"
        ixv(coordInd) = ixv(currentI)
        iyv(coordInd) = iyv(currentI)

        // adjust numbers of individuals in the various states
        // (don't need to change currentS)
        currentI -= 1
        currentR += 1
        //add cooridinates of newly recovered sites
        rxv(currentR) = x
        ryv(currentR) = y
      } else {
        //A recovered site becomes susceptible

        stateArray(xr, yr) = 0
        // remove coordinates of newly-suceptible site from rxv
        // and ryv, by copying the coordinates from the ends of the
        // vectors up to fill in the "hole"
        rxv(rcoordInd) = rxv(currentR)
        ryv(rcoordInd) = ryv(currentR)
        currentR -= 1
        currentS += 1
      }
      // if enough time has gone by, update record-keeping vectors
      // and possibly draw graphics
      if ((isRecording) && (currentTime - lastTimeRecorded) > recordDeltaT) {
        lastTimeRecorded = currentTime
        et(ind) = currentTime
        s(ind) = currentS
        i(ind) = currentI
        r(ind) = currentR
        println("S " + et(ind) + "=" + s(ind) + "stateArray:" + stateArray.data.filter(_ == 0).length)
        println("I " + et(ind) + "=" + i(ind))
        ind += 1
        if ((ind % displayEvery) == 0) {
          //  display
        }
        // if displayEvery isn't 0, and it's time to draw stuff

      }
      if (ind + 1 == vectorlengths && (isRecording)) {
        expandVectors()
      }
    }
    //TODO: Use consistent data type for all latticeBuffer
    Array.tabulate[Double](latticeSize, latticeSize)((i: Int, j: Int) => stateArray(i, j).toDouble)
  }

  def init() {
    latticeSize = getInteger(latticeSize.name)
    phi = getDouble(phi.name)
    mu = getDouble(mu.name)
    alpha = getDouble(alpha.name)
    gamma = getDouble(gamma.name)
    numI0 = getInteger(numI0.name)
    n = latticeSize * latticeSize
    stateArray = DenseMatrix.zeros[Double](latticeSize, latticeSize)
    lattice = stateArray
    ixv = zeros[Int](n.toInt)
    iyv = zeros[Int](n.toInt)
    rxv = zeros[Int](n.toInt)
    ryv = zeros[Int](n.toInt)
    currentI = 0
    for (i <- 0 until numI0) {
      val x, y = (nextDouble() * latticeSize).toInt
      stateArray(x, y) = 1
      ixv(i) = x
      iyv(i) = y
      currentI += 1
    }
    i(0) = currentI
    s(0) = latticeSize * latticeSize - i(0)
    ind = 0
  }

  def getX(): Double = currentTime

  def getY() = Array(currentS, currentI, currentR) :/ n
}
