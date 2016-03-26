package com.ananth.onboarding;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ananth.onboarding.fragments.FirstFragment;
import com.ananth.onboarding.fragments.SecondFragment;
import com.ananth.onboarding.fragments.ThirdFragment;
import com.ananth.onboarding.viewpager.CirclePageIndicator;

public class MainActivity extends FragmentActivity {
	View decorView;
	private CirclePageIndicator mIndicator;
	ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		decorView = getWindow().getDecorView();

		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		// decorView.setSystemUiVisibility(
		// View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		// | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		// | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
		// | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		// | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		setContentView(R.layout.activity_main);

		 pager = (ViewPager) findViewById(R.id.viewPager);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		// pager.setPageTransformer(false, new ZoomOutPageTransformer());
		pager.setPageTransformer(false, new DepthPageTransformer());
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(pager);

		mIndicator
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						Toast.makeText(MainActivity.this,
								"Changed to page " + position,
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
					}

					@Override
					public void onPageScrollStateChanged(int state) {
						
			
					}
				});
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			switch (pos) {

			case 0:
				return FirstFragment.newInstance("FirstFragment, Instance 1");
			case 1:
				return SecondFragment.newInstance("SecondFragment, Instance 1");
			case 2:
				return ThirdFragment.newInstance("ThirdFragment, Instance 1");
			default:
				return ThirdFragment.newInstance("ThirdFragment, Default");
			}
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	// @Override
	// public void transformPage(View view, float position) {
	// // TODO Auto-generated method stub
	//
	// view.setTranslationX(view.getWidth() * -position);
	//
	// if (position <= -1.0F || position >= 1.0F) {
	// view.setAlpha(0.0F);
	// } else if (position == 0.0F) {
	// view.setAlpha(1.0F);
	// } else {
	// // position is between -1.0F & 0.0F OR 0.0F & 1.0F
	// view.setAlpha(1.0F - Math.abs(position));
	// }
	//
	// }

	private static class FadePageTransformer implements
			PageTransformer {
		public void transformPage(View view, float position) {
			view.setAlpha(1 - Math.abs(position));
			if (position < 0) {
				view.setScrollX((int) ((float) (view.getWidth()) * position));
			} else if (position > 0) {
				view.setScrollX(-(int) ((float) (view.getWidth()) * -position));
			} else {
				view.setScrollX(0);
			}
		}
	}

	public class DepthPageTransformer implements PageTransformer {
		private static final float MIN_SCALE = 0.75f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 0) { // [-1,0]
				// Use the default slide transition when moving to the left page
				view.setAlpha(1);
				view.setTranslationX(0);
				view.setScaleX(1);
				view.setScaleY(1);

			} else if (position <= 1) { // (0,1]
				// Fade the page out.
				view.setAlpha(1 - position);

				// Counteract the default slide transition
				view.setTranslationX(pageWidth * -position);

				// Scale the page down (between MIN_SCALE and 1)
				float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
						* (1 - Math.abs(position));
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

	public class ZoomOutPageTransformer implements PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) { // [-1,1]
				// Modify the default slide transition to shrink the page as
				// well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
						/ (1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}
}
