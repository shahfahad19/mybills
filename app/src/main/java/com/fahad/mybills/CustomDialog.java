package com.fahad.mybills;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog {
    private Dialog dialog;
    private final Context context;
    public CustomDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void showAlert(String message) {
        dialog = new Dialog(this.context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cc_alert);
        dialog.setCancelable(false);

        Button alertButton = dialog.findViewById(R.id.okBtn);
        TextView alertTextView = dialog.findViewById(R.id.textViewMessage);

        alertTextView.setText(message);

        dialog.show();

        alertButton.setOnClickListener(v -> dialog.hide());
    }

    public void showConfirmation(String message, String confirmText, String cancelText, ConfirmationListener listener) {
        dialog = new Dialog(this.context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cc_confirmation);
        dialog.setCancelable(false);

        Button confirmButton = dialog.findViewById(R.id.confirmBtn);
        Button cancelButton = dialog.findViewById(R.id.cancelBtn);
        TextView confirmationTextView = dialog.findViewById(R.id.textViewMessage);

        confirmationTextView.setText(message);
        confirmButton.setText(confirmText);
        cancelButton.setText(cancelText);

        dialog.show();

        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onConfirmed();
            }
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onCancelled();
            }
        });
    }

    public void showDownloadDialog(DownloadListener listener) {
        dialog = new Dialog(this.context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cc_download_selector);
        dialog.setCancelable(false);

        Button imageBtn = dialog.findViewById(R.id.imageBtn);
        Button pdfBtn = dialog.findViewById(R.id.pdfBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

        dialog.show();

        imageBtn.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onImageClick();
            }
        });

        pdfBtn.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onPdfClick();
            }
        });

        cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onCancel();
            }
        });
    }

    public void showProgress(String message) {
        dialog = new Dialog(this.context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cc_progress);
        dialog.setCancelable(false);

        TextView textView = dialog.findViewById(R.id.textViewMessage);
        textView.setText(message);

        dialog.show();
    }

    public void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public interface ConfirmationListener {
        void onConfirmed();

        void onCancelled();
    }

    public interface DownloadListener {
        void onImageClick();

        void onPdfClick();

        void onCancel();
    }
}
