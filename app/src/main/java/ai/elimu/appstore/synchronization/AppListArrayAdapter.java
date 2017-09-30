package ai.elimu.appstore.synchronization;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ai.elimu.appstore.R;
import ai.elimu.appstore.model.Application;
import ai.elimu.model.enums.admin.ApplicationStatus;
import timber.log.Timber;

public class AppListArrayAdapter extends ArrayAdapter<Application> {

    private Context context;

    private List<Application> applications;

    static class ViewHolder {
        TextView textViewPackageName;
        TextView textViewVersion;
        Button buttonDownload;
    }

    public AppListArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Application> applications) {
        super(context, resource, applications);
        Timber.i("AppListArrayAdapter");

        this.context = context;
        this.applications = applications;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Timber.i("AppListArrayAdapter");

        Application application = applications.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = layoutInflater.inflate(R.layout.activity_app_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.textViewPackageName = listItem.findViewById(R.id.textViewPackageName);
        viewHolder.textViewVersion = listItem.findViewById(R.id.textViewVersion);
        viewHolder.buttonDownload = listItem.findViewById(R.id.buttonDownload);

        viewHolder.textViewPackageName.setText(application.getPackageName());

        viewHolder.textViewVersion.setText(context.getText(R.string.version) + ": " + application.getVersionCode());

        if (application.getApplicationStatus() != ApplicationStatus.ACTIVE) {
            // Do not allow APK download
            viewHolder.buttonDownload.setEnabled(false);
        } else {
            viewHolder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.i("buttonDownload onClick");

                    // Initiate APK download
                    // TODO
                }
            });
        }

        return listItem;
    }
}
