package ru.redroundpanda.amadeus;

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

    private static final HashMap<String[], List<VoiceLine>> responseInputMap = new HashMap<>();
    static Boolean isSpeaking = false;
    static MediaPlayer player;
    private static int shaman_girls = -1;
    private static HashMap<String, VoiceLine> voiceLines;

    static void initialize(Context context) {
        voiceLines = VoiceLine.Line.getLines(context);
        responseInputMap.put(
                context.getResources().getStringArray(R.array.t_christina),
                Arrays.asList(
                        voiceLines.get("christina"),
                        voiceLines.get("why_christina"),
                        voiceLines.get("should_christina"),
                        voiceLines.get("no_tina")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.forbidden_names),
                Arrays.asList(
                        voiceLines.get("dont_call_me_like_that")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.atchannel),
                Arrays.asList(
                        voiceLines.get("senpai_dont_tell"),
                        voiceLines.get("still_not_happy")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.maho),
                Arrays.asList(
                        voiceLines.get("senpai_question"),
                        voiceLines.get("senpai_what_we_talking"),
                        voiceLines.get("senpai_questionmark"),
                        voiceLines.get("senpai_who_is_this")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.time_machine),
                Arrays.asList(
                        voiceLines.get("tm_noncence"),
                        voiceLines.get("tm_you_said"),
                        voiceLines.get("tm_no_evidence"),
                        voiceLines.get("tm_dont_know"),
                        voiceLines.get("tm_not_possible")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.amadeus),
                Arrays.asList(
                        voiceLines.get("humans_software"),
                        voiceLines.get("memory_complexity"),
                        voiceLines.get("secret_diary"),
                        voiceLines.get("modifiying_memories"),
                        voiceLines.get("memories_christina")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.hi),
                Arrays.asList(
                        voiceLines.get("hello"),
                        voiceLines.get("nice_to_meet_okabe"),
                        voiceLines.get("pleased_to_meet"),
                        voiceLines.get("looking_forward_to_working")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.hentai),
                Arrays.asList(
                        voiceLines.get("devilish_pervert"),
                        voiceLines.get("pervert_confirmed"),
                        voiceLines.get("pervert_idiot")
                ));
        responseInputMap.put(
                context.getResources().getStringArray(R.array.robotics),
                Arrays.asList(
                        voiceLines.get("hehehe")
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
                        voiceLines.get("gah"),
                        voiceLines.get("gah_extended")
                };
            } else {
                VoiceLine singleLine;
                switch (shaman_girls) {
                    case 5:
                        singleLine = voiceLines.get("leskinen_awesome");
                        break;
                    case 6:
                        singleLine = voiceLines.get("leskinen_nice");
                        break;
                    case 7:
                        singleLine = voiceLines.get("leskinen_oh_no");
                        break;
                    case 8:
                        singleLine = voiceLines.get("leskinen_shaman");
                        break;
                    case 9:
                    default:
                        singleLine = voiceLines.get("leskinen_holy_cow");
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
                        voiceLines.get("ask_me"),
                        voiceLines.get("what_do_you_want"),
                        voiceLines.get("what_is_it"),
                        voiceLines.get("hehehe"),
                        voiceLines.get("why_say_that"),
                        voiceLines.get("you_sure")
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
                    Amadeus.speak(voiceLines.get("ok"), activity);
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
