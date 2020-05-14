package com.example.orthancmanager.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;

import com.example.orthancmanager.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class TransferSyntaxDialogPreference extends DialogPreference
{
    private String jsonStr;
    boolean DeflatedTransferSyntaxAccepted;
    boolean JpegTransferSyntaxAccepted;
    boolean Jpeg2000TransferSyntaxAccepted;
    boolean JpegLosslessTransferSyntaxAccepted;
    boolean JpipTransferSyntaxAccepted;
    boolean Mpeg2TransferSyntaxAccepted;
    boolean RleTransferSyntaxAccepted;
    Switch DeflatedTransfer;
    Switch JpegTransfer;
    Switch Jpeg2000Transfer;
    Switch JpegLosslessTransfer;
    Switch JpipTransfer;
    Switch Mpeg2Transfer;
    Switch RleTransfer;




    public TransferSyntaxDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setDialogLayoutResource(R.layout.transfer_syntax);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        setValue(restore ? getPersistedString("default") : (String) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return a.getString(index);
    }

    @Override
    protected void onBindDialogView(View view)
    {
        super.onBindDialogView(view);

        JsonParser parser = new JsonParser();
        JsonObject orthancJson=new JsonObject();
        orthancJson = parser.parse(jsonStr).getAsJsonObject();
        if (orthancJson.has("DeflatedTransfer")) DeflatedTransferSyntaxAccepted=orthancJson.get("DeflatedTransfer").getAsBoolean();
        if (orthancJson.has("JpegTransfer")) JpegTransferSyntaxAccepted=orthancJson.get("JpegTransfer").getAsBoolean();
        if (orthancJson.has("Jpeg2000Transfer")) Jpeg2000TransferSyntaxAccepted=orthancJson.get("Jpeg2000Transfer").getAsBoolean();
        if (orthancJson.has("JpegLosslessTransfer")) JpegLosslessTransferSyntaxAccepted=orthancJson.get("JpegLosslessTransfer").getAsBoolean();
        if (orthancJson.has("JpipTransfer")) JpipTransferSyntaxAccepted=orthancJson.get("JpipTransfer").getAsBoolean();
        if (orthancJson.has("Mpeg2Transfer")) Mpeg2TransferSyntaxAccepted=orthancJson.get("Mpeg2Transfer").getAsBoolean();
        if (orthancJson.has("RleTransfer")) RleTransferSyntaxAccepted=orthancJson.get("RleTransfer").getAsBoolean();

        DeflatedTransfer = (Switch)view.findViewById(R.id.DeflatedTransfer);
        JpegTransfer = (Switch)view.findViewById(R.id.JpegTransfer);
        Jpeg2000Transfer = (Switch)view.findViewById(R.id.Jpeg2000Transfer);
        JpegLosslessTransfer = (Switch)view.findViewById(R.id.JpegLosslessTransfer);
        JpipTransfer = (Switch)view.findViewById(R.id.JpipTransfer);
        Mpeg2Transfer = (Switch)view.findViewById(R.id.Mpeg2Transfer);
        RleTransfer = (Switch)view.findViewById(R.id.RleTransfer);

        DeflatedTransfer.setChecked(DeflatedTransferSyntaxAccepted);
        JpegTransfer.setChecked(JpegTransferSyntaxAccepted);
        Jpeg2000Transfer.setChecked(Jpeg2000TransferSyntaxAccepted);
        JpegLosslessTransfer.setChecked(JpegLosslessTransferSyntaxAccepted);
        JpipTransfer.setChecked(JpipTransferSyntaxAccepted);
        Mpeg2Transfer.setChecked(Mpeg2TransferSyntaxAccepted);
        RleTransfer.setChecked(RleTransferSyntaxAccepted);
    }

    public String getValue()
    {
        return jsonStr;
    }

    public void setValue(String value)
    {
        //if (value != mValue)
        // {
        jsonStr = value;
        persistString(value);
        notifyChanged();
        //}
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        // when the user selects "OK", persist the new value
        if (positiveResult)
        {
            //MainActivity.print("pltcm");
            // String jsonEditValue = jsonEdit.getText().toString();
            // if (callChangeListener(jsonEditValue))
            {
                JsonObject transferSyntax = new JsonObject();
                transferSyntax.addProperty("DeflatedTransfer",DeflatedTransfer.isChecked());
                transferSyntax.addProperty("JpegTransfer",JpegTransfer.isChecked());
                transferSyntax.addProperty("Jpeg2000Transfer",Jpeg2000Transfer.isChecked());
                transferSyntax.addProperty("JpegLosslessTransfer",JpegLosslessTransfer.isChecked());
                transferSyntax.addProperty("JpipTransfer",JpipTransfer.isChecked());
                transferSyntax.addProperty("Mpeg2Transfer",Mpeg2Transfer.isChecked());
                transferSyntax.addProperty("RleTransfer",RleTransfer.isChecked());
                setValue(transferSyntax.toString());
                //MainActivity.print(transferSyntax.toString());
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        // save the instance state so that it will survive screen orientation changes and other events that may temporarily destroy it
        final Parcelable superState = super.onSaveInstanceState();
        // set the state's value with the class member that holds current setting value
        final SavedState myState = new SavedState(superState);
        myState.value = getValue();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        // check whether we saved the state in onSaveInstanceState()
        if (state == null || !state.getClass().equals(SavedState.class))
        {
            // didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // restore the state
        SavedState myState = (SavedState) state;
        setValue(myState.value);

        super.onRestoreInstanceState(myState.getSuperState());
    }

    private static class SavedState extends BaseSavedState
    {
        String value;

        public SavedState(Parcelable superState)
        {
            super(superState);
        }

        public SavedState(Parcel source)
        {
            super(source);
            value = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            super.writeToParcel(dest, flags);

            dest.writeString(value);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
        {
            @Override
            public SavedState createFromParcel(Parcel in)
            {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size)
            {
                return new SavedState[size];
            }
        };
    }
}