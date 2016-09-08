package br.com.android.estudos.sunshineapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Dustin on 08/09/2016.
 */
public class LocationEditTextPreference extends EditTextPreference implements TextWatcher {
    private static final int DEFAULT_MINIMUM_LOCATION_LENGTH = 3;
    private final int mMinLength;
    private Button mPositiveButton;

    public LocationEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LocationEditTextPreference,
                0, 0);

        try {
            mMinLength = typedArray.getInteger(R.styleable.LocationEditTextPreference_minLength,
                    DEFAULT_MINIMUM_LOCATION_LENGTH);
        } finally {
            typedArray.recycle();
        }

    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        Dialog dialog = this.getDialog();
        if ( dialog instanceof AlertDialog ) {
            AlertDialog alertDialog = (AlertDialog) dialog;
            mPositiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

            this.getEditText().addTextChangedListener(this);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mPositiveButton.setEnabled( s.length() >= mMinLength );
    }
}
