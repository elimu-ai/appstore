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
        if ((installCompleteCallback == null) || (intent.getData() == null)) {
            return;
        }
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            installCompleteCallback.onInstallComplete(intent.getData().getSchemeSpecificPart());
        } else {
            installCompleteCallback.onUninstallComplete(intent.getData().getSchemeSpecificPart());
        }

    }

    public void setInstallCompleteCallback(@NonNull InstallCompleteCallback
                                                   installCompleteCallback) {
        this.installCompleteCallback = Preconditions.checkNotNull(installCompleteCallback);
    }

    /**
     * A listener that listens to install/uninstall completion event
     */
    public interface InstallCompleteCallback {

        void onInstallComplete(@NonNull String packageName);

        void onUninstallComplete(@NonNull String packageName);

    }
}
