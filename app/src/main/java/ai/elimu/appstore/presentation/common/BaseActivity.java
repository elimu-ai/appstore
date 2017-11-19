package ai.elimu.appstore.presentation.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import ai.elimu.appstore.R;


public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    protected void setupActionbar(Toolbar toolbar, String title, boolean homeEnabled, int
            homeResId) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(homeEnabled);
            actionBar.setTitle(title);
            actionBar.setHomeAsUpIndicator(homeResId);
        }
    }

    private void showProgressDialog() {

        if (progressDialog == null) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);

            try {
                progressDialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }

            progressDialog.setCancelable(false);

            if (progressDialog.getWindow() != null) {
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics
                        .Color.TRANSPARENT));
            }

            progressDialog.setContentView(R.layout.dialog_loading);

        } else if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {

            try {
                progressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                progressDialog = null;
            }

        }
    }

    public void setLoadingDialog(final boolean isActive) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isActive) {
                    showProgressDialog();
                } else {
                    hideProgressDialog();
                }
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context
                            .INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {

            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}
