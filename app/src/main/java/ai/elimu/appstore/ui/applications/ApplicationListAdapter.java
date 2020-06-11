package ai.elimu.appstore.ui.applications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ai.elimu.appstore.R;
import ai.elimu.appstore.room.entity.Application;

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ApplicationViewHolder> {

    class ApplicationViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewFirstLine;
        private final TextView textViewSecondLine;

        private ApplicationViewHolder(View itemView) {
            super(itemView);
            textViewFirstLine = itemView.findViewById(R.id.textViewFirstLine);
            textViewSecondLine = itemView.findViewById(R.id.textViewSecondLine);
        }
    }

    private final LayoutInflater layoutInflater;

    private List<Application> applications;

    ApplicationListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.activity_application_list_item, parent, false);
        return new ApplicationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder viewHolder, int position) {
        if (applications != null) {
            Application application = applications.get(position);
            viewHolder.textViewFirstLine.setText(application.getPackageName());
            viewHolder.textViewSecondLine.setText(application.getApplicationStatus().toString());
        }
    }

    @Override
    public int getItemCount() {
        if (applications == null) {
            return 0;
        } else {
            return applications.size();
        }
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
        notifyDataSetChanged();
    }
}
