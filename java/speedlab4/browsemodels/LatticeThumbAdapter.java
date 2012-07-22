package speedlab4.browsemodels;


import com.speedlab4.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class LatticeThumbAdapter extends PagerAdapter {
	
	protected Context mContext;
	protected LatticeThumb[] thumbs;
	protected LayoutInflater inflator;
	protected int screenSize, drawableSize, currentSelectedModel, thumbsPerPage;
	protected ViewSwitcher lastClickedView;
	protected Resources res;
	private static int[] viewIds = {R.id.thumb_1, R.id.thumb_2, R.id.thumb_3, R.id.thumb_4};
	
	public LatticeThumbAdapter(Context context, LatticeThumb[] thumbs, Resources r, int minScreenSize,
			boolean landscape, boolean large){
		this.mContext = context;
		this.thumbs = thumbs;
		this.inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.screenSize = minScreenSize;
		this.res = r;
		this.currentSelectedModel = R.id.vants; // default
		if (landscape){
			this.thumbsPerPage = 2;
			this.drawableSize = screenSize/2 - 10;
		}
		else if (large){
			this.thumbsPerPage = 4;
			this.drawableSize = screenSize/2 - 10;
		}
		else{
			this.thumbsPerPage = 1;
			this.drawableSize = screenSize - 60;
		}
	}

	@Override
	public int getCount() {
		if (thumbs.length % thumbsPerPage == 0)
			return thumbs.length/thumbsPerPage;
		else
			return thumbs.length/thumbsPerPage + 1;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position){
		
		View backThumb;
		TextView frontThumb, backThumbText;
		LatticeThumb currentThumb;
		Drawable d;
		View page = inflator.inflate(R.layout.lattice_thumb, null);
		
		for (int i=0; i<thumbsPerPage; i++){
			if ((i + thumbsPerPage*position) < thumbs.length){

				ViewSwitcher switcher = (ViewSwitcher) page.findViewById(viewIds[i]);
				
				// populate front of one thumb
				currentThumb = thumbs[i + thumbsPerPage*position];	
				frontThumb = (TextView) inflator.inflate(R.layout.one_thumb, null);							
				frontThumb.setText(currentThumb.description);
				d = res.getDrawable(currentThumb.imageResID);
				d.setBounds(0, 0, drawableSize, drawableSize);
				frontThumb.setCompoundDrawables(null, null, null, d);
				
				// populate back of one thumb
				backThumb = inflator.inflate(R.layout.one_thumb_back, null);
				backThumbText = (TextView) backThumb.findViewById(R.id.thumb_descrip);
				backThumbText.setText(currentThumb.fullDescrpID);
				
				// add views to switcher
				switcher.addView(frontThumb, 0, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				switcher.addView(backThumb, 1, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				// add listener
				switcher.setOnClickListener(new ThumbListener(currentThumb));
				
			}
		}
		
		// add view to container
		((ViewPager) container).addView(page);
	
		return page;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		( (ViewPager)container).removeView( (View)object);
	}
	
	public int getCurrentModel(){
		return currentSelectedModel;
	}
	
	public void closeLastDetailView(){
		lastClickedView.showNext();
	}
	
	class ThumbListener implements OnClickListener{		
		LatticeThumb mThumb;
		
		public ThumbListener(LatticeThumb thumb){
			mThumb = thumb;
		}

		public void onClick(View v) {
			currentSelectedModel = mThumb.modelID;
			// make sure previous clicked view is showing image
			if (lastClickedView != v && lastClickedView != null && lastClickedView.getDisplayedChild() == 1)
				lastClickedView.showNext();				
			((ViewSwitcher) v).showNext();
			lastClickedView = (ViewSwitcher)v;
		}
		
	}
	

}
