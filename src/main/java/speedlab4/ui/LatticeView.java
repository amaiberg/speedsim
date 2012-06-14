package speedlab4.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import com.speedlab4.R;
import speedlab4.model.AbstractSimModel;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//TODO: Non-hacky fix for drawing first lattice instance without first variable.
//Probably due to onStart wiping the first screen.


public class LatticeView extends SurfaceView implements SurfaceHolder.Callback, Serializable {

    public float square_size;
    public BlockingQueue<double[][]> latticeBuffer = new ArrayBlockingQueue<double[][]>(5);
    private Paint p;
    private int startx, starty, width = 100, height = 100;
    private AbstractSimModel abstractSimModel;
    private int sizeFactor = 1;
    private SurfaceViewThread viewThread;
    private Bitmap backBuffer;
    private Canvas backCanvas;

    private double rate =0.5d;
    private int frames=0,maxframes=0;


    public LatticeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LatticeView);
        CharSequence s = a.getString(R.styleable.LatticeView_relative_size);
        //Toast.makeText(context,"relative size: " + s.toString(),Toast.LENGTH_SHORT);
        //  Log.e("s1","relative size: " + s.toString() );
        if (s != null)
            sizeFactor = Integer.valueOf(s.toString());

        getHolder().addCallback(this);
        viewThread = new SurfaceViewThread(getHolder(), this);

    }

    public void setSimModel(AbstractSimModel abstractSimModel) {
        latticeBuffer.clear();
        this.abstractSimModel = abstractSimModel;
    }


    public void setDims(int startx, int starty, int width, int height) {
        this.startx = startx;
        this.starty = starty;
        this.width = width;
        this.height = height;
    }

    public void addMatrix(double[][] matrix) {

        try {
            latticeBuffer.put(matrix);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        this.startAnimation(this.getAnimation());
    }

    public void flush(){
       latticeBuffer.clear();
    }

    //@Override
    public void drawOnCanvas(Canvas canvas) {
        double[][] lastMat = new double[10][10];
               try {
                    lastMat = latticeBuffer.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                drawLattice(canvas,lastMat);


    }

    public void drawLattice(Canvas canvas,double[][] lastMat) {

        square_size = (float) width / (float) lastMat.length;
        // Log.i("lastMat", lastMat.length + "");
        for (float j = 0; j < lastMat.length; j++)
            for (float k = 0; k < lastMat.length; k++) {
                int c = abstractSimModel.getColor((int) lastMat[(int) j][(int) k]);
                p.setColor(c);
                canvas.drawRect(startx + k * square_size, starty + j
                        * square_size, startx + k * square_size
                        + square_size, starty + j * square_size
                        + square_size, p);
            }
    }

    @Override
    public void onMeasure(int width, int height) {

        int parentWidth = MeasureSpec.getSize(width);
        int parentHeight = MeasureSpec.getSize(height);
        //Lattice size relative to the screen
        //Set Lattice screen size depending on screen orientation
        int myOrient = this.getResources().getConfiguration().orientation;

        if (myOrient == Configuration.ORIENTATION_LANDSCAPE) {
            //Landscape
            this.width = parentHeight;
            this.height = parentHeight;
            this.setMeasuredDimension(this.width, this.height);
        } else if (myOrient == Configuration.ORIENTATION_PORTRAIT) {
            //Portrait
            this.width = parentWidth / sizeFactor;
            this.height = parentWidth / sizeFactor;
            this.setMeasuredDimension(this.width, this.height);
        }

        this.setLayoutParams(new FrameLayout.LayoutParams(this.width, this.height));


        super.onMeasure(width, height);
        initializeBackBuffer();

    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right,
                         int bottom) {

        // int paddingTop,paddingBottom,paddingLeft,paddingRight;
        int width = right - left;
        int height = bottom - top;
        setDims(0, 0, width, height);
        super.onLayout(changed, left, top, right, bottom);

    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    public void surfaceCreated(SurfaceHolder holder) {
  //      viewThread.setRunning(true);
        viewThread.start();

    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        viewThread.setRunning(false);
        while (retry) {
            try {
                viewThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
    }

    public void stop() {

        surfaceDestroyed(getHolder());
        viewThread.interrupt();
    }

    public void setRate(double rate){
        this.rate = rate;
    }

    public void pause(){
        viewThread.setRunning(false);
        maxframes =0;
    }

    public void resume(){
        maxframes =-1;
        viewThread.setRunning(true);
    }

    public void resume(int maxframes){
        if(maxframes != -1){
            this.frames =0;
            this.maxframes = maxframes;
            viewThread.setRunning(true);
            }
        }



    class SurfaceViewThread extends Thread {

        private final SurfaceHolder surfaceHolder;
        private LatticeView view;
        private boolean run = false;

        public SurfaceViewThread(SurfaceHolder surfaceHolder, LatticeView view) {
            this.surfaceHolder = surfaceHolder;
            this.view = view;
        }

        public void setRunning(boolean val) {
            run = val;
        }

        public void run() {
            Canvas c;
            while (true) {
                if(run && (frames < maxframes || maxframes == -1)){
                    c = null;
                frames++;
                view.drawOnCanvas(backCanvas);

                try {
                    synchronized (surfaceHolder) {
                        c = surfaceHolder.lockCanvas();
                        c.drawBitmap(backBuffer, 0, 0, null);
                    }
                    Thread.sleep((int)(rate * 600d));

                } catch (InterruptedException e) {

                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                    else{

                //        try {
                       //     Thread.sleep(30);
                  //      } catch (InterruptedException e) {
                //            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    //    }
                    }
                }

                }
            }

        }
    }

    protected void initializeBackBuffer() {

        backBuffer = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);

        backCanvas = new Canvas(backBuffer);
    }




}
