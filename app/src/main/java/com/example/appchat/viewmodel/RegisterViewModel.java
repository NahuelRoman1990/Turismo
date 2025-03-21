package com.example.appchat.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.appchat.model.User;
import com.example.appchat.providers.AuthProvider;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<String> registerResult = new MutableLiveData<>();
    private final AuthProvider authProvider;

    public RegisterViewModel() {
        this.authProvider = new AuthProvider();
    }

    public LiveData<String> getRegisterResult() {
        return registerResult;
    }

    public void register(User user) {
        LiveData<String> result = authProvider.signUp(user);

        result.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String objectId) {
                if (objectId != null) {
                    // Registro exitoso, configurar el ACL
                    configureUserACL(objectId);
                } else {
                    // Error en el registro
                    registerResult.setValue(null);
                    Log.e("RegisterViewModel", "Error durante el registro.");
                }

                // Eliminar el Observer para evitar fugas de memoria
                result.removeObserver(this);
            }
        });
    }

    private void configureUserACL(String userId) {
        // Obtener el usuario recién registrado
        ParseUser user = ParseUser.createWithoutData(ParseUser.class, userId);

        // Crear un nuevo ACL
        ParseACL acl = new ParseACL();

        // Permitir que el usuario tenga control total (lectura y escritura)
        acl.setReadAccess(user, true); // El usuario puede leer
        acl.setWriteAccess(user, true); // El usuario puede escribir

        // Permitir que cualquier usuario tenga acceso de lectura
        acl.setPublicReadAccess(true); // Lectura pública

        // Asignar el ACL al usuario
        user.setACL(acl);

        // Guardar los cambios en Parse
        user.saveInBackground(e -> {
            if (e == null) {
                // ACL configurado correctamente
                registerResult.setValue(userId);
                Log.d("RegisterViewModel", "ACL configurado correctamente para el usuario: " + userId);
            } else {
                // Error al configurar el ACL
                registerResult.setValue(null);
                Log.e("RegisterViewModel", "Error al configurar ACL: ", e);
            }
        });
    }
}

