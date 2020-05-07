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
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;
        import com.google.gson.JsonArray;
        import com.google.gson.JsonObject;
        import com.google.gson.JsonParser;
        import java.util.ArrayList;
        import java.util.Set;

public class PeerDialogPreference extends DialogPreference
{
    private String jsonStr;
    static ArrayList<Peer> peers = new ArrayList<Peer>();
    Object[] jsonkeys;

    public PeerDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setDialogLayoutResource(R.layout.peer_dialog_recyclerview);
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
        final EditText editName = (EditText)view.findViewById(R.id.peerName);
        final EditText editURL = (EditText)view.findViewById(R.id.peerURL);
        final EditText editLogin = (EditText)view.findViewById(R.id.peerLogin);
        final EditText editPassword = (EditText)view.findViewById(R.id.peerPassword);
        ImageView addItem = (ImageView)view.findViewById(R.id.addPeer);
        JsonParser parser = new JsonParser();
        JsonObject orthancJson = parser.parse(jsonStr).getAsJsonObject();
        Set<String> keys = orthancJson.keySet();
        jsonkeys = keys.toArray();
        peers.clear();

        try {
            for (int i = 0; i <= jsonkeys.length - 1; i++) {
                JsonArray bufArray = orthancJson.getAsJsonArray(jsonkeys[i].toString());
                Peer node = new Peer();
                node.setmName(jsonkeys[i].toString());
                node.setmURL(bufArray.get(0).getAsString());
                node.setmLogin(bufArray.get(1).getAsString());
                node.setmPassword(bufArray.get(2).getAsString());
                peers.add(node);
            }
        }catch (Exception e){
            MainActivity.print("dicompref error "+ e.toString());
        }

        try {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewPeer);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            final PeerAdapter adapter;
            adapter = new PeerAdapter(peers, this.getContext());
            recyclerView.setAdapter(adapter);
            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!(editName.getText().toString().equals(""))&
                            (!editURL.getText().toString().equals("")))
                    {
                        Peer node = new Peer();
                        node.setmName(editName.getText().toString());
                        node.setmURL(editURL.getText().toString());
                        node.setmLogin(editLogin.getText().toString());
                        node.setmPassword(editPassword.getText().toString());
                        peers.add(node);
                        adapter.notifyDataSetChanged();
                        editName.setText("");
                        editURL.setText("");
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
        peers.remove(i);
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
            try {
                JsonObject jsonObj = new JsonObject();
                for (int i = 0; i <= peers.size() - 1; i++) {
                    JsonArray arrayJSON = new JsonArray();
                    Peer node = peers.get(i);
                    //arrayJSON.add(node.mName);
                    arrayJSON.add(node.mURL);
                    arrayJSON.add(node.mLogin);
                    arrayJSON.add(node.mPassword);
                    jsonObj.add(node.mName, arrayJSON);
                }
                setValue(jsonObj.toString());
                //MainActivity.print(jsonObj.toString());
            }catch (Exception e){
                MainActivity.print(e.toString());
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
