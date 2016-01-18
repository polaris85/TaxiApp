package com.libreteam.taxi.extra;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/** Customizing AutoCompleteTextView to return Place Description   
 *  corresponding to the selected item
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {
	
	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** Returns the place description corresponding to the selected item */
	@SuppressWarnings("unchecked")
	@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
		StringBuilder address = new StringBuilder();
		for(int i = 0; i< hm.get("description").split(" ").length - 1; i++)
			address.append(hm.get("description").split(" ")[i]).append(" ");
		
		CharSequence strAddress;
		if(address.length()>=2)
			strAddress = address.toString().substring(0, address.length()-2);
		else
			strAddress = address.toString().substring(0, address.length());
		return strAddress;
	}
}
