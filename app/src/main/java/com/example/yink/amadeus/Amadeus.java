package com.example.yink.amadeus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class Amadeus {

    static Boolean isSpeaking = false;
    static MediaPlayer player;
    private static int shaman_girls = -1;
    private static HashMap<String, VoiceLine> voiceLines;
    private static HashMap<String[], List<VoiceLine>> responseInputMap = new HashMap<>();

    static void initialize(Context context) {
        voiceLines = VoiceLine.Line.getLines(context);
        responseInputMap.put(
                context.getResources().getStringArray(R.array.t_christina),
                Arrays.asList(
                        voiceLines.get("CHRISTINA"),
                        voiceLines.get("WHY_CHRISTINA"),
                        voiceLines.get("SHOULD_CHRISTINA"),
                        voiceLines.get("NO_TINA")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.forbidden_names),
                Arrays.asList(
                        voiceLines.get("DONT_CALL_ME_LIKE_THAT")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.atchannel),
                Arrays.asList(
                        voiceLines.get("SENPAI_DONT_TELL"),
                        voiceLines.get("STILL_NOT_HAPPY")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.maho),
                Arrays.asList(
                        voiceLines.get("SENPAI_QUESTION"),
                        voiceLines.get("SENPAI_WHAT_WE_TALKING"),
                        voiceLines.get("SENPAI_QUESTIONMARK"),
                        voiceLines.get("SENPAI_WHO_IS_THIS")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.time_machine),
                Arrays.asList(
                        voiceLines.get("TM_NONCENCE"),
                        voiceLines.get("TM_YOU_SAID"),
                        voiceLines.get("TM_NO_EVIDENCE"),
                        voiceLines.get("TM_DONT_KNOW"),
                        voiceLines.get("TM_NOT_POSSIBLE")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.amadeus),
                Arrays.asList(
                        voiceLines.get("HUMANS_SOFTWARE"),
                        voiceLines.get("MEMORY_COMPLEXITY"),
                        voiceLines.get("SECRET_DIARY"),
                        voiceLines.get("MODIFIYING_MEMORIES"),
                        voiceLines.get("MEMORIES_CHRISTINA")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.hi),
                Arrays.asList(
                        voiceLines.get("HELLO"),
                        voiceLines.get("NICE_TO_MEET_OKABE"),
                        voiceLines.get("PLEASED_TO_MEET"),
                        voiceLines.get("LOOKING_FORWARD_TO_WORKING")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.hentai),
                Arrays.asList(
                        voiceLines.get("DEVILISH_PERVERT"),
                        voiceLines.get("PERVERT_CONFIRMED"),
                        voiceLines.get("PERVERT_IDIOT")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.robotics),
                Arrays.asList(
                        voiceLines.get("HEHEHE")
                ));
    }

    static void speak(VoiceLine line, final Activity activity) {
        final AnimationDrawable animation;
        final TextView subtitles = activity.findViewById(R.id.subtitles_text);
        final ImageView kurisu = activity.findViewById(R.id.kurisu);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);

        try {
            player = MediaPlayer.create(activity, line.getId());
            final Visualizer visualizer = new Visualizer(player.getAudioSessionId());

            if (settings.getBoolean("show_subtitles", false)) {
                subtitles.setText(line.getSubtitle());
            }

            Resources res = activity.getResources();
            animation = (AnimationDrawable) Drawable.createFromXml(res, res.getXml(line.getMood()));

            if (player.isPlaying()) {
                player.stop();
                player.release();
                visualizer.setEnabled(false);
                player = new MediaPlayer();
            }

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isSpeaking = true;
                    mp.start();
                    visualizer.setEnabled(true);
                }
            });

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isSpeaking = false;
                    mp.release();
                    visualizer.setEnabled(false);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            kurisu.setImageDrawable(animation.getFrame(0));
                        }
                    });
                }
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

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (normalized > 50) {
                                        kurisu.setImageDrawable(animation.getFrame((int) Math.ceil(Math.random() * 2)));
                                    } else {
                                        kurisu.setImageDrawable(animation.getFrame(0));
                                    }
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
                        voiceLines.get("GAH"),
                        voiceLines.get("GAH_EXTENDED")
                };
            } else {
                VoiceLine singleLine;
                switch (shaman_girls) {
                    case 5:
                        singleLine = voiceLines.get("LESKINEN_AWESOME");
                        break;
                    case 6:
                        singleLine = voiceLines.get("LESKINEN_NICE");
                        break;
                    case 7:
                        singleLine = voiceLines.get("LESKINEN_OH_NO");
                        break;
                    case 8:
                        singleLine = voiceLines.get("LESKINEN_SHAMAN");
                        break;
                    case 9:
                    default:
                        singleLine = voiceLines.get("LESKINEN_HOLY_COW");
                        shaman_girls = 0;
                        break;
                }
                specificLines = new VoiceLine[]{singleLine};
            }
        } else {
            for (String[] input_bundle : responseInputMap.keySet()) {
                for (String inputString : input_bundle) {
                    if (containsInput(input, inputString)) {
                        specificLines = responseInputMap.get(input_bundle).toArray(new VoiceLine[0]);
                        break;
                    }
                }
            }

            if (specificLines == null) {
                specificLines = new VoiceLine[]{
                        voiceLines.get("ASK_ME"),
                        voiceLines.get("WHAT_DO_YOU_WANT"),
                        voiceLines.get("WHAT_IS_IT"),
                        voiceLines.get("HEHEHE"),
                        voiceLines.get("WHY_SAY_THAT"),
                        voiceLines.get("YOU_SURE")
                };
            }
        }
        int intTarget = 0;
        if (specificLines.length > 1) {
            intTarget = new Random().nextInt(specificLines.length);
        }
        Amadeus.speak(specificLines[intTarget], activity);
    }

    private static boolean containsInput(final String input, final String... strings) {
        for (String s : strings) {
            if (input.contains(s)) return true;
        }
        return false;
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
                    Amadeus.speak(voiceLines.get("OK"), activity);
                    switch (packageInfo.packageName) {
                        /* Exceptional cases */
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

}
