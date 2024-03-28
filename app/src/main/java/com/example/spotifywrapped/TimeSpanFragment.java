package com.example.spotifywrapped;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TimeSpanFragment extends AppCompatActivity {
    private Spinner timespanSpinner;
    private Button generateSummaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timespan_fragment);

        timespanSpinner = findViewById(R.id.timespan_spinner);
        generateSummaryButton = findViewById(R.id.generate_summary_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.timespan_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timespanSpinner.setAdapter(adapter);

        generateSummaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedTimespan = timespanSpinner.getSelectedItem().toString();
                generateSummary(selectedTimespan);
            }
        });
    }

    private void generateSummary(String selectedTimespan) {
        // Call Spotify API with selectedTimespan to fetch user data
        // Display Wrapped summary for the selected timespan
        Toast.makeText(this, "Generating summary for " + selectedTimespan, Toast.LENGTH_SHORT).show();
    }
}
