package com.example.orthancmanager;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DicomModalitiesDialogPreference extends DialogPreference
{
    private String jsonStr;
    static ArrayList<DicomModalities> dicomModalities = new ArrayList<DicomModalities>();
    Object[] jsonkeys;

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
        final EditText editName = (EditText)view.findViewById(R.id.addName);
        final EditText editAET = (EditText)view.findViewById(R.id.addNameModalities);
        final EditText editIP = (EditText)view.findViewById(R.id.addIPModalities);
        final EditText editPORT = (EditText)view.findViewById(R.id.addPort);
        final Spinner chooseProperty = (Spinner) view.findViewById(R.id.PropertySpinner);
        ImageView addItem = (ImageView)view.findViewById(R.id.addModalitiesItem);

        JsonParser parser = new JsonParser();
        JsonObject orthancJson = parser.parse(jsonStr).getAsJsonObject();
        Set<String> keys = orthancJson.keySet();
        jsonkeys = keys.toArray();
        dicomModalities.clear();

        try {
            for (int i = 0; i <= jsonkeys.length - 1; i++) {
                JsonArray bufArray = orthancJson.getAsJsonArray(jsonkeys[i].toString());
                DicomModalities node = new DicomModalities();
                node.setmTitle(jsonkeys[i].toString());
                node.setmName(bufArray.get(0).getAsString());
                node.setmIP(bufArray.get(1).getAsString());
                node.setmPort(bufArray.get(2).getAsString());
                node.setmProperty(bufArray.get(3).getAsString());
                dicomModalities.add(node);
            }
        }catch (Exception e){
            MainActivity.print("dicompref error "+ e.toString());
        }

        try {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewDicomModalities);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            final DicomModelitiesAdapter adapter;
            adapter = new DicomModelitiesAdapter(dicomModalities, this.getContext());
            recyclerView.setAdapter(adapter);
            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!(editName.getText().toString().equals(""))&
                            (!editAET.getText().toString().equals(""))&
                                (!editIP.getText().toString().equals(""))&
                                    (!editPORT.getText().toString().equals("")))
                    {
                        DicomModalities node = new DicomModalities();
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

    public static void delItem(int i){
        dicomModalities.remove(i);
    }

    public String getValue()
    {
        return jsonStr;
    }

    public void setValue(String value)
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
                    DicomModalities node = dicomModalities.get(i);

                    arrayJSON.add(node.mName);
                    arrayJSON.add(node.mIP);
                    arrayJSON.add(Integer.valueOf(node.mPort));
                    arrayJSON.add(node.mProperty);
                    jsonObj.add(jsonkeys[i].toString(), arrayJSON);
                }
                setValue(jsonObj.toString());
                //MainActivity.print(jsonObj.toString());
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