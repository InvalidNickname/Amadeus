package ru.redroundpanda.amadeus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class Amadeus {

    private static final HashMap<String, HashMap<String, HashMap<Boolean, List<VoiceLine>>>> responseInputMap = new HashMap<>(); // карта <объект, субъект, вопрос, VoiceLine>
    private static final HashMap<String, String> objectMap = new HashMap<>(); // карта <слово, объект>
    private static final HashMap<String, String> subjectMap = new HashMap<>(); // карта <слово, субъект>
    static Boolean isSpeaking = false;
    static MediaPlayer player;
    private static int shaman_girls = -1;
    private static HashMap<String, VoiceLine> voiceLines;

    static void initialize(Context context) {
        voiceLines = VoiceLine.Line.getLines(context);

        // считывание ответов
        TypedArray tempVoiceLine = context.getResources().obtainTypedArray(R.array.voice_lines);
        for (int i = 0; i < tempVoiceLine.length(); i++) {
            int id = tempVoiceLine.getResourceId(i, 0);
            if (id > 0) {
                String[] temp = context.getResources().getStringArray(id);
                ArrayList<VoiceLine> lines = new ArrayList<>();
                for (int j = 3; j < temp.length; j++) {
                    lines.add(voiceLines.get(temp[j]));
                }
                switch (temp[2]) {
                    case "true":
                        fillMap(temp[0], temp[1], true, lines);
                        break;
                    case "false":
                        fillMap(temp[0], temp[1], false, lines);
                        break;
                    default:
                        fillMap(temp[0], temp[1], true, lines);
                        fillMap(temp[0], temp[1], false, lines);
                        break;
                }
            }
        }
        tempVoiceLine.recycle();

        // считывание объектов разговора
        TypedArray tempObject = context.getResources().obtainTypedArray(R.array.objects);
        for (int i = 0; i < tempObject.length(); i++) {
            int id = tempObject.getResourceId(i, 0);
            if (id > 0) {
                String[] temp = context.getResources().getStringArray(id);
                String object = context.getResources().getResourceEntryName(id);
                for (String key : temp) {
                    objectMap.put(key, object);
                }
            }
        }
        tempObject.recycle();

        // считывание субъектов разговора
        TypedArray tempSubject = context.getResources().obtainTypedArray(R.array.subjects);
        for (int i = 0; i < tempSubject.length(); i++) {
            int id = tempSubject.getResourceId(i, 0);
            if (id > 0) {
                String[] temp = context.getResources().getStringArray(id);
                String object = context.getResources().getResourceEntryName(id);
                for (String key : temp) {
                    subjectMap.put(key, object);
                }
            }
        }
        tempSubject.recycle();
    }

    private static void speak(VoiceLine line, final Activity activity) {
        if (line == null) {
            Log.w("Amadeus", "Response line not found");
            return;
        }

        final AnimationDrawable animation;
        final TextView subtitles = activity.findViewById(R.id.subtitles_text);
        final ImageView kurisu = activity.findViewById(R.id.kurisu);
        final ImageView mouth = activity.findViewById(R.id.mouth);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);

        Log.i("Amadeus", activity.getString(line.getSubtitle()));

        try {
            player = MediaPlayer.create(activity, line.getId());
            final Visualizer visualizer = new Visualizer(player.getAudioSessionId());

            if (settings.getBoolean("show_subtitles", true)) {
                subtitles.setText(line.getSubtitle());
            }

            kurisu.setImageResource(line.getMood());

            Resources res = activity.getResources();
            animation = (AnimationDrawable) Drawable.createFromXml(res, res.getXml(line.getExpression()));

            if (player.isPlaying()) {
                player.stop();
                player.release();
                visualizer.setEnabled(false);
                player = new MediaPlayer();
            }

            player.setOnPreparedListener(mp -> {
                isSpeaking = true;
                mp.start();
                visualizer.setEnabled(true);
            });

            player.setOnCompletionListener(mp -> {
                isSpeaking = false;
                mp.release();
                visualizer.setEnabled(false);

                activity.runOnUiThread(() -> mouth.setImageDrawable(animation.getFrame(0)));
            });

            visualizer.setEnabled(false);
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                            int sum = 0;
                            for (int i = 1; i < bytes.length; i++) {
                                sum += bytes[i] + 128;
                            }
                            // the normalized volume
                            final float normalized = sum / (float) bytes.length;

                            activity.runOnUiThread(() -> {
                                if (normalized > 120) {
                                    mouth.setImageDrawable(animation.getFrame(2));
                                } else if (normalized > 60) {
                                    mouth.setImageDrawable(animation.getFrame(1));
                                } else {
                                    mouth.setImageDrawable(animation.getFrame(0));
                                }
                            });
                        }

                        public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        }
                    }, Visualizer.getMaxCaptureRate() / 2, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // input must be already set to lower case
    static void responseToInput(String input, Context context, Activity activity) {
        if (containsInput(input, context.getResources().getStringArray(R.array.secret))) {
            shaman_girls++;
            switch (shaman_girls) {
                case 5:
                    speak(voiceLines.get("ans_leskinen_awesome"), activity);
                    break;
                case 6:
                    speak(voiceLines.get("ans_leskinen_nice"), activity);
                    break;
                case 7:
                    speak(voiceLines.get("ans_leskinen_oh_no"), activity);
                    break;
                case 8:
                    speak(voiceLines.get("ans_leskinen_shaman"), activity);
                    break;
                case 9:
                    speak(voiceLines.get("ans_leskinen_holy_cow"), activity);
                    shaman_girls = 0;
                    break;
                default:
                    speakSpecific("system", "SHAMAN_PREPARATION", false, activity);
                    break;
            }
        } else {
            String[] splitInput = input.split(" ");
            String object = identifyObject(splitInput, objectMap, "obj_christina");
            String subject = identifySubject(splitInput, subjectMap);

            boolean question = false;
            if (containsInput(input, context.getResources().getStringArray(R.array.q_marks))) {
                question = true;
            }

            Log.i("Amadeus", input);
            Log.i("Amadeus", object + " " + subject + " " + (question ? "?" : "."));

            speakSpecific(object, subject, question, activity);
        }
    }

    private static boolean containsInput(final String input, final String... strings) {
        for (String s : strings) {
            if (input.contains(s)) return true;
        }
        return false;
    }

    private static String identifyObject(String[] input, HashMap<String, String> data, String def) {
        String result = def;
        for (String i : input)
            for (String key : data.keySet()) {
                if ((key.startsWith("u/") && i.contains(key.substring(2))) || (!key.startsWith("u/") && i.equals(key))) {
                    result = data.get(key);
                    break;
                }
            }
        return result;
    }

    private static String identifySubject(String[] input, HashMap<String, String> data) {
        String result = "";
        for (String key : data.keySet()) {
            String[] splitKey = key.split(" ");
            int last = 0;
            int found = 0;
            for (String val : splitKey) {
                for (int i = last; i < input.length; i++) {
                    if (input[i].contains(val)) {
                        last = i + 1;
                        found++;
                        break;
                    }
                }
            }
            if (found == splitKey.length) {
                result = data.get(key);
            }
        }
        return result;
    }

    static void openApp(String[] input, Activity activity) {
        List<ApplicationInfo> packages = activity.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        String[] apps = activity.getResources().getStringArray(R.array.assistant_open);

        // проверяем что это одно из приложений, название которых можно перевести на английский
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < apps.length; j++) {
                if (apps[j].contains(input[i])) {
                    input[i] = activity.getResources().getStringArray(R.array.apps_to_open)[j];
                }
            }
        }

        for (ApplicationInfo packageInfo : packages) {
            for (String word : input) {
                if (packageInfo.packageName.contains(word)) {
                    Intent app;
                    speakSpecific("system", "OK", false, activity);
                    switch (packageInfo.packageName) {
                        case "com.android.phone": {
                            app = new Intent(Intent.ACTION_DIAL, null);
                            activity.startActivity(app);
                            break;
                        }
                        case "com.android.chrome": {
                            app = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                            app.setPackage(packageInfo.packageName);
                            activity.startActivity(app);
                            break;
                        }
                        default: {
                            app = activity.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
                            if (app != null) {
                                app.addCategory(Intent.CATEGORY_LAUNCHER);
                                activity.startActivity(app);
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    static void makeACall(String[] input, Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, 0);
        } else {
            String number = "";
            Cursor cur = activity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    if (cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).toLowerCase().equals(input[0].toLowerCase())) {
                        if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor pCur = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            if (pCur != null) {
                                while (pCur.moveToNext()) {
                                    number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                }
                                pCur.close();
                            }
                        }
                    }
                }
                cur.close();

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                Log.i("Amadeus", "Calling " + number);

                speakSpecific("system", "OK", false, activity);

                activity.startActivity(intent);
            }
        }
    }

    private static void fillMap(String object, String subject, boolean question, List<VoiceLine> lines) {
        HashMap<String, HashMap<Boolean, List<VoiceLine>>> fMap = responseInputMap.get(object);
        if (fMap == null) {
            HashMap<Boolean, List<VoiceLine>> temp1 = new HashMap<>();
            temp1.put(question, lines);
            HashMap<String, HashMap<Boolean, List<VoiceLine>>> temp2 = new HashMap<>();
            temp2.put(subject, temp1);
            responseInputMap.put(object, temp2);
        } else {
            HashMap<Boolean, List<VoiceLine>> tMap = fMap.get(subject);
            if (tMap == null) {
                HashMap<Boolean, List<VoiceLine>> temp1 = new HashMap<>();
                temp1.put(question, lines);
                fMap.put(subject, temp1);
            } else {
                tMap.put(question, lines);
            }
        }
    }


    static void speakSpecific(String object, String subject, boolean question, Activity activity) {
        speakSpecific(object, subject, question, activity, false);
    }

    private static void speakSpecific(String object, String subject, boolean question, Activity activity, boolean err) {
        HashMap<String, HashMap<Boolean, List<VoiceLine>>> t1 = responseInputMap.get(object);
        if (t1 == null) {
            Log.w("Amadeus", "Object \"" + object + "\" doesn't exist");
            // try to speak empty line only once, avoiding recursion if this line doesn't exist
            if (!err) speakSpecific("system", "EMPTY", false, activity, true);
            return;
        }
        HashMap<Boolean, List<VoiceLine>> t2 = t1.get(subject);
        if (t2 == null) {
            Log.w("Amadeus", "Subject \"" + subject + "\" for object \"" + object + "\" doesn't exist");
            // try to speak empty line only once, avoiding recursion if this line doesn't exist
            if (!err) speakSpecific("system", "EMPTY", false, activity, true);
            return;
        }
        List<VoiceLine> t3 = t2.get(question);
        if (t3 == null) {
            Log.w("Amadeus", (question ? "Question " : "Non question ") + "answer for object \"" + object + "\" and subject \"" + subject + "\" doesn't exist");
            // try to speak empty line only once, avoiding recursion if this line doesn't exist
            if (!err) speakSpecific("system", "EMPTY", false, activity, true);
            return;
        }
        VoiceLine[] specificLines = t3.toArray(new VoiceLine[0]);
        speak(specificLines[new Random().nextInt(specificLines.length)], activity);
    }

    static void speakRandom(Activity activity) {
        Random random = new Random();
        VoiceLine[] temp = voiceLines.values().toArray(new VoiceLine[0]);
        int id = random.nextInt(voiceLines.size());
        int raw_id = temp[id].getId();
        // чтобы не говорила пасхалки случайно
        while (raw_id == R.raw.leskinen_awesome || raw_id == R.raw.leskinen_nice || raw_id == R.raw.leskinen_oh_no || raw_id == R.raw.leskinen_shaman || raw_id == R.raw.leskinen_holy_cow) {
            id = random.nextInt(voiceLines.size());
            raw_id = temp[id].getId();
        }
        speak(temp[id], activity);
    }
}
