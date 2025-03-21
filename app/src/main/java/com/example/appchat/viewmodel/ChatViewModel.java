package com.example.appchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.appchat.model.Message;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import android.os.Handler;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();
    private List<Message> messages = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable runnable;

    public ChatViewModel() {
        loadMessages(); // Cargar mensajes iniciales
        subscribeToMessages(); // Iniciar la suscripción para nuevos mensajes
    }

    public LiveData<List<Message>> getMessagesLiveData() {
        return messagesLiveData;
    }

    private void loadMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include("sender"); // Incluir información del remitente
        query.orderByAscending("createdAt"); // Ordenar por fecha de creación
        query.setLimit(50); // Limitar el número de mensajes a 50
        query.findInBackground((objects, e) -> {
            if (e == null && objects != null) {
                messages.clear(); // Limpiar la lista actual
                messages.addAll(objects); // Agregar los mensajes obtenidos
                messagesLiveData.postValue(messages); // Notificar al LiveData
            }
        });
    }

    private void subscribeToMessages() {
        runnable = new Runnable() {
            @Override
            public void run() {
                loadNewMessages(); // Cargar nuevos mensajes
                handler.postDelayed(this, 5000); // Repetir cada 5 segundos
            }
        };
        handler.post(runnable); // Iniciar el proceso
    }

    private void loadNewMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include("sender"); // Incluir información del remitente
        query.orderByAscending("createdAt"); // Ordenar por fecha de creación
        query.whereGreaterThan("createdAt", getLastMessageDate()); // Obtener mensajes nuevos
        query.findInBackground((objects, e) -> {
            if (e == null && objects != null && !objects.isEmpty()) {
                if (!messages.containsAll(objects)) {
                    messages.addAll(objects); // Agregar nuevos mensajes a la lista
                    messagesLiveData.postValue(messages); // Notificar al LiveData
                }
            }
        });
    }

    private Date getLastMessageDate() {
        if (messages.isEmpty()) {
            return new Date(0); // Retorna una fecha muy antigua si no hay mensajes
        }
        return messages.get(messages.size() - 1).getCreatedAt(); // Retorna la fecha del último mensaje
    }

    public void sendMessage(String content) {
        if (!content.isEmpty()) {
            Message message = new Message();
            message.setContent(content);
            message.setSender(ParseUser.getCurrentUser());
            message.setTimestamp(String.valueOf(System.currentTimeMillis()));
            message.saveInBackground(e -> {
                if (e == null) {
                    // Agregar el mensaje a la lista
                    messages.add(message);
                    // Notificar al LiveData que los mensajes han cambiado
                    messagesLiveData.postValue(messages);
                }
            });
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(runnable); // Detener el Handler
    }
}