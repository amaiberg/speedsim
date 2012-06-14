package speedlab4.model.scala

import org.achartengine.chart.PointStyle
import org.achartengine.renderer.BasicStroke


class State(aname: String, acolor: Int) {
  val name: String = aname;
  val color: Int = acolor;

}

object Implicits {
  implicit def state2name(state: State): String = {
    state.name

  }

  implicit def state2color(state: State): Int = {
    state.color
  }

  implicit def state2pointstyle(state: State): PointStyle = {
    PointStyle.X
  }

  implicit def state2stroke(state: State): BasicStroke = {
    BasicStroke.SOLID
  }


}