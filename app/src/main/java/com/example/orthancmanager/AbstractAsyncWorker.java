package com.example.orthancmanager;

import android.os.AsyncTask;
import com.example.orthancmanager.datastorage.OrthancServer;
import static java.lang.Thread.sleep;

public abstract class AbstractAsyncWorker<String> extends AsyncTask<Void, Void, String> {
    private ConnectionCallback callback;
    private Throwable t;
    private OrthancServer server;
    private String param;

    AbstractAsyncWorker(ConnectionCallback callback, OrthancServer server, java.lang.String param) {
        this.callback = callback;
        this.server = server;
        this.param = (String) param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null) {
            callback.onBegin();
        }
    }

    protected abstract String doAction() throws Exception;

    @Override
    protected String doInBackground(Void... params) {
        try {

            return doAction();
        } catch (Exception e) {
            t = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(String v) {
        super.onPostExecute(v);
        if (callback != null) {
            callback.onEnd();
        }
        try {
            sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        generateCallback(v,server,param);
    }

    private void generateCallback(String data, OrthancServer server, String param) {
        if (callback == null) return;
        if (data != null) {
            callback.onSuccess((java.lang.String) data, server, (java.lang.String) param);
        } else if (t != null) {
            callback.onFailure(t);
        } else {
            callback.onFailure(new NullPointerException("Result is empty but error empty too"));
        }
    }
}
