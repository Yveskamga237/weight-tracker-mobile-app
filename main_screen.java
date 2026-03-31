package com.example.weighttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class main_screen extends AppCompatActivity {

    WeightDB _weight;
    UserModel _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button editBtn = findViewById(R.id.buttonEditWeight);

        try {
            onInit();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        openSettings();
        return false;
    }

    public void openSettings(){
        Intent intent = new Intent(this, UserSettings.class);
        startActivity(intent);
    }

    public void openEdit(View view){
        Intent intent = new Intent(this, edit_view.class);
        startActivity(intent);
    }

    public void openWeightForm(View view){
        Intent intent = new Intent(this, weight_entry.class);
        startActivity(intent);
    }

    public void onNewGoal(View view){

        //call the singletons
        UserModel _user = UserModel.getUserInstance();
        WeightDB _db = WeightDB.getInstance(this);

        //call the singletons to find the user and weights db
        AlertDialog.Builder builder = new AlertDialog.Builder(main_screen.this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);

        builder
                .setView(input)
                .setTitle("Enter New Goal Weight")
                .setMessage("Change your current goal of " + _user.getGoal() + " lbs?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        float userGoal = Float.valueOf(input.getText().toString());
                        _user.setGoal(userGoal);
                        _db.addGoal(_user);
                        _user.setGoal(userGoal);

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);

                    }
                })
                .setNegativeButton("Cancel", null) //Do nothing on no
                .show();
    }

    private void onInit() throws ParseException {

        _weight = WeightDB.getInstance(this);
        _user = UserModel.getUserInstance();

        List<WeightsClass> allEntry = new ArrayList<>();
        allEntry = _weight.getAllWeights(_user);

        //start to add the dynamic rows
        //find the table object
        TableLayout _table = findViewById(R.id.weight_table);

        //header
        TableRow _header = new TableRow(this);

        TextView _headerC1 = new TextView(this);
        _headerC1.setText("Date");
        _headerC1.setBackgroundResource(R.color.white);
        _headerC1.setGravity(Gravity.CENTER);
        _headerC1.setPadding(10,10,10,10);
        _header.addView(_headerC1);

        TextView _headerC2 = new TextView(this);
        _headerC2.setText("Weight");
        _headerC2.setBackgroundResource(R.color.white);
        _headerC2.setGravity(Gravity.CENTER);
        _headerC2.setPadding(10,10,10,10);
        _header.addView(_headerC2);

        TextView _headerC3 = new TextView(this);
        _headerC3.setText("Weight Remaining");
        _headerC3.setBackgroundResource(R.color.white);
        _headerC3.setGravity(Gravity.CENTER);
        _headerC3.setPadding(10,10,10,10);
        _header.addView(_headerC3);

        _table.addView(_header);

        for (int i = 0; i < allEntry.size(); i++){
            TableRow _row = new TableRow(this);
            TextView _textC1 = new TextView(this);
            _textC1.setText(allEntry.get(i).getDate());
            _textC1.setTextSize(14);
            _textC1.setBackgroundResource(R.color.white);
            _textC1.setGravity(Gravity.CENTER_HORIZONTAL);
            _textC1.setPadding(10,10,10,10);
            _row.addView(_textC1);

            TextView _textC2 = new TextView(this);
            _textC2.setText(String.valueOf(allEntry.get(i).getWeight()));
            _textC2.setTextSize(14);
            _textC2.setBackgroundResource(R.color.white);
            _textC2.setGravity(Gravity.CENTER_HORIZONTAL);
            _textC2.setPadding(10,10,10,10);
            _row.addView(_textC2);

            TextView _textC3 = new TextView(this);
            float weightRemain = allEntry.get(i).getWeight() - _user.getGoal();
            String stringWeightRemain = String.valueOf(weightRemain);

            _textC3.setText(stringWeightRemain);
            _textC3.setTextSize(14);
            _textC3.setBackgroundResource(R.color.white);
            _textC3.setGravity(Gravity.CENTER_HORIZONTAL);
            _textC3.setPadding(10,10,10,10);
            _row.addView(_textC3);

            _table.addView(_row);

        }
    }
}