package speedlab4.params.scala


object ParamImplicits {


  implicit def dparam2double(par: SParamDouble): Double = {
    par.value.doubleValue()
  }


  implicit def iparam2int(par: SParamInteger): Int = {
    par.value.intValue()
  }


}