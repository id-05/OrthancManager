package com.example.orthancmanager;

import android.os.AsyncTask;

import static java.lang.Thread.sleep;

public abstract class AbstractAsyncWorker<String> extends AsyncTask<Void, Void, String> {
    private ConnectionCallback<String> callback;
    private Throwable t;
    private OrthancServer server;
    private String param;

    //В конструктор передаём интерфейс
    protected AbstractAsyncWorker(ConnectionCallback callback, OrthancServer server, java.lang.String param) {
        this.callback = callback;
        this.server = server;
        this.param = (String) param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null) {
            callback.onBegin(); //Сообщаем через интерфейс о начале
        }
    }

    protected abstract String doAction() throws Exception; //Этот метод будем переопределять

    @Override
    protected String doInBackground(Void... params) {
        try {

            return doAction(); //В параллельном потоке вызываем абстрактный метод.
        } catch (Exception e) {
            t = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(String v) {
        super.onPostExecute(v);
        if (callback != null) {
            callback.onEnd(); //Сообщаем об окончании
        }
        try {
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        generateCallback(v,server,param);
    }

    private void generateCallback(String data, OrthancServer server, String param) { //Генерируем ответ
        if (callback == null) return;
        if (data != null) { //Есть данные - всё хорошо
            callback.onSuccess((java.lang.String) data, server, (java.lang.String) param);
        } else if (t != null) {
            callback.onFailure(t); //Есть ошибка - вызываем onFailure
        } else { //А такая ситуация вообще не должна появляться)
            callback.onFailure(new NullPointerException("Result is empty but error empty too"));
        }
    }
}
