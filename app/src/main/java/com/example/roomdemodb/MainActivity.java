package com.example.roomdemodb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView txtData;
    EditText editData;
    TextView txtIdData;

    List<Data> dataList = null;
    Handler handler = new Handler();

    String TAG = "--";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataDB.getInstance(this);
        txtData = findViewById(R.id.txtData);
        editData = findViewById(R.id.editData);
        txtData.setMovementMethod(new ScrollingMovementMethod());
        txtIdData = findViewById(R.id.deleteIdInput);

     initDatabase();
//     refreshUI();

    }

    /**
     * -------------------   Asynchronous methods ------------------
     */

    /**
     * initDatabase -   Populates Data table with some sample data.
     *                  Sets all fields including id numbers.
     *                  The data is inserted on a worker thread.
     *                  This method does not refresh the user interface.
     */

    private void initDatabase()
    {
        Runnable initDB = ()-> {
            DataDB dataDB = DataDB.getInstance(MainActivity.this);
            for (int i = 1; i < 5; i++) {
                Data data = new Data();
                data.id=i;
                data.data = "this is data item added from runnable: "+i;
                dataDB.dataDAO().insert(data);

            }
            dataList =  dataDB.dataDAO().findAllData();
            handler.post(showDataList);
        };

        Thread thread = new Thread(initDB);
        thread.start();

    }

    /**
     * shoeDataList -   This Runnable is used to to update the user interface
     *                  with data that has been added to the dataList.
     *                  This Runnable must run on the main thread.  It is intended
     *                  to be 'posted' by worker threads.
     */

    Runnable showDataList = ()->
    {
        txtData.setText("");

        for (Data item : dataList) {
            txtData.append(item.id+ " "+item.data+"\n");
        }
    };


    /**
     * refreshUI -  This methods updates the user interface using data
     *              that has been inserted into in the Data table.
     *              The 'findAllData' query is run on a worker thread,
     *              After retrieving the data, the user interface is updated
     *              in the main UI thread by posting 'showDataList' runnable.
     */

    private void refreshUI() {
        txtData.setText("");
        Runnable updateUI = ()->{
            DataDB dataDB = DataDB.getInstance(MainActivity.this);
            dataList =  dataDB.dataDAO().findAllData();
            handler.post(showDataList);
        };

        Thread thread = new Thread(updateUI);
        thread.start();
    }


    /**
     * onAddData -  This is an event handler method that inserts a new record into
     *              the Data table.  The insert operation is performed on a worker
     *              thread.  After the data has been inserted, the user interface is
     *              updated by posting 'showDataList' to the main UI thread.
     */

    public void onAddData (View view)
    {
        TextView txtEditData = (TextView) editData;
        String dataString =  txtEditData.getText().toString();
        Data data = new Data();
        data.data = dataString;
        Log.i("--", "onAddData: " + dataString);

        Runnable runInsert = ()->{
            DataDB dataDB = DataDB.getInstance(this);
            dataDB.dataDAO().insert(data);
           dataList = dataDB.dataDAO().findAllData();
//            for (Data item: dataList) {
//                Log.i(TAG, "onAddData: " + item.data);
//            }
            handler.post(showDataList);
        };

        Thread thread = new Thread(runInsert);
        thread.start();
    }

    public void onDeleteDataById(View view){
//        Long dataId = Long.valueOf(String.valueOf(txtIdData));
        TextView txtEditData = (TextView) txtIdData;
        String dataString =  txtEditData.getText().toString();
        if(dataString.length() !=0) {

//        Log.i(TAG,"delete ID "+dataString);
            Runnable runDelete = () -> {
                DataDB dataDB = DataDB.getInstance(this);
                dataDB.dataDAO().deleteById(Long.valueOf(dataString));
                dataList = dataDB.dataDAO().findAllData();
                handler.post(showDataList);
            };
            Thread thread = new Thread(runDelete);
            thread.start();
        }

    }

    public void onDeleteAllData(View view){

        Runnable runDeleteAll = ()-> {
            DataDB dataDB = DataDB.getInstance(this);
            dataDB.dataDAO().deleteAll();
            dataList = dataDB.dataDAO().findAllData();
            handler.post(showDataList);
        };
        Thread thread = new Thread(runDeleteAll);
        thread.start();
        //.
//          refreshUI();
//        handler.post(showDataList);
    }
}