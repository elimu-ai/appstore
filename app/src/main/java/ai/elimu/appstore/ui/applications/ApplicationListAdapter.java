package ai.elimu.appstore.ui.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ai.elimu.appstore.R;
import ai.elimu.appstore.room.entity.Application;
import ai.elimu.appstore.util.InstallationHelper;
import timber.log.Timber;

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ApplicationViewHolder> {

    class ApplicationViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewFirstLine;
        private final TextView textViewSecondLine;

        private Button launchButton;

        private ApplicationViewHolder(View itemView) {
            super(itemView);
            Timber.i("ApplicationViewHolder");
            textViewFirstLine = itemView.findViewById(R.id.textViewFirstLine);
            textViewSecondLine = itemView.findViewById(R.id.textViewSecondLine);
            launchButton = itemView.findViewById(R.id.list_item_launch_button);
        }
    }

    private final LayoutInflater layoutInflater;
    private final Context context;

    private List<Application> applications;

    ApplicationListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.i("onCreateViewHolder");
        View itemView = layoutInflater.inflate(R.layout.activity_application_list_item, parent, false);
        return new ApplicationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ApplicationViewHolder viewHolder, int position) {
        Timber.i("onBindViewHolder");
        if (applications != null) {
            Application application = applications.get(position);
            viewHolder.textViewFirstLine.setText(application.getPackageName());
            viewHolder.textViewSecondLine.setText(
                    application.getApplicationStatus().toString() + ", " +
                    application.getLiteracySkills() + ", " +
                    application.getNumeracySkills()
            );

            // If the APK has been installed, display the launch button. If not, hide it.
            if (InstallationHelper.isApplicationInstalled(application.getPackageName(), context)) {
                viewHolder.launchButton.setVisibility(View.VISIBLE);
                viewHolder.launchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Timber.i("onClick");

                        Timber.i("Launching \"" + application.getPackageName() + "\"");
                        PackageManager packageManager = context.getPackageManager();
                        Intent launchIntent = packageManager.getLaunchIntentForPackage(application.getPackageName());
                        Timber.i("launchIntent: " + launchIntent);
                        context.startActivity(launchIntent);
                    }
                });
            } else {
                viewHolder.launchButton.setVisibility(View.INVISIBLE);
            }


        }
    }

    @Override
    public int getItemCount() {
        Timber.i("getItemCount");
        if (applications == null) {
            return 0;
        } else {
            return applications.size();
        }
    }

    public void setApplications(List<Application> applications) {
        Timber.i("setApplications");
        this.applications = applications;
        notifyDataSetChanged();
    }
}
