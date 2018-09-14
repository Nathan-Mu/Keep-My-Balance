package edu.monash.fit4039.keepmybalance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

//resources: https://github.com/bendemboski/DateSlider
//resources: https://developer.android.com/reference/android/widget/DatePicker.html
//resources: https://github.com/developersuru/android-month-year-picker
public class MonthYearDatePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private final DatePicker mDatePicker;
    private final OnDateSetListener mDateSetListener;

    public MonthYearDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }

    //create the date picker
    public MonthYearDatePickerDialog(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear,
                            int dayOfMonth) {
        super(context, theme);

        mDateSetListener = listener;

        Context themeContext = getContext();
        //set text of buttons
        setButton(BUTTON_POSITIVE, "OK", this);
        setButton(BUTTON_NEGATIVE, "Cancel", this);
        setIcon(0);

        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_month_year_picker, null);
        setView(view);
        mDatePicker = (DatePicker) view.findViewById(R.id.month_year_picker);
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);

        //only show month and year
        hideDay(mDatePicker);
    }

    //only show the month and year
    private void hideDay(DatePicker mDatePicker) {
        try {
            //hide day
            int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
            if (daySpinnerId != 0) {
                View daySpinner = mDatePicker.findViewById(daySpinnerId);
                if (daySpinner != null) {
                    daySpinner.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //when user click the button (OnClickListener interface)
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mDateSetListener != null) {
                    mDatePicker.clearFocus();
                    mDateSetListener.onDateSet(mDatePicker, mDatePicker.getYear(), mDatePicker.getMonth(),
                            mDatePicker.getDayOfMonth());
                }
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        if (view.getId() == R.id.month_year_picker)
            mDatePicker.init(year, month, day, this);
    }

    public void updateDate(int year, int month, int dayOfMonth) {
        mDatePicker.updateDate(year, month, dayOfMonth);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDatePicker.getYear());
        state.putInt(MONTH, mDatePicker.getMonth());
        state.putInt(DAY, mDatePicker.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int start_year = savedInstanceState.getInt(YEAR);
        int start_month = savedInstanceState.getInt(MONTH);
        int start_day = savedInstanceState.getInt(DAY);
        mDatePicker.init(start_year, start_month, start_day, this);

    }

    public interface OnDateSetListener {
        void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth);
    }
}