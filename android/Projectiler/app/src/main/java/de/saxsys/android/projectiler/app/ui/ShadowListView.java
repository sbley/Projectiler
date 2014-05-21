package de.saxsys.android.projectiler.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.ListView;

import de.saxsys.android.projectiler.app.R;


public class ShadowListView extends ListView {

	public ShadowListView(final Context context) {
		super(context);
	}

	public ShadowListView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public ShadowListView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	@ExportedProperty(category = "drawing")
	public int getSolidColor() {
		super.getSolidColor();

		return getContext().getResources().getColor(R.color.Grey);
	}
}
