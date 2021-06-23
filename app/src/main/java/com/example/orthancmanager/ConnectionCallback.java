package com.example.orthancmanager;

import com.example.orthancmanager.date.OrthancServer;

public interface ConnectionCallback <V> {
    void onBegin(); //Асинхронная операция началась

    void onSuccess(String data, OrthancServer server, String param); //Получили результат

    void onFailure(Throwable t); //Получили ошибку

    void onEnd(); //Операция закончилась
}
