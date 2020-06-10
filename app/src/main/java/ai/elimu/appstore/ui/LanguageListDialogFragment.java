package ai.elimu.appstore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ai.elimu.appstore.MainActivity;
import ai.elimu.appstore.R;
import ai.elimu.appstore.util.SharedPreferencesHelper;
import ai.elimu.model.enums.Language;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     LanguageListDialogFragment.newInstance().show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class LanguageListDialogFragment extends BottomSheetDialogFragment {

    public static LanguageListDialogFragment newInstance() {
        LanguageListDialogFragment fragment = new LanguageListDialogFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language_list_dialog_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new LanguageAdapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        setCancelable(false);
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_language_list_dialog_list_dialog_item, parent, false));
            text = itemView.findViewById(R.id.text);
        }
    }


    private class LanguageAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Language[] languages = Language.values();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Language language = languages[position];
            holder.text.setText(language.getEnglishName() + " (" + language.getNativeName() + ")");
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(getClass().getName(), "onClick");

                    Log.i(getClass().getName(), "language: " + language);
                    SharedPreferencesHelper.storeLanguage(getContext(), language);

                    // Restart the MainActivity
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return languages.length;
        }
    }
}
