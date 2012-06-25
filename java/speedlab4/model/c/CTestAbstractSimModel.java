package speedlab4.model.c;


/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 3/17/12
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.util.Log;
import com.speedlab4.R;
import speedlab4.model.java.JAbstractSimModel;
import speedlab4.params.Param;

import java.io.FileDescriptor;


public class CTestAbstractSimModel extends JAbstractSimModel {
    private boolean first = true;
    private Context mContext;

    //  private double[][] lattice = new double[100][100];
    public CTestAbstractSimModel(int size, Context c, Param... params) {
        super(size, params);
        System.loadLibrary("tools");
        System.loadLibrary("worm_simulation");
        System.loadLibrary("test-sim");
        mContext = c;
    }

    @Override
    public int getColor(int state) {
        if (state < 0)
            return Color.GRAY;
        if (state == 0)
            return Color.rgb(0, 0, 64);
        if (state < 100)
            return Color.rgb(0, (int) ((state) * 2.55), 255);
        if (state <= 200)
            return Color.rgb(255, (int) ((state - 100) * 2.55), 0);
        else return Color.rgb(0, 0, 80);
    }

    @Override
    public double[][] next(double time) {
        if (first == true) {
            init();
            first = false;
        }
        double[][] tmp;
        tmp = next();
        // Log.i("", "carray:");
        //  Log.i("",  print_array(tmp));
        return tmp;
    }

    @Override
    protected void init() {
//        AssetFileDescriptor r = mContext.getResources().openRawResourceFd(R.raw.land1);
//        if (r != null) {
//            FileDescriptor fd = r.getFileDescriptor();
//            long off = r.getStartOffset();
//            long len = r.getLength();
//            int check = initsim(this.getSize(), fd, off, len);
//            if (check == 1) {
//                Log.i("!", "C access failed");
//            }
//        } else Log.i("!", "access failed");

        //   if(check !=0) throw new IllegalAccessError("sdffs");
    }

    @Override
    public double[][] first() {
        return new double[this.getSize()][this.getSize()];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public native double[][] next();

    public native int initsim(int size, FileDescriptor fd, long off, long len);

    private static String print_array(double[][] carray) {
        String ret = "[";
        for (int i = 0; i < carray.length; i++) {
            ret += "[";
            for (int j = 0; j < carray.length; j++)
                ret += carray[i][j] + ((j < carray.length) ? "," : "]\n");
        }
        ret += "]";
        return ret;
    }

}
