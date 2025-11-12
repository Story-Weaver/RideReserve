package by.story_weaver.ridereserve.Logic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkHelper {

    public static void checkInternetConnection(Context context, NetworkCheckCallback callback) {
        // Сначала проверяем базовое подключение
        if (!isNetworkAvailable(context)) {
            callback.onResult(false);
            return;
        }

        // Затем проверяем реальный доступ в интернет
        checkRealInternetAccess(callback);
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private static void checkRealInternetAccess(NetworkCheckCallback callback) {
        new Thread(() -> {
            boolean hasInternet = false;
            try {
                // Пробуем несколько серверов для надежности
                hasInternet = pingHost("8.8.8.8", 53, 2000) ||
                        pingHost("1.1.1.1", 53, 2000) ||
                        pingHost("google.com", 80, 2000);
            } catch (Exception e) {
                Log.e("NetworkHelper", e.getMessage() + " ");
            }

            final boolean result = hasInternet;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                callback.onResult(result);
            });
        }).start();
    }

    private static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public interface NetworkCheckCallback {
        void onResult(boolean hasInternet);
    }
}
