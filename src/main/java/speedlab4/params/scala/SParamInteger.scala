package speedlab4.params.scala

import scala.Predef._
import speedlab4.params.ParamInteger
;

class SParamInteger(name: String, value: Int, min: Int, max: Int,description:String="",reqRestart:Boolean=false) extends ParamInteger(name, value, min, max,description,reqRestart) {

  def +(par: ParamInteger): Int = {
    value + par.value.intValue()
  }

  def *(par: ParamInteger): Int = {
    value * par.value.intValue()
  }

  def /(par: ParamInteger): Int = {
    value / par.value.intValue()
  }

  def -(par: ParamInteger): Int = {
    value - par.value.intValue()
  }


  def +(par: Int): Int = {
    value + par
  }

  def *(par: Int): Int = {
    value * par
  }

  def /(par: Int): Int = {
    value / par
  }

  def -(par: Int): Int = {
    value - par
  }

}