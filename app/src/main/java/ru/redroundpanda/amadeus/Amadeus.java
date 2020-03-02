package ru.redroundpanda.amadeus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

    static void speak(VoiceLine line, final Activity activity) {
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
                            // The normalized volume
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

    static void responseToInput(String input, Context context, Activity activity) {
        VoiceLine[] specificLines = null;
        input = input.toLowerCase();

        if (containsInput(input, context.getResources().getStringArray(R.array.secret))) {
            shaman_girls++;
            if (shaman_girls < 5) {
                specificLines = new VoiceLine[]{
                        voiceLines.get("ans_gah"),
                        voiceLines.get("ans_gah_extended")
                };
            } else {
                VoiceLine singleLine;
                switch (shaman_girls) {
                    case 5:
                        singleLine = voiceLines.get("ans_leskinen_awesome");
                        break;
                    case 6:
                        singleLine = voiceLines.get("ans_leskinen_nice");
                        break;
                    case 7:
                        singleLine = voiceLines.get("ans_leskinen_oh_no");
                        break;
                    case 8:
                        singleLine = voiceLines.get("ans_leskinen_shaman");
                        break;
                    case 9:
                    default:
                        singleLine = voiceLines.get("ans_leskinen_holy_cow");
                        shaman_girls = 0;
                        break;
                }
                specificLines = new VoiceLine[]{singleLine};
            }
        } else {
            String object = identifyObject(input.split(" "), objectMap, "obj_christina");
            String subject = identifySubject(input.split(" "), subjectMap);

            boolean question = false;
            for (String questionMarks : context.getResources().getStringArray(R.array.q_marks)) {
                if (containsInput(input, questionMarks)) {
                    question = true;
                    break;
                }
            }

            Log.i("Amadeus", input);
            Log.i("Amadeus", object + " " + subject + " " + (question ? "?" : "."));

            if (responseInputMap.containsKey(object) && responseInputMap.get(object).containsKey(subject) && responseInputMap.get(object).get(subject).containsKey(question)) {
                specificLines = responseInputMap.get(object).get(subject).get(question).toArray(new VoiceLine[0]);
            }

            if (specificLines == null) {
                specificLines = responseInputMap.get("system").get("EMPTY").get(false).toArray(new VoiceLine[0]);
            }
        }
        if (specificLines.length > 1) {
            Amadeus.speak(specificLines[new Random().nextInt(specificLines.length)], activity);
        } else {
            Amadeus.speak(specificLines[0], activity);
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
                    VoiceLine[] specificLines = responseInputMap.get("system").get("OK").get(false).toArray(new VoiceLine[0]);
                    Amadeus.speak(specificLines[new Random().nextInt(specificLines.length)], activity);
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
}
