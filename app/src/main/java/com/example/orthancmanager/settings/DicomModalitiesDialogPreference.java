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
import android.widget.Spinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orthancmanager.MainActivity;
import com.example.orthancmanager.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Set;

public class DicomModalitiesDialogPreference extends DialogPreference
{
    private String jsonStr;
    private static ArrayList<DicomModaliti> dicomModalities = new ArrayList<>();

    public DicomModalitiesDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dicom_modalities_dialog_recyclerview);
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
        final EditText editName = view.findViewById(R.id.addName);
        final EditText editAET = view.findViewById(R.id.addNameModalities);
        final EditText editIP = view.findViewById(R.id.addIPModalities);
        final EditText editPORT = view.findViewById(R.id.addPort);
        final Spinner chooseProperty = view.findViewById(R.id.PropertySpinner);
        ImageView addItem = view.findViewById(R.id.addModalitiesItem);
        JsonParser parser = new JsonParser();
        JsonObject orthancJson = parser.parse(jsonStr).getAsJsonObject();
        Set<String> keys = orthancJson.keySet();
        Object[] jsonkeys = keys.toArray();
        dicomModalities.clear();

        try {
            for (int i = 0; i <= jsonkeys.length - 1; i++) {
                JsonArray bufArray = orthancJson.getAsJsonArray(jsonkeys[i].toString());
                DicomModaliti node = new DicomModaliti();
                node.setmName(jsonkeys[i].toString());
                node.setmTitle(bufArray.get(0).getAsString());
                node.setmIP(bufArray.get(1).getAsString());
                node.setmPort(bufArray.get(2).getAsString());
                node.setmProperty(bufArray.get(3).getAsString());
                dicomModalities.add(node);
            }
        }catch (Exception e){
            MainActivity.print("dicompref error "+ e.toString());
        }

        try {
            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDicomModalities);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            final DicomModelitiesAdapter adapter;
            adapter = new DicomModelitiesAdapter(dicomModalities);
            recyclerView.setAdapter(adapter);
            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!(editName.getText().toString().equals(""))&
                            (!editAET.getText().toString().equals(""))&
                                (!editIP.getText().toString().equals(""))&
                                    (!editPORT.getText().toString().equals("")))
                    {
                        DicomModaliti node = new DicomModaliti();
                        node.setmName(editName.getText().toString());
                        node.setmTitle(editAET.getText().toString());
                        node.setmIP(editIP.getText().toString());
                        node.setmPort(editPORT.getText().toString());
                        node.setmProperty(chooseProperty.getSelectedItem().toString());
                        dicomModalities.add(node);
                        adapter.notifyDataSetChanged();
                        editName.setText("");
                        editAET.setText("");
                        editIP.setText("");
                        editPORT.setText("");
                        chooseProperty.setSelection(0);
                    }
                }
            });
        }catch (Exception e){
            MainActivity.print("onBindDialogView  "+e.toString());
        }
    }

    static void delItem(int i){
        dicomModalities.remove(i);
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
                JsonObject jsonObj = new JsonObject();
                for(int i=0; i<=dicomModalities.size()-1; i++){
                    JsonArray arrayJSON = new JsonArray();
                    DicomModaliti node = dicomModalities.get(i);
                    arrayJSON.add(node.mTitle);
                    arrayJSON.add(node.mIP);
                    arrayJSON.add(Integer.valueOf(node.mPort));
                    arrayJSON.add(node.mProperty);
                    jsonObj.add(node.mName, arrayJSON);
                }
                setValue(jsonObj.toString());
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