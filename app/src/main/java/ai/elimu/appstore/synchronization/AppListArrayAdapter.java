package ai.elimu.appstore.synchronization;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

import ai.elimu.appstore.model.Application;

public class AppListArrayAdapter extends ArrayAdapter<Application> {

    private Context context;

    private List<Application> applications;

    public AppListArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Application> objects) {
        super(context, resource, objects);
    }
}
