package com.ktv.views;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.ktv.R;
import com.ktv.tools.DensityUtil;
import com.ktv.tools.Logger;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.fragments.dialogFragment.FragmentDialog1;
import com.ktv.ui.fragments.dialogFragment.FragmentDialog2;
import com.ktv.ui.fragments.dialogFragment.FragmentDialog3;
import com.ktv.ui.fragments.dialogFragment.FragmentDialog4;

public class MyDialogFragment extends DialogFragment implements View.OnClickListener{

	private static final String TAG="MyDialogFragment";

	private View strView;

	Context mContext;

	private RadioButton radioMenu1;
	private RadioButton radioMenu2;
	private RadioButton radioMenu3;
	private RadioButton radioMenu4;

	private boolean PoDisplay=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL,R.style.CustomDialog);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		dialogPosition();
		strView = inflater.inflate(R.layout.activity_main_popuwindos, container);
		mContext=getActivity();

		getIntentData();
		initView();
		return strView;
	}

	/**
	 * Bundle传值
	 */
	private void getIntentData(){
		PoDisplay= getArguments().getBoolean("PoDisplay");
		Logger.i(TAG,"PoDisplay..."+PoDisplay);
	}

	/**
	 * diaog的相关属性设置
	 */
	private void dialogPosition(){
		Window window = getDialog().getWindow();

		Drawable drawable = getResources().getDrawable(R.mipmap.popuwinds_main);//获取drawable
		window.setBackgroundDrawable(drawable);

		final DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
		layoutParams.width = dm.widthPixels/2;//宽占屏幕一半
		layoutParams.height = dm.heightPixels/2;//高占屏幕一半
		layoutParams.gravity = Gravity.TOP;
		layoutParams.y = DensityUtil.dip2px(getActivity(), 70);//距离顶部有48dp的距离
		getDialog().getWindow().setAttributes(layoutParams);

		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					if (getChildFragmentManager().getBackStackEntryCount() > 0) {// 回退栈的数量
						getChildFragmentManager().popBackStack();
						return true;
					}
				}
				return false;
			}
		});
	}

	private void initView(){
		radioMenu1 = strView.findViewById(R.id.rdb1_top_menu);
		radioMenu2 = strView.findViewById(R.id.rdb2_top_menu);
		radioMenu3 = strView.findViewById(R.id.rdb3_top_menu);
		radioMenu4 = strView.findViewById(R.id.rdb4_top_menu);

		radioMenu1.setOnClickListener(this);
		radioMenu2.setOnClickListener(this);
		radioMenu3.setOnClickListener(this);
		radioMenu4.setOnClickListener(this);

		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (PoDisplay){
			transaction.add(R.id.main_popudows, new FragmentDialog3());
			radioMenu3.requestFocus();
		} else {
			transaction.add(R.id.main_popudows, new FragmentDialog2());
			radioMenu2.requestFocus();
		}
		transaction.commit();
	}

	@Override
	public void onClick(View v) {
		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		for (int i = 0; i < getChildFragmentManager().getBackStackEntryCount() - 1; i++) {
			getChildFragmentManager().popBackStack();
		}
		switch (v.getId()) {
			case R.id.rdb1_top_menu:
				transaction.replace(R.id.main_popudows, new FragmentDialog1());
				break;
			case R.id.rdb2_top_menu:
				transaction.replace(R.id.main_popudows, new FragmentDialog2());
				break;
			case R.id.rdb3_top_menu:
				transaction.replace(R.id.main_popudows, new FragmentDialog3());
				break;
			case R.id.rdb4_top_menu:
				transaction.replace(R.id.main_popudows, new FragmentDialog4());
				break;

			default:
				break;
		}
//		transaction.addToBackStack(null);
		transaction.commit();
	}
}
