package ai.elimu.appstore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.Preconditions;

public class InstallCompleteReceiver extends BroadcastReceiver {

    private InstallCompleteCallback installCompleteCallback;

    @Override
    public void onReceive(Context context, Intent intent) {
        installCompleteCallback.onInstallComplete(intent.getData().toString());
    }

    public void setInstallCompleteCallback(@NonNull InstallCompleteCallback
                                                   installCompleteCallback) {
        this.installCompleteCallback = Preconditions.checkNotNull(installCompleteCallback);
    }

    /**
     * A listener that listens to install completion event
     */
    public interface InstallCompleteCallback {

        void onInstallComplete(@NonNull String packageName);

    }
}
