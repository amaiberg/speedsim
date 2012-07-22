package speedlab4.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.speedlab4.R;
import speedlab4.model.AbstractSimModel;
import speedlab4.ui.listeners.DrawingListener;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

//TODO: Non-hacky fix for drawing first lattice instance without first variable.
//Probably due to onStart wiping the first screen.


public class LatticeView extends SurfaceView implements SurfaceHolder.Callback, Serializable {

    public float square_size;
    public BlockingQueue<double[][]> latticeBuffer = new ArrayBlockingQueue<double[][]>(1);
    private Paint p;
    private int startx, starty, width = 100, height = 100;
    private AbstractSimModel abstractSimModel;
    private int sizeFactor = 1;
    private SurfaceViewThread viewThread;
    private Bitmap backBuffer;
    private Canvas backCanvas;
    private double[][] currentMatrix;

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
        
        currentMatrix = new double[10][10];

        getHolder().addCallback(this);
        viewThread = new SurfaceViewThread(getHolder(), this);
    }

    public void setSimModel(AbstractSimModel abstractSimModel) {
        latticeBuffer.clear();
        this.abstractSimModel = abstractSimModel;
    }
    
    public AbstractSimModel getSimModel(){
    	return this.abstractSimModel;
    }

    public void setDims(int startx, int starty, int width, int height) {
        this.startx = startx;
        this.starty = starty;
        this.width = width;
        this.height = height;
    }

    /*
     * Tries to put the given matrix into the latticeBuffer queue.
     * Calling thread will block if the queue is full.
     */
    public void addMatrix(double[][] matrix) {

        try {
            latticeBuffer.put(matrix);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
 
    /*
     * Changes the value of the given cell in the underlying matrix
     * of the currently shown lattice
     */
    public void setCurrentMatrixCell(int x, int y, double val){
    	currentMatrix[x][y] = val;
    }
    
    /*
     * Sets the current underlying matrix of the lattice to the
     * given value.
     */
    public void setCurrentMatrix(double[][] matrix){
    	this.currentMatrix = matrix;
    }
    
    /*
     * Returns the matrix that corresponds to the lattice currently
     * displayed on the screen.
     */
    public double[][] getCurrentMatrix(){
    	return this.currentMatrix;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        this.startAnimation(this.getAnimation());
    }

    public void flush(){
       latticeBuffer.clear();
    }

    /*
     * Retrieves/removes the matrix at the head of the
     * latticeBuffer queue and draws it to backCanvas
     */
    public void drawNextOnCanvas(Canvas canvas) {
        double[][] lastMat = new double[10][10];
               try {
                    lastMat = latticeBuffer.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                drawLattice(canvas,lastMat);
    }
    
    /*
     * Draws the underlying matrix of the canvas that is 
     * currently displayed without taking a step in the model
     */
    public void drawCurrentOnCanvas(Canvas canvas){
    	drawLattice(canvas, currentMatrix);
    }

    private void drawLattice(Canvas canvas,double[][] lastMat) {
    	if (currentMatrix.length != lastMat.length)
    		currentMatrix = new double[lastMat.length][lastMat.length];
    	
        square_size = (float) width / (float) lastMat.length;
        
        for (float j = 0; j < lastMat.length; j++)
            for (float k = 0; k < lastMat.length; k++) {
            	// save lastMat in currentMatrix
            	currentMatrix[(int)j][(int)k] = lastMat[(int) j][(int) k];
            	// draw lastMat to canvas
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

        //this.setLayoutParams(new FrameLayout.LayoutParams(this.width, this.height));
        this.setLayoutParams(new LinearLayout.LayoutParams(this.width, this.height));

       // super.onMeasure(width, height);
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


    /*
     * Part of SurfaceHolder.Callback interface
     * Called by system when surface is first created
     */
    public void surfaceCreated(SurfaceHolder holder) {
    	if (viewThread == null)
    		viewThread = new SurfaceViewThread(getHolder(), this);
        viewThread.setRunning(true);
        if (!viewThread.isAlive())
        	viewThread.start();
    }


    /*
     * Part of SurfaceHolder.Callback interface
     * Called by stop(), which is called when the Activity's onStop() is called
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
    	if (viewThread != null){
    		boolean retry = true;
    		viewThread.setRunning(false);
    		viewThread.interrupt();
    		while (retry) {
    			try {
    				// Blocks this thread (main thread) until viewThread dies.
    				viewThread.join();
    				retry = false;
    			} catch (InterruptedException e) {
    				// we will try it again and again...
    			}
    		}
    		viewThread = null;
    	}
    }

    /*
     * Called when Activity's onStop() called
     */
    public void stop() {
        //surfaceDestroyed(getHolder());
        //viewThread.interrupt();
    	surfaceDestroyed(getHolder());
    }

    public void setRate(double rate){
        this.rate = rate;
    }

    public void pause(){
        viewThread.setDrawNext(false);
        viewThread.setDrawCurrent(false);
        maxframes =0;
    }
    
    public boolean isPaused(){
    	return (!viewThread.getDraw() || frames == maxframes);
    }

    /*
     * Called by modelController's resume(), which is called by Activity's
     * onResume() and by the 'Continue' button
     */
    public void resume(){
        maxframes =-1;
        viewThread.setDrawNext(true);
        viewThread.setDrawCurrent(false);
    }

    public void resume(int maxframes, boolean drawNext){
    	if (viewThread == null)
    		viewThread = new SurfaceViewThread(getHolder(), this);
    	if(maxframes != -1){
    		this.frames =0;
    		this.maxframes = maxframes;
    		viewThread.setDrawNext(drawNext);
    		viewThread.setDrawCurrent(!drawNext);
    	}
    }

    public void setHandler(Handler h){
    	viewThread.setHandler(h);
    }

    class SurfaceViewThread extends Thread {

        private final SurfaceHolder surfaceHolder;
        private LatticeView view;
        volatile private boolean run = false;
        //volatile private boolean draw = false;
        volatile private boolean drawNext = false;
        volatile private boolean drawCurrent = false;
        private Handler handler;///

        public SurfaceViewThread(SurfaceHolder surfaceHolder, LatticeView view) {
            super("Surface View thread");
            this.surfaceHolder = surfaceHolder;
            this.view = view;
        }

        public void setRunning(boolean val) {
        	run = val;
        }
        
        public void setDrawCurrent(boolean val){
        	drawCurrent = val;
        }
        
        public void setDrawNext(boolean val){
        	drawNext = val;
        }
        
        public boolean getDraw(){
        	return drawNext;
        }
        
        public void setHandler(Handler h){
        	this.handler = h;
        }

        public void run() {
            Canvas c;
            while (run) {               		
            	if((drawNext||drawCurrent) && (frames < maxframes || maxframes == -1)){ 
            		c = null;
            		frames++;
            		if (drawNext){           		
            			view.drawNextOnCanvas(backCanvas); // get matrix from queue and draw it on backBuffer
            			if (handler.getLooper() != null)
            				handler.sendEmptyMessage(1); // tells simThread its ok to step model
            		}
            		else view.drawCurrentOnCanvas(backCanvas); // draw current underlying matrix instead

            		try {
            			synchronized (surfaceHolder) {
            				// take bitmap from backBuffer and draw it onto Surface
            				c = surfaceHolder.lockCanvas();
            				c.drawBitmap(backBuffer, 0, 0, null);
            			}
            			Thread.sleep((int)(rate * 200d));

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
