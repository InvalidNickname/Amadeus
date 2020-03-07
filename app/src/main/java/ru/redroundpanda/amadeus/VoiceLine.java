package ru.redroundpanda.amadeus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.HashMap;

class VoiceLine {
    final private int id;
    final private int mood, expression, eyes;
    final private int subtitle;

    @SuppressLint("ResourceType")
    private VoiceLine(int id, Context context) {
        TypedArray data = context.getResources().obtainTypedArray(id);
        this.id = context.getResources().getIdentifier(data.getString(0), "raw", context.getPackageName());
        String moodString = data.getString(1);
        switch (moodString) {
            case "happy":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_happy;
                expression = R.drawable.mouth_happy;
                break;
            case "pissed":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_pissed;
                expression = R.drawable.mouth_pissed;
                break;
            case "annoyed":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_annoyed;
                expression = R.drawable.mouth_annoyed;
                break;
            case "angry":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_angry;
                expression = R.drawable.mouth_angry;
                break;
            case "blush":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_blush;
                expression = R.drawable.mouth_blush;
                break;
            case "side":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_side;
                expression = R.drawable.mouth_side;
                break;
            case "sad":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_sad;
                expression = R.drawable.mouth_sad;
                break;
            case "normal":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_normal;
                expression = R.drawable.mouth_normal;
                break;
            case "sleepy":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_closed;
                expression = R.drawable.mouth_eyes_closed;
                break;
            case "winking":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_winking;
                expression = R.drawable.mouth_winking;
                break;
            case "disappointed":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_disappointed;
                expression = R.drawable.mouth_disappointed;
                break;
            case "indifferent":
                mood = R.drawable.kurisu_front;
                eyes = R.drawable.eyes_indifferent;
                expression = R.drawable.mouth_indifferent;
                break;
            case "sided_pleasant":
                mood = R.drawable.kurisu_sided;
                eyes = R.drawable.eyes_sided_pleasant;
                expression = R.drawable.mouth_sided_pleasant;
                break;
            case "sided_worried":
                mood = R.drawable.kurisu_sided;
                eyes = R.drawable.eyes_sided_worried;
                expression = R.drawable.mouth_sided_worried;
                break;
            case "sided_angry":
                mood = R.drawable.kurisu_sided;
                eyes = R.drawable.eyes_sided_angry;
                expression = R.drawable.mouth_sided_angry;
                break;
            case "sided_blush":
                mood = R.drawable.kurisu_sided;
                eyes = R.drawable.eyes_sided_blush;
                expression = R.drawable.mouth_sided_blush;
                break;
            case "sided_eyes_closed":
                mood = R.drawable.kurisu_sided;
                eyes = R.drawable.eyes_sided_closed;
                expression = R.drawable.mouth_sided_eyes_closed;
                break;
            case "sided_surprised":
                mood = R.drawable.kurisu_sided;
                eyes = R.drawable.eyes_sided_surprised;
                expression = R.drawable.mouth_sided_surprised;
                break;
            case "sided_thinking":
                mood = R.drawable.kurisu_sided;
                eyes = R.drawable.eyes_sided_thinking;
                expression = R.drawable.mouth_sided_thinking;
                break;
            case "back":
                mood = R.drawable.kurisu_back;
                eyes = 0;
                expression = 0;
                break;
            default:
                // unreachable
                mood = 0;
                eyes = 0;
                expression = 0;
                break;
        }
        subtitle = data.getResourceId(2, 0);
        data.recycle();
    }

    int getId() {
        return id;
    }

    int getEyes() {
        return eyes;
    }

    int getMood() {
        return mood;
    }

    int getExpression() {
        return expression;
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
