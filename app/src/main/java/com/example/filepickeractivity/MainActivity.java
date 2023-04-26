package com.example.filepickeractivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class MainActivity extends AppCompatActivity
{

    Button myButton;
    TextView myTextView;
    /*
     *  ActivityResultLauncher launches an intent once there onActivityResult method has finished executing
     * The onActivityResult method is important in that I/O operations are memory intensive and therefore slow
     * The onActivityResult ensure that only when the read/write is complete, before calling the intent that holds the data read from file.
     * LifeCycles https://developer.android.com/topic/libraries/architecture/lifecycle
     */
    ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTextView = findViewById(R.id.textView);
        myButton = findViewById(R.id.button);
        // initializing the activityResult launcher
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if(data!=null)
                {
                    // get the location of the selected file
                    // read up about difference between URI and filepath
                    Uri  fileUril = data.getData();
                    String fileName= getFileName(fileUril); // in case you want to know the file name ..other ways of opening file need this
                    String lines=" ";


                    try {
                        // Java stuff we know reading a file.
                        // getContentResolver().openInputStream  [ check this out ]

                        InputStream myInputStream =getContentResolver().openInputStream(fileUril);

                        BufferedReader br = new BufferedReader(new InputStreamReader(myInputStream));
                       String line= br.readLine();
                       //there might be other efficient ways of reading the file contents
                       while(line!=null)
                       {
                           lines+=line+"\n";
                           line= br.readLine();
                       }
                       myTextView.setText(lines);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        // when button is clicked we call an implicit intent with action get content
        myButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*"); // type */* any file, "application/pdf" only pdf, ect..
                        activityResultLauncher.launch(intent);
                    }
                }
        );

    }
    // in case you need to know the filename of the picked file
    public String getFileName(Uri uri) throws IllegalArgumentException
    {
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor.getCount()<=0)
        {
            cursor.close();
        }
        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }



}