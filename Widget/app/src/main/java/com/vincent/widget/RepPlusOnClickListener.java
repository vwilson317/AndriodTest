package com.vincent.widget;

import android.view.View;
import android.widget.TextView;

public class RepPlusOnClickListener implements View.OnClickListener{
    Integer currentRepCount;
    public RepPlusOnClickListener(int currentRepCount) {
        this.currentRepCount = currentRepCount;
    }

    @Override
    public void onClick(View v)
    {
        currentRepCount++;
        TextView currentRepAmountTextView = (TextView)v.findViewById(R.id.currentRepAmount);
        currentRepAmountTextView.setText(currentRepCount.toString());
    }
}
