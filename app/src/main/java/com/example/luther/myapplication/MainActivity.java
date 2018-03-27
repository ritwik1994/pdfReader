package com.example.luther.myapplication;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;



public class MainActivity extends AppCompatActivity {

    private WebView wv;
    String pdfFileName;
    Uri uri;
    private int ViewSize = 0;
    private final static int PICKFILE_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Settings
        PDFImage.sShowImages = true; // show images
        PDFPaint.s_doAntiAlias = true; // make text smooth
        HardReference.sKeepCaches = true; // save images in cache
        Button button = (Button)findViewById(R.id.button);
        //Setup webview
        wv = (WebView)findViewById(R.id.webView1);
        wv.getSettings().setBuiltInZoomControls(true);//show zoom buttons
        wv.getSettings().setSupportZoom(true);//allow zoom
        //get the width of the webview
        wv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                ViewSize = wv.getWidth();
                wv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, PICKFILE_RESULT_CODE);
//                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//                chooseFile.setType("*/*");
//                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });

        try
        {
//            File file = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "/ritwikResume.pdf");
//            RandomAccessFile f = new RandomAccessFile(file, "r");
//            byte[] data = new byte[(int)f.length()];
//            f.readFully(data);
//            pdfLoadImages(data);
        }
        catch(Exception ignored)
        {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKFILE_RESULT_CODE) {
//            uri = data.getData();
//            pdfFileName = getFileName(uri);
//            System.out.println(pdfFileName);
//            uri = data.getData();
//            pdfFileName = getFileName(uri);
//            System.out.println(pdfFileName);

            try
            {
                Uri uri = data.getData();
                pdfFileName = getFileName(uri);
                String src = uri.getPath();
                File source = new File(src);
//                File file = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "/ritwikResume.pdf");
                RandomAccessFile f = new RandomAccessFile(source, "r");
                byte[] data1 = new byte[(int)f.length()];
                f.readFully(data1);
                pdfLoadImages(data1);
            }
            catch(Exception ignored)
            {
            }
//            String filename = uri.getLastPathSegment();
//            File destination = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "/CustomFolder/" + filename);
//            copy(source, destination);
//            try
//            {
//                File file = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "/ritwikResume.pdf");
//                RandomAccessFile f = new RandomAccessFile(file, "r");
//                byte[] data1 = new byte[(int)f.length()];
//                f.readFully(data1);
//                pdfLoadImages(data1);
//            }
//            catch(Exception ignored)
//            {
//            }
//            System.out.println(filePath);
        }
    }

    private void copy(File source, File destination) {
        try {
            FileChannel in = new FileInputStream(source).getChannel();
            FileChannel out = new FileOutputStream(destination).getChannel();

            try {
                in.transferTo(0, in.size(), out);
            } catch (Exception f) {
                // post to log
            } finally {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
        }catch(Exception e){

        }
    }
//    public String getPath(Uri uri) {
//
//        String path = null;
//        String[] projection = { MediaStore.Files.FileColumns.DATA };
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//
//        if(cursor == null){
//            path = uri.getPath();
//        }
//        else{
//            cursor.moveToFirst();
//            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
//            path = cursor.getString(column_index);
//            cursor.close();
//        }
//
//        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
//    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    //Load Images:
    private void pdfLoadImages(final byte[] data)
    {
        try
        {
            // run async
            new AsyncTask<Void, Void, String>()
            {
                // create and show a progress dialog
                ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "", "Opening...");

                @Override
                protected void onPostExecute(String html)
                {
                    //after async close progress dialog
                    progressDialog.dismiss();
                    //load the html in the webview
                    wv.loadDataWithBaseURL("", html, "text/html","UTF-8", "");
                }

                @Override
                protected String doInBackground(Void... params)
                {
                    try
                    {
                        //create pdf document object from bytes
                        ByteBuffer bb = ByteBuffer.NEW(data);
                        PDFFile pdf = new PDFFile(bb);
                        //Get the first page from the pdf doc
                        PDFPage PDFpage = pdf.getPage(1, true);
                        //create a scaling value according to the WebView Width
                        final float scale = ViewSize / PDFpage.getWidth() * 0.95f;
                        //convert the page into a bitmap with a scaling value
                        Bitmap page = PDFpage.getImage((int)(PDFpage.getWidth() * scale), (int)(PDFpage.getHeight() * scale), null, true, true);
                        //save the bitmap to a byte array
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        page.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        stream.reset();
                        //convert the byte array to a base64 string
                        String base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                        //create the html + add the first image to the html
                        String html = "<!DOCTYPE html><html><body bgcolor=\"#b4b4b4\"><img src=\"data:image/png;base64,"+base64+"\" hspace=10 vspace=10><br>";
                        //loop though the rest of the pages and repeat the above
                        for(int i = 2; i <= pdf.getNumPages(); i++)
                        {
                            PDFpage = pdf.getPage(i, true);
                            page = PDFpage.getImage((int)(PDFpage.getWidth() * scale), (int)(PDFpage.getHeight() * scale), null, true, true);
                            page.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byteArray = stream.toByteArray();
                            stream.reset();
                            base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                            html += "<img src=\"data:image/png;base64,"+base64+"\" hspace=10 vspace=10><br>";
                        }
                        stream.close();
                        html += "</body></html>";
                        return html;
                    }
                    catch (Exception e)
                    {
                        Log.d("error", e.toString());
                    }
                    return null;
                }
            }.execute();
            System.gc();// run GC
        }
        catch (Exception e)
        {
            Log.d("error", e.toString());
        }
    }
}
