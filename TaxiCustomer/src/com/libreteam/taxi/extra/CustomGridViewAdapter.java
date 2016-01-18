package com.libreteam.taxi.extra;

import java.util.ArrayList;

import com.libreteam.taxi.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomGridViewAdapter extends ArrayAdapter<Item> {
	private Context context;
	private int layoutResourceId;
	private View row;
	private ArrayList<Item> data = new ArrayList<Item>();
    private static int width;
    private static ImageView img;
    
	public CustomGridViewAdapter(Context context, int layoutResourceId,
													ArrayList<Item> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		row = convertView;
		RecordHolder holder = null;
		if (row == null) 
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RecordHolder();
			holder.txtTitle = (TextView)row.findViewById(R.id.item_text);
			img = (ImageView) row.findViewById(R.id.item_image);
			img.getLayoutParams().height = (CustomGridViewAdapter.getWidth() - 140) /3;
			img.getLayoutParams().width = (CustomGridViewAdapter.getWidth() - 140) /3;
			holder.imageItem = img;
			row.setTag(holder);
		} else {
			holder = (RecordHolder)row.getTag();
		}

		Item item = data.get(position);
		holder.txtTitle.setText(item.getTitle());
		Typeface font = Typeface.createFromAsset(context.getAssets(), "pt_sans.ttf");		   
		holder.txtTitle.setTypeface(font);
		holder.imageItem.setImageBitmap(item.getImage());
		return row;
	}

	
	static class RecordHolder {
		TextView txtTitle;
		ImageView imageItem;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), 
										bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static int getWidth() {
		return width;
	}

	public  void setWidth(int width) {
		CustomGridViewAdapter.width = width;
	}
}