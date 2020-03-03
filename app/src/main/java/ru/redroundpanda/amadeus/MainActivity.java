package ru.redroundpanda.amadeus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Random randomgen = new Random();
    private HashMap<String, VoiceLine> voiceLines;
    private String recogLang;
    private String[] contextLang;
    private SpeechRecognizer sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        voiceLines = VoiceLine.Line.getLines(this);

        ImageView kurisu = findViewById(R.id.kurisu);
        ImageView subtitlesBackground = findViewById(R.id.subtitles_background);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        recogLang = settings.getString("recognition_lang", getString(R.string.default_recognition_lang));
        contextLang = recogLang.split("-");
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new Listener());

        if (!settings.getBoolean("show_subtitles", true)) {
            subtitlesBackground.setVisibility(View.INVISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        Amadeus.initialize(this);
        Amadeus.speakSpecific("system", "HELLO", false, this);

        kurisu.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Amadeus.isSpeaking) {
                    if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        promptSpeechInput();
                    } else {
                        Amadeus.speakSpecific("system", "NO_PERMISSION", false, this);
                    }
                }

            } else if (!Amadeus.isSpeaking) {
                promptSpeechInput();
            }
        });

        kurisu.setOnLongClickListener(view -> {
            if (!Amadeus.isSpeaking) {
                VoiceLine[] temp = voiceLines.values().toArray(new VoiceLine[0]);
                int id = randomgen.nextInt(voiceLines.size());
                int raw_id = temp[id].getId();
                // чтобы не говорила пасхалки случайно
                while (raw_id == R.raw.leskinen_awesome || raw_id == R.raw.leskinen_nice || raw_id == R.raw.leskinen_oh_no || raw_id == R.raw.leskinen_shaman || raw_id == R.raw.leskinen_holy_cow) {
                    id = randomgen.nextInt(voiceLines.size());
                    raw_id = temp[id].getId();
                }
                Amadeus.speak(temp[id], MainActivity.this);
            }
            return true;
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LangContext.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sr != null) sr.destroy();
        if (Amadeus.player != null) Amadeus.player.release();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, recogLang);

        sr.startListening(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                Context context = LangContext.load(getApplicationContext(), contextLang[0]);
                ArrayList<String> input = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Amadeus.responseToInput(input.get(0), context, MainActivity.this);
            }
        }
    }

    private class Listener implements RecognitionListener {

        public void onReadyForSpeech(Bundle params) {
        }

        public void onBeginningOfSpeech() {
        }

        public void onRmsChanged(float rmsdB) {
        }

        public void onBufferReceived(byte[] buffer) {
        }

        public void onEndOfSpeech() {
        }

        public void onError(int error) {
            sr.cancel();
            Amadeus.speakSpecific("system", "ERROR", false, MainActivity.this);
        }

        public void onResults(Bundle results) {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String input = (String) data.get(0);
            input = input.replace(".", "").toLowerCase();
            String[] splitInput = input.split(" ");

            Context context = LangContext.load(getApplicationContext(), contextLang[0]);

            // check for assistant commands
            boolean assistant = false;
            for (String s : getResources().getStringArray(R.array.assistant)) {
                if (splitInput[0].equalsIgnoreCase(s)) {
                    assistant = true;
                    break;
                }
            }

            if (splitInput.length > 2 && assistant) {
                String cmd = splitInput[1];
                String[] args = new String[splitInput.length - 2];
                System.arraycopy(splitInput, 2, args, 0, splitInput.length - 2);
                if (cmd.contains(context.getString(R.string.open))) {
                    Amadeus.openApp(args, MainActivity.this);
                } else if (cmd.contains(context.getString(R.string.call))) {
                    Amadeus.makeACall(args, MainActivity.this);
                } else {
                    Amadeus.responseToInput(input, context, MainActivity.this);
                }
            } else {
                Amadeus.responseToInput(input, context, MainActivity.this);
            }
        }

        public void onPartialResults(Bundle partialResults) {
        }

        public void onEvent(int eventType, Bundle params) {
        }

    }

}
