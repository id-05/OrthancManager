package com.example.orthancmanager;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Set;

public class HttpUserDialogPreference extends DialogPreference
{
    private String jsonStr;
    private EditText jsonEdit;




    public HttpUserDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // get attributes specified in XML
       //TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberPickerDialogPreference, 0, 0);
        //try
        //{
           // setMinValue(a.getInteger(R.styleable.NumberPickerDialogPreference_min, DEFAULT_MIN_VALUE));
           // setMaxValue(a.getInteger(R.styleable.NumberPickerDialogPreference_android_max, DEFAULT_MAX_VALUE));
        //}
        //finally
       // {
          //  a.recycle();
        //}

        // !!!!!!!!!!!!!!!!!!!!!в это месте заменили с R.layout.http_users_dialog
        setDialogLayoutResource(R.layout.http_users_dialog_recyclerview);
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

        //TextView dialogMessageText = (TextView) view.findViewById(R.id.text_dialog_message);
        //dialogMessageText.setText(getDialogMessage());

        //jsonEdit = (EditText) view.findViewById(R.id.jsontext);

        JsonParser parser = new JsonParser();
        JsonObject orthancJson=new JsonObject();
        orthancJson = parser.parse(jsonStr).getAsJsonObject();
        JsonObject buf = new JsonObject();
        buf = orthancJson.get("RegisteredUsers").getAsJsonObject();
        Set<String> keys = buf.keySet();
        Object[] jsonkeys = keys.toArray();
        try {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewHttp);
            //recyclerView.setHasFixedSize(true);
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            HttpUserDialogAdapter adapter;
            adapter = new HttpUserDialogAdapter(jsonStr, this.getContext());
            recyclerView.setAdapter(adapter);
        }catch (Exception e){
            MainActivity.print("onBindDialogView  "+e.toString());
        }
        MainActivity.print("onbind "+jsonkeys[0].toString()+":"+buf.get(jsonkeys[0].toString()).getAsString());
        //jsonEdit.setText(jsonkeys[0].toString());
    }

    public String getValue()
    {
        return jsonStr;
    }

    public void setValue(String value)
    {
        //value = Math.max(Math.min(value, mMaxValue), mMinValue);

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
            MainActivity.print("pltcm");
           // String jsonEditValue = jsonEdit.getText().toString();
           // if (callChangeListener(jsonEditValue))
            {
             //   setValue(jsonEditValue);
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
        //myState.minValue = getMinValue();
        //myState.maxValue = getMaxValue();
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
        //setMinValue(myState.minValue);
        //setMaxValue(myState.maxValue);
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
            MainActivity.print("pltcm");
            value = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            super.writeToParcel(dest, flags);

            //dest.writeInt(minValue);
            //dest.writeInt(maxValue);
            //
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
