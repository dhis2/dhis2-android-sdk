package org.hisp.dhis.android.sdk.export;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.ref.WeakReference;

/**
 * Created by thomaslindsjorn on 05/09/16.
 */
public abstract class ExportService<T extends ExportResponse> extends Service {

    final Messenger messenger = new Messenger(new MessageHandler<T>(this));

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "onBind. Intent: " + intent);
        return messenger.getBinder();
    }

    //static inner class doesn't hold an implicit reference to the outer class
    private static class MessageHandler<T extends ExportResponse> extends Handler {
        //Using a weak reference to not prevent garbage collection
        private final WeakReference<ExportService<T>> exportServiceWeakReference;

        public MessageHandler(ExportService<T> exportServiceInstance) {
            exportServiceWeakReference = new WeakReference<ExportService<T>>(exportServiceInstance);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.replyTo != null && exportServiceWeakReference.get() != null) {
                // TODO: Put in security measures here.
                // Check username and password provided by client in msg bundle extra
                // against UserAccount.getCurrentUserAccountFromDb() ?

                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                T responseObject = exportServiceWeakReference.get().getResponseObject();
                String responseAsString = exportServiceWeakReference.get().marshallToString(responseObject);
                bundle.putString("data", responseAsString);
                message.setData(bundle);
                try {
                    msg.replyTo.send(message);
                } catch (RemoteException e) {
                    Log.e("EXPORT", "Error sending message to client", e);
                }
            }
        }
    }

    @NonNull
    private String marshallToString(T responseObject) {
        ObjectMapper om = new ObjectMapper();
        String responseString;
        try {
            responseString = om.writeValueAsString(responseObject);
        } catch (JsonProcessingException e) {
            try {
                ExportResponse errorResponse = new ExportResponse();
                errorResponse.setError(e);
                responseString = om.writeValueAsString(errorResponse);
                Log.e("EXPORT", "Unable to marshall object to String: " + responseObject.getClass().toString(), e);
            } catch (JsonProcessingException e1) {
                responseString = "Unable to marshall object to String\n" + e1.toString();
                Log.e("EXPORT", "Unable to marshall object to String", e1);
            }
        }
        return responseString;
    }

    public abstract T getResponseObject();
}
