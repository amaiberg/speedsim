package com.speedlab4
import _root_.android.app.{Activity, Dialog}
import _root_.android.view.View

case class TypedResource[T](id: Int)
case class TypedLayout(id: Int)

object TR {
  val strtBtn = TypedResource[android.widget.Button](R.id.strtBtn)
  val floating_button = TypedResource[android.widget.Button](R.id.floating_button)
  val main = TypedResource[android.widget.LinearLayout](R.id.main)
  val param_grid = TypedResource[android.widget.GridView](R.id.param_grid)
  val chartView = TypedResource[speedlab4.ChartView](R.id.chartView)
  val view_flipper = TypedResource[android.widget.ViewFlipper](R.id.view_flipper)
  val speedbar = TypedResource[android.widget.SeekBar](R.id.speedbar)
  val nxtBtn = TypedResource[android.widget.Button](R.id.nxtBtn)
  val popview = TypedResource[speedlab4.ui.LatticeView](R.id.popview)
 object layout {
  val decription_layout = TypedLayout(R.layout.decription_layout)
 val param_grid = TypedLayout(R.layout.param_grid)
 val quick_options = TypedLayout(R.layout.quick_options)
 val pop_layout = TypedLayout(R.layout.pop_layout)
 val chartview_layout = TypedLayout(R.layout.chartview_layout)
 val lattice_layout = TypedLayout(R.layout.lattice_layout)
 val lattice_flipper = TypedLayout(R.layout.lattice_flipper)
 val dummyview = TypedLayout(R.layout.dummyview)
 }
}
trait TypedViewHolder {
  def findViewById( id: Int ): View
  def findView[T](tr: TypedResource[T]) = findViewById(tr.id).asInstanceOf[T]
}
trait TypedView extends View with TypedViewHolder
trait TypedActivityHolder extends TypedViewHolder
trait TypedActivity extends Activity with TypedActivityHolder
trait TypedDialog extends Dialog with TypedViewHolder
object TypedResource {
  implicit def layout2int(l: TypedLayout) = l.id
  implicit def view2typed(v: View) = new TypedViewHolder { 
    def findViewById( id: Int ) = v.findViewById( id )
  }
  implicit def activity2typed(a: Activity) = new TypedViewHolder { 
    def findViewById( id: Int ) = a.findViewById( id )
  }
  implicit def dialog2typed(d: Dialog) = new TypedViewHolder { 
    def findViewById( id: Int ) = d.findViewById( id )
  }
}
