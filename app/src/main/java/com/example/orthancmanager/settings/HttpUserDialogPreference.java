package com.example.orthancmanager.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orthancmanager.MainActivity;
import com.example.orthancmanager.R;
import com.example.orthancmanager.settings.HttpUserDialogAdapter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Set;

public class HttpUserDialogPreference extends DialogPreference
{
    private String jsonStr;
    private EditText jsonEdit;
    static ArrayList<String> bufLogin = new ArrayList<String>();
    static ArrayList<String> bufPassword = new ArrayList<String>();



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
        // !!!!!!!!!!!!!!!!!!!!!в это месте заменили с
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
        final EditText editLogin = (EditText)view.findViewById(R.id.addLogin);
        final EditText editPassword = (EditText)view.findViewById(R.id.addPassword);
        ImageView addItem = (ImageView)view.findViewById(R.id.addItem);

        JsonParser parser = new JsonParser();
        JsonObject orthancJson=new JsonObject();
        orthancJson = parser.parse(jsonStr).getAsJsonObject();
        Set<String> keys = orthancJson.keySet();
        Object[] jsonkeys = keys.toArray();
        bufLogin.clear();
        bufPassword.clear();
        for(int i=0; i<=jsonkeys.length-1; i++){
            bufLogin.add(jsonkeys[i].toString());
            bufPassword.add(orthancJson.get(jsonkeys[i].toString()).getAsString());
        }
        try {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewHttp);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            final HttpUserDialogAdapter adapter;
            adapter = new HttpUserDialogAdapter(bufLogin, bufPassword, this.getContext());
            recyclerView.setAdapter(adapter);

            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!(editLogin.getText().toString().equals(""))&
                            (!editPassword.getText().toString().equals("")))
                    {
                        bufLogin.add(editLogin.getText().toString());
                        bufPassword.add(editPassword.getText().toString());
                        adapter.notifyDataSetChanged();
                        editLogin.setText("");
                        editPassword.setText("");
                    }
                }
            });

        }catch (Exception e){
            MainActivity.print("onBindDialogView  "+e.toString());
        }
    }

    public static void delItem(int i){
        bufLogin.remove(i);
        bufPassword.remove(i);
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
                JsonObject jsonObj = new JsonObject();
                for(int i=0; i<=bufLogin.size()-1; i++){
                    jsonObj.addProperty(bufLogin.get(i).toString(),bufPassword.get(i).toString());
                }
                setValue(jsonObj.toString());
                //MainActivity.print(jsonObj.toString());
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
