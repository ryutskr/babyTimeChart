package com.babytimechart.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.activity.babytimechart.R;
import com.babytimechart.ui.BabyTimeSettingMenuAdapter.MenuItemModel;

public class BabyTimeSettingMenuAdapter extends ArrayAdapter<MenuItemModel>{


	protected static final int TYPE_HEADER = 0;
	protected static final int TYPE_ROW = 1;

	public BabyTimeSettingMenuAdapter(Context context) {
		super(context, 0);
	}

	public void addHeader(int title){
		add( new MenuItemModel(-1, title, -1, true, -1) );
	}

	public void addItem(int id, int title, int title_explain, int color){
		add( new MenuItemModel(id, title, title_explain, false, color));
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).isHeader ? TYPE_HEADER : TYPE_ROW;
	}

	@Override
	public boolean isEnabled(int position) {
		return !getItem(position).isHeader;
	}
	//-----------------------------------------------------------------------------------------------

	public class ViewHolderRow {
		public final TextView titleHolder;
		public final TextView title_explainHolder;
		public final CalendarColorSquare squareHolder;

		public ViewHolderRow(TextView title, TextView title_explain, CalendarColorSquare squareHolder) {
			this.titleHolder = title;
			this.title_explainHolder = title_explain;
			this.squareHolder=squareHolder;
		}
	}

	public class ViewHolderHeader {

		public final TextView titleHolder;

		public ViewHolderHeader(TextView text1) {
			this.titleHolder = text1;
		}
	}

	//-----------------------------------------------------------------------------------------------

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		MenuItemModel item = getItem(position);
		switch (getItemViewType(position)) {
		case TYPE_HEADER:
			if( view == null ){
				view = LayoutInflater.from(getContext()).inflate(R.layout.activity_setting_listitem_header, null);

				TextView title = (TextView) view.findViewById(R.id.textView_header);
				title.setText(item.title);
				view.setTag(new ViewHolderHeader(title));
			}else{
				ViewHolderHeader viewHolderHeader = (ViewHolderHeader) view.getTag();
				viewHolderHeader.titleHolder.setText(item.title);
			}
			break;
		case TYPE_ROW:
			if( view == null ){
				view = LayoutInflater.from(getContext()).inflate(R.layout.activity_setting_listitem_row, null);

				TextView title = (TextView) view.findViewById(R.id.row_title);
				TextView title_explain = (TextView) view.findViewById(R.id.row_explain);
				CalendarColorSquare square = (CalendarColorSquare) view.findViewById(R.id.colorsquare);

				title.setText(item.title);
				title_explain.setText(item.title_explain);
				if( item.colorSquare != 0 )
					square.setBackgroundColor(item.colorSquare);
				else
					square.setVisibility(View.GONE);

				view.setTag(new ViewHolderRow(title, title_explain, square));
			}else{
				ViewHolderRow viewHolderRow = (ViewHolderRow) view.getTag();
				viewHolderRow.titleHolder.setText(item.title);
				viewHolderRow.title_explainHolder.setText(item.title_explain);

				if( item.colorSquare != 0 )
					viewHolderRow.squareHolder.setBackgroundColor(item.colorSquare);
				else
					viewHolderRow.squareHolder.setVisibility(View.GONE);

			}
			break;
		}

		return view;		
	}
	
	public class MenuItemModel{
		public int _id;
		public int title;
		public int title_explain;
		public boolean isHeader;	 
		public int colorSquare;


		public MenuItemModel( int id, int title, int title_explain, boolean header, int colorSquare) {
			this._id =id;
			this.title = title;
			this.title_explain = title_explain; 
			this.isHeader = header;	
			this.colorSquare = colorSquare;
		}
	}
	
}
