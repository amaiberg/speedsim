package speedlab4.params.scala

import speedlab4.params.{ParamNumber, ParamDouble}


;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/27/12
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
class SParamDouble(name: String, value: Double, min: Double = 0.0d, max: Double = 1.0d, description: String="", reqRestart:Boolean=false) extends ParamDouble(name, value, min, max,description,reqRestart) {


  def +(par: ParamNumber[_ <: Number]): Double = {
    value + par.value.doubleValue()
  }

  def *(par: ParamNumber[_ <: Number]): Double = {
    value * par.value.doubleValue()
  }

  def /(par: ParamNumber[_ <: Number]): Double = {
    value / par.value.doubleValue()
  }

  def -(par: ParamNumber[_ <: Number]): Double = {
    value - par.value.doubleValue()
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

  //description = ""


}
