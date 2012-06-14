/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/11/12
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
package speedlab4.model.scala

;

import scalala.tensor.dense.DenseMatrix
import org.achartengine.chart.{PointStyle, LineChart}
import org.achartengine.renderer.BasicStroke
import speedlab4._
import model.{AbstractAnalyzer, AbstractSimModel}
import params.scala.{SParamInteger, SParamDouble, SParam}
import params.{ParamBoolean, Param}
import ui.chart.ChartData
import speedlab4.model.{AbstractAnalyzer, AbstractSimModel}
import speedlab4.params.{Param, ParamBoolean}
import speedlab4.ui.chart.ChartData

abstract class ScAbstractSimModel(al: Int, sparams: Array[Param[_]]) extends AbstractSimModel[SParamInteger, SParamDouble](al: Int, sparams: Array[Param[_]]) {
  type DP = SParamDouble
  type IP = SParamInteger
  type BP = ParamBoolean


  var lattice: DenseMatrix[Double] = DenseMatrix.zeros[Double](al, al)

  var map: Map[Int, State] = getMap
  this.analyzer = new DummyAnalyzer()


  def getX(): Double;

  def getY(): Array[Double];

  @Override
  def getParam(value: Double, name: String): SParam = {
    new SParam(name, value, 0, 100);
  }

  def getMap: Map[Int, State];

  def get(name: String): Param[_] = {
    params.get(name)
  }

  def getDouble(name: String): SParamDouble = {
    dParams.get(name)
  }

  def getInteger(name: String): SParamInteger = {
    iParams.get(name)
  }

  /*
  @Override
  def getParamInteger(value: Int = 50, name: String, min: Int = 0, max: Int = 100): SParamInteger = {
    new SParamInteger(name, value, min, max)
  }
  */
  @Override
  def getParamInteger(value: Int = 50, name: String, min: Int = 0, max: Int = 100, description: String = "", reqRestart: Boolean = false): SParamInteger = {
    new SParamInteger(name, value, min, max, description, reqRestart)
  }

  @Override
  def getParamDouble(name: String, value: Double = .5, min: Double = 0d, max: Double = 1d, description: String = "", reqRestart: Boolean = false): SParamDouble = {
    new SParamDouble(name, value, min, max, description, reqRestart)
  }

  def first(): Array[Array[Double]] = {
    Array.tabulate[Double](al, al)((i: Int, j: Int) => lattice(i, j).toDouble)

  }

  class DummyAnalyzer extends AbstractAnalyzer {
    def getChartData: ChartData = {
      val arr: Iterable[State] = map.values
      val array: Array[State] = arr.toArray[State]
      android.util.Log.i("Map?", array.toString);
      val size: Int = array.length
      var colors: Array[Int] = Array.tabulate[Int](size)((i: Int) => array(i).color)

      val styles: Array[PointStyle] = Array.tabulate[PointStyle](size)(_ => PointStyle.X)

      val strokes: Array[BasicStroke] = Array.tabulate[BasicStroke](size)(_ => BasicStroke.SOLID)

      val types: Array[String] = Array.tabulate[String](size)(_ => LineChart.TYPE)

      val titles: Array[String] = Array.tabulate[String](size)((i: Int) => array(i).name)

      val chartData: ChartData = new ChartData("SIRS", "time", "% of cells", titles, colors, styles, strokes, types, array.length)
      return chartData
    }

    def getXPoint = getX()

    def getYPoint = getY()
  }

}
