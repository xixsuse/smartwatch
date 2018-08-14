package com.tcs.sonusourav.smartwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by SONU SOURAV on 4/8/2018.
 */

public class BloodPressureResult extends AppCompatActivity {

    private ActionBar bpAnalysisActionBar;
    private Button bpShareButton;
    private TextView bpResultTextView;
    private TextView bpResultTimeTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloodpressure_analysis_result);

        bpShareButton = findViewById(R.id.bp_share_button);

        bpResultTextView=findViewById(R.id.bp_result_tv);
        bpResultTimeTextView=findViewById(R.id.bp_result_time_tv);



        bpResultTextView.setText(" 88 ");
        bpResultTimeTextView.setText(Calendar.getInstance().getTime().toString());

        bpShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText=" HeartRate : 88 ";

                createandDisplayPdf(shareText);
            }
        });
    }

    public void onResume() {
        super.onResume();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        bpAnalysisActionBar = getSupportActionBar();
        bpAnalysisActionBar.setTitle("Blood Pressure");
        bpAnalysisActionBar.setHomeButtonEnabled(true);
        bpAnalysisActionBar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createandDisplayPdf(String text) {

        Document doc = new Document();

        try {

            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/smartWatch";

            File dir = new File(path);
            if(!dir.exists()){
                dir.mkdirs();
                Log.d("directory","dir does not exist");
            }

            File file = new File(dir, "Bloodpressure.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Paragraph p1 = new Paragraph(text);
            Font paraFont= new Font(Font.FontFamily.COURIER);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally {
            doc.close();
        }

        sharePdf();
    }



    private void sharePdf() {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"sonusouravdx001@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "body text");
        File root = Environment.getExternalStorageDirectory();
        String pathToMyAttachedFile = "smartwatch/HeartrateResult.pdf";
        File file = new File(root, pathToMyAttachedFile);
        if (!file.exists() || !file.canRead()) {
            return;
        }
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }
}
