package com.example.weighttracker;

import static android.util.Log.println;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class edit_view extends AppCompatActivity {

    WeightDB _weight;
    UserModel _user;

    //create a list to hold the ids of the checkboxes marked
    List<CompoundButton> checkedRows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);

        try {
            onInit();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void openWeightForm(View view){
        Intent intent = new Intent(this, weight_entry.class);
        startActivity(intent);
    }

    public void openMain(View view){
        Intent intent = new Intent(this, main_screen.class);
        startActivity(intent);
    }

    public void onInit() throws ParseException {
        _weight = WeightDB.getInstance(this);
        _user = UserModel.getUserInstance();

        List<WeightsClass> allEntry = new ArrayList<>();
        allEntry = _weight.getAllWeights(_user);

        //start to add the dynamic rows
        //find the table object
        TableLayout _table = findViewById(R.id.editTable);

        //header
        TableRow _header = new TableRow(this);

        TextView _headerC1 = new TextView(this);
        _headerC1.setText("Selection");
        _headerC1.setBackgroundResource(R.color.white);
        _headerC1.setGravity(Gravity.CENTER);
        _headerC1.setPadding(10,10,10,10);
        _header.addView(_headerC1);

        TextView _headerC2 = new TextView(this);
        _headerC2.setText("Date");
        _headerC2.setBackgroundResource(R.color.white);
        _headerC2.setGravity(Gravity.CENTER);
        _headerC2.setPadding(10,10,10,10);
        _header.addView(_headerC2);

        TextView _headerC3 = new TextView(this);
        _headerC3.setText("Weight");
        _headerC3.setBackgroundResource(R.color.white);
        _headerC3.setGravity(Gravity.CENTER);
        _headerC3.setPadding(10,10,10,10);
        _header.addView(_headerC3);

        _table.addView(_header);

        for (int i = 0; i < allEntry.size(); i++){
            TableRow _row = new TableRow(this);

            CheckBox check = new CheckBox(this);
            check.setGravity(Gravity.CENTER);
            check.setPadding(10,10,10,10);
            check.setId(allEntry.get(i).getID());

            TableRow.LayoutParams rowParams = new TableRow.LayoutParams();
            rowParams.gravity = Gravity.CENTER;
            rowParams.span = 1;
            check.setLayoutParams(rowParams);

            check.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int _id = ((CompoundButton) v).getId();
                    String sID = String.valueOf(_id);

                    if(((CompoundButton) v).isChecked()){
                        checkedRows.add((CompoundButton) v); //add the checked box to the collection
/*                        for (CompoundButton a : checkedRows){
                            System.out.println(a.getId());
                        }*/
                    } else {
                        checkedRows.remove((CompoundButton) v); //remove the box from the collection
/*                        for (CompoundButton a : checkedRows){
                            System.out.println(a.getId());
                        }*/
                    }
                }
            });

            _row.addView(check);

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

            _table.addView(_row);
        }
    }

    public void delRow(View view){

        for(CompoundButton a : checkedRows){
            _weight.removeEntry(a.getId());
        }

        //refresh the activity
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public void editRow(View view){

        for (CompoundButton a : checkedRows){

            AlertDialog.Builder builder = new AlertDialog.Builder(edit_view.this);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_CLASS_NUMBER);

            builder
                    .setView(input)
                    .setTitle("Edit Record")
                    .setMessage("What is the new weight??")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            float newWeight = Float.valueOf(input.getText().toString());
                            _weight.updateEntry(a.getId(), newWeight, _user);

                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}