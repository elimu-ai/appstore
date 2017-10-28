package ai.elimu.appstore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.Preconditions;

public class PackageUpdateReceiver extends BroadcastReceiver {

    private PackageUpdateCallback packageUpdateCallback;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ((packageUpdateCallback == null) || (intent.getData() == null)) {
            return;
        }
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            packageUpdateCallback.onInstallComplete(intent.getData().getSchemeSpecificPart());
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            packageUpdateCallback.onUninstallComplete(intent.getData().getSchemeSpecificPart());
        }
    }

    public void setPackageUpdateCallback(@NonNull PackageUpdateCallback
                                                 packageUpdateCallback) {
        this.packageUpdateCallback = Preconditions.checkNotNull(packageUpdateCallback);
    }

    /**
     * A listener that listens to install/uninstall completion event
     */
    public interface PackageUpdateCallback {

        void onInstallComplete(@NonNull String packageName);

        void onUninstallComplete(@NonNull String packageName);

    }
}
