package com.tcs.sonusourav.smartwatch;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

/**
 * Created by SONU SOURAV on 4/8/2018.
 */

public class HeartRateResult extends AppCompatActivity {

    private ActionBar HeartAnalysisActionBar;
    private Button hrShareButton;
    private TextView hrResultTextView;
    private TextView hrResultTimeTextView;
    private   Uri uri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heartbeat_analysis_result);


        hrShareButton = findViewById(R.id.hr_share_button);
        hrResultTextView=findViewById(R.id.hr_result_tv);
        hrResultTimeTextView=findViewById(R.id.hr_result_time_tv);



        hrResultTextView.setText(" 88 ");
        hrResultTimeTextView.setText(Calendar.getInstance().getTime().toString());

                hrShareButton.setOnClickListener(new View.OnClickListener() {
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








    // Method for creating a pdf file from text, saving it then opening it for display
    public void createandDisplayPdf(String text) {

        Document doc = new Document();

        try {

            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/smartWatch";

            File dir = new File(path);
            if(!dir.exists()){
                dir.mkdirs();
                Log.d("directory","dir does not exist");
            }

            File file = new File(dir, "HeartRateResult.pdf");
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




    // Method for opening a pdf file
    private void viewPdf() {

        File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartwatch"+"/HeartrateResult.pdf");
        Uri path = FileProvider.getUriForFile(HeartRateResult.this,
                this.getApplicationContext().getPackageName() + ".provider",
                pdfFile);


        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);


        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(HeartRateResult.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
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


    public boolean onCreateOptionsMenu(Menu menu) {
        HeartAnalysisActionBar = getSupportActionBar();
        Objects.requireNonNull(HeartAnalysisActionBar).setTitle("Heart rate");
        HeartAnalysisActionBar.setHomeButtonEnabled(true);
        HeartAnalysisActionBar.setDisplayHomeAsUpEnabled(true);
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


}
