package com.github.handstands.spoked;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class PresetSentences extends AppCompatActivity implements OnClickListener, OnInitListener {
    private int MY_DATA_CHECK_CODE = 0;
    private String TAG = "Spoked";
    private TextToSpeech myTTS;
    private ListView listView;
    private static String SAVED_SENTENCE_FILE = "savedSentences.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_sentences);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        Button speakButton = (Button)findViewById(R.id.speak);
        assert speakButton != null;
        speakButton.setOnClickListener(this);
        TAG = getString(R.string.app_name);
        Button stopButton = (Button)findViewById(R.id.stop);
        assert stopButton != null;
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTTS.stop();
            }
        });

        Button clearButton = (Button)findViewById(R.id.clear);
        assert clearButton != null;
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText sentence = (EditText)findViewById(R.id.sentence);
                if (sentence != null) {
                    sentence.setText("");
                }
            }
        });

        Button addButton = (Button)findViewById(R.id.add);
        assert addButton != null;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter adapter = (ArrayAdapter)listView.getAdapter();
                EditText sentence = (EditText)findViewById(R.id.sentence);

                String sentenceText = null;
                if (sentence != null) {
                    sentenceText = sentence.getText().toString().replace("\n", "");
                }
                if(sentenceText != null && !sentenceText.isEmpty()) {
                    boolean alreadyIn = false;
                    for(int i=0;i<adapter.getCount();i++) {
                        String tmp = adapter.getItem(i).toString();
                        alreadyIn = tmp.equals(sentenceText);
                    }
                    if(!alreadyIn) {
                        adapter.add(sentenceText);
                    }
                }
                storeSavedSentences();
            }
        });
        listView = (ListView)findViewById(R.id.presetList);
        if(!checkSavedSentences(SAVED_SENTENCE_FILE)) {
            Log.d(TAG, "No saved sentences.");
            Log.d(TAG, getFilesDir().toString());
            Log.d(TAG, Arrays.toString(fileList()));
            makeDefaultSavedSentences(SAVED_SENTENCE_FILE);
            String d = "No go";
            try {
                d = readFile(SAVED_SENTENCE_FILE);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, d);

        }

        String[] values = loadSavedSentences(SAVED_SENTENCE_FILE);
        if(values != null) {
            Log.d(TAG, "Populating list");
            populateList(values);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                EditText sentence = (EditText)findViewById(R.id.sentence);
                String itemValue = (String) listView.getItemAtPosition(position);
                if(sentence != null && !itemValue.isEmpty()) {
                    sentence.setText(itemValue);
                }
            }

        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Looooooong click");
                ArrayAdapter adapter = (ArrayAdapter)listView.getAdapter();
                adapter.remove(adapter.getItem(position));
                storeSavedSentences();
                return false;
            }
        });
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preset_sentences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);

    }

    public void onClick(View v) {
        EditText enteredText = (EditText)findViewById(R.id.sentence);
        assert enteredText != null;
        String words = enteredText.getText().toString();
        speakWords(words);
    }

    private void speakWords(String speech) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else {
            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void populateList(String[] values) {
        ArrayList<String> tempList= new ArrayList<>();
        tempList.addAll(Arrays.asList(values));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, tempList);
        listView.setAdapter(adapter);
    }

    private boolean checkSavedSentences(String filename) {
        try {
            String data = readFile(filename);
            return parseSavedSentences(data) != null;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String[] parseSavedSentences(String data) {
        if(!data.isEmpty()){
            return data.split("\n");
        }
        else {
            return null;
        }
    }

    private void makeDefaultSavedSentences(String filename) {
        File file = new File(getFilesDir(), filename);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            String tmp = getString(R.string.default_sentence);

            try {
                fos.write(tmp.getBytes());
                fos.close();
            }
            catch (IOException e) {
                Log.d(TAG, "Write failed");
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String[] loadSavedSentences(String filename) {
        try {
            return parseSavedSentences(readFile(filename));
            //return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

    }

    public void onInit(int initStatus) {
        Locale l;
        if (initStatus == TextToSpeech.SUCCESS) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                l = myTTS.getDefaultVoice().getLocale();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                l = myTTS.getDefaultLanguage();
            } else {
                l = Locale.getDefault();
            }
            int a = myTTS.isLanguageAvailable(l);
            switch(a) {
                case TextToSpeech.LANG_NOT_SUPPORTED:
                    Log.d(TAG, "Not supported");
                    break;
                case TextToSpeech.LANG_MISSING_DATA:
                    Log.d(TAG, "Missing data");
                    break;
                case TextToSpeech.LANG_AVAILABLE:
                    Log.d(TAG, "Available");
                    break;
                case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                    Log.d(TAG, "Country available");
                    break;
                case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                    Log.d(TAG, "OK");
                    break;
            }
            if(a == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE || a == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                myTTS.setLanguage(l);
                Log.d(TAG, "Local");
            }
            else {
                myTTS.setLanguage(Locale.US);
            }
        }
    }
    public void storeSavedSentences() {
        File file = new File(getFilesDir(), SAVED_SENTENCE_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ArrayAdapter adapter = (ArrayAdapter)listView.getAdapter();
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<adapter.getCount();i++) {
                sb.append(adapter.getItem(i));
                sb.append("\n");
            }
            try {
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    public String readFile(String filename) throws IOException {

        File file = new File(getFilesDir(), filename);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        StringBuilder stringBuffer = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {

            stringBuffer.append(line).append("\n");
        }

        return stringBuffer.toString();

    }
}
