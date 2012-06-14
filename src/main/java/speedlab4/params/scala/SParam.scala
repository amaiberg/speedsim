package speedlab4.params.scala

import speedlab4.params.ParamDouble


/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 12/23/11
 * Time: 6:31 PM
 * To change this template use File | Settings | File Templates.
 */
class SParam(aname: String, avalue: Double, min: Double, max: Double) extends ParamDouble(aname: String, avalue: Double, min: Double, max: Double) {


  def +(par: SParam): Double = {
    value + par.value
  }

  def *(par: SParam): Double = {
    value * par.value
  }

  def /(par: SParam): Double = {
    value / par.value
  }

  def -(par: SParam): Double = {
    value - par.value
  }


  def +(par: Int): Double = {
    value + par
  }

  def *(par: Int): Double = {
    value * par
  }

  def /(par: Int): Double = {
    value / par
  }

  def -(par: Int): Double = {
    value - par
  }


  def toInt(): Int = value.toInt

  def toDouble(): Double = value

  description = ""
}

