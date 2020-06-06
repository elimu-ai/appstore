package ai.elimu.appstore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i("onReceive");
    }
}
