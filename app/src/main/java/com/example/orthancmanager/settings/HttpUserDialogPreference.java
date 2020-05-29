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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Set;

public class HttpUserDialogPreference extends DialogPreference
{
    private String jsonStr;
    private static ArrayList<String> bufLogin = new ArrayList<>();
    private static ArrayList<String> bufPassword = new ArrayList<>();

    public HttpUserDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
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
        final EditText editLogin = view.findViewById(R.id.addLogin);
        final EditText editPassword = view.findViewById(R.id.addPassword);
        ImageView addItem = view.findViewById(R.id.addItem);
        JsonParser parser = new JsonParser();
        JsonObject orthancJson;
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
            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHttp);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            final HttpUserDialogAdapter adapter;
            adapter = new HttpUserDialogAdapter(bufLogin, bufPassword);
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

    static void delItem(int i){
        bufLogin.remove(i);
        bufPassword.remove(i);
    }

    private String getValue()
    {
        return jsonStr;
    }

    private void setValue(String value)
    {
            jsonStr = value;
            persistString(value);
            notifyChanged();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);
        if (positiveResult)
        {
            {
                JsonObject jsonObj = new JsonObject();
                for(int i=0; i<=bufLogin.size()-1; i++){
                    jsonObj.addProperty(bufLogin.get(i), bufPassword.get(i));
                }
                setValue(jsonObj.toString());
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState myState = new SavedState(superState);
        myState.value = getValue();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (state == null || !state.getClass().equals(SavedState.class))
        {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        setValue(myState.value);
        super.onRestoreInstanceState(myState.getSuperState());
    }

    private static class SavedState extends BaseSavedState
    {
        String value;

        SavedState(Parcelable superState)
        {
            super(superState);
        }

        SavedState(Parcel source)
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
