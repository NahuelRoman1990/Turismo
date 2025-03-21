package com.example.appchat.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchat.R;
import com.example.appchat.adapters.ChatAdapter;
import com.example.appchat.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MensajeActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMensajes;
    private ChatAdapter mensajeAdapter;
    private List<Message> listaMensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);

        recyclerViewMensajes = findViewById(R.id.recyclerViewMensajes);
        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(this));

        listaMensajes = new ArrayList<>(); // Cargar los mensajes aquí
        mensajeAdapter = new ChatAdapter(this, listaMensajes);
        recyclerViewMensajes.setAdapter(mensajeAdapter);
    }
}