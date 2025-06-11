package com.example.cibilparser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request storage permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        Button parseButton = findViewById(R.id.parse_button);
        parseButton.setOnClickListener(v -> {
            try {
                String pdfPath = "/storage/emulated/0/Download/Lonkar_Cibil.pdf";
                String csvPath = "/storage/emulated/0/Download/cibil_data.csv";

                // Parse CIBIL PDF and save to CSV
                List<LoanDetails> loans = CibilParser.detectAndParse(pdfPath);
                CibilParser.saveToCsv(csvPath, loans);

                Toast.makeText(this, "Parsing complete! CSV saved to: " + csvPath, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
