package ru.redroundpanda.amadeus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.HashMap;

class VoiceLine {
    final private int id;
    final private int mood;
    final private int subtitle;

    @SuppressLint("ResourceType")
    private VoiceLine(int id, Context context) {
        TypedArray data = context.getResources().obtainTypedArray(id);
        this.id = context.getResources().getIdentifier(data.getString(0), "raw", context.getPackageName());
        String moodString = data.getString(1);
        switch (moodString) {
            case "happy":
                mood = R.drawable.kurisu_happy;
                break;
            case "pissed":
                mood = R.drawable.kurisu_pissed;
                break;
            case "annoyed":
                mood = R.drawable.kurisu_annoyed;
                break;
            case "angry":
                mood = R.drawable.kurisu_angry;
                break;
            case "blush":
                mood = R.drawable.kurisu_blush;
                break;
            case "side":
                mood = R.drawable.kurisu_side;
                break;
            case "sad":
                mood = R.drawable.kurisu_sad;
                break;
            case "normal":
                mood = R.drawable.kurisu_normal;
                break;
            case "sleepy":
                mood = R.drawable.kurisu_eyes_closed;
                break;
            case "winking":
                mood = R.drawable.kurisu_winking;
                break;
            case "disappointed":
                mood = R.drawable.kurisu_disappointed;
                break;
            case "indifferent":
                mood = R.drawable.kurisu_indifferent;
                break;
            case "sided_pleasant":
                mood = R.drawable.kurisu_sided_pleasant;
                break;
            case "sided_worried":
                mood = R.drawable.kurisu_sided_worried;
                break;
            case "sided_angry":
                mood = R.drawable.kurisu_sided_angry;
                break;
            default:
                // недостижимо
                mood = R.drawable.kurisu_happy1;
                break;
        }
        this.subtitle = data.getResourceId(2, 0);
        data.recycle();
    }

    int getId() {
        return id;
    }

    int getMood() {
        return mood;
    }

    int getSubtitle() {
        return subtitle;
    }

    static class Line {
        static final HashMap<String, VoiceLine> voiceLines = new HashMap<>();

        static HashMap<String, VoiceLine> getLines(Context context) {
            Resources resources = context.getResources();
            TypedArray data = resources.obtainTypedArray(R.array.answers);
            for (int i = 0; i < data.length(); ++i) {
                int id = data.getResourceId(i, 0);
                if (id > 0) {
                    voiceLines.put(resources.getResourceEntryName(id), new VoiceLine(id, context));
                }
            }
            data.recycle();
            return voiceLines;
        }
    }
}
