package com.fahad.mybills.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fahad.mybills.Bill;
import com.fahad.mybills.CustomDialog;
import com.fahad.mybills.R;
import com.fahad.mybills.Utils.CustomWebView;
import com.fahad.mybills.Utils.TextUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewBillActivity extends AppCompatActivity {
    CustomWebView webView, webViewForDL;
    CustomDialog customDialog;
    Bill bill;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill);

        customDialog = new CustomDialog(this);

        FloatingActionButton fabDownload = findViewById(R.id.fabDownload);
        fabDownload.setOnClickListener(v-> {

            customDialog.showDownloadDialog(new CustomDialog.DownloadListener() {

                @Override
                public void onImageClick() {
                    saveAsImage();
                }

                @Override
                public void onPdfClick() {
                    saveAsPDF();
                }

                @Override
                public void onCancel() {
                    customDialog.hide();
                }
            });
        });

        Intent intent = getIntent();
        if (intent.hasExtra("bill")) {
            bill = (Bill) intent.getSerializableExtra("bill");
        }

        webView = findViewById(R.id.webView);
        webViewForDL = findViewById(R.id.webViewForDL);

        webView.setInitialScale(100);
        webViewForDL.setInitialScale(100);


        if (bill != null) {
            String decompressedHtml = TextUtils.decompressText(bill.getBillData());
            webView.loadDataWithBaseURL(null, decompressedHtml, "text/html", "UTF-8", null);
            webViewForDL.loadDataWithBaseURL(null, decompressedHtml, "text/html", "UTF-8", null);

            if (bill.getRef().length() == 11) {
            } else if (bill.getRef().length() == 14) {
            }
        }
    }

    private void saveAsPDF() {
        Bitmap bitmap = getBitmap();

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas pdfCanvas = page.getCanvas();
        pdfCanvas.drawBitmap(bitmap, 0, 0, null);
        pdfDocument.finishPage(page);

        String fileName = bill.getCompany() + "-" + bill.getRef() + "-" + bill.getMonth() + ".pdf";
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyBills");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved to Downloads/MyBills folder", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }



    private void saveAsImage() {
        Bitmap bitmap = getBitmap();

        String fileName = bill.getCompany() + "-" + bill.getRef() + "-" + bill.getMonth() + ".png";

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyBills");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Image saved to Downloads/MyBills folder", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmap() {
        int contentWidth = webViewForDL.getWidth();
        int contentHeight = webViewForDL.getHeight();

        float scale = webViewForDL.getResources().getDisplayMetrics().density;

        Bitmap bitmap = Bitmap.createBitmap((int) (contentWidth * scale), (int) (contentHeight * scale), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        canvas.scale(scale, scale);

        webViewForDL.draw(canvas);

        return bitmap;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
        if (webViewForDL != null) {
            webViewForDL.destroy();
        }
    }
}
