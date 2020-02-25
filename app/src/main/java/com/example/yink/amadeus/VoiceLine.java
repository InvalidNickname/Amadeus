package com.example.yink.amadeus;

import android.annotation.SuppressLint;
import android.content.Context;
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
        static HashMap<String, VoiceLine> voiceLines = new HashMap<>();

        static HashMap<String, VoiceLine> getLines(Context context) {
            voiceLines.put("HELLO", new VoiceLine(R.array.hello, context));
            voiceLines.put("DAGA_KOTOWARU", new VoiceLine(R.array.daga_kotowaru, context));
            voiceLines.put("DEVILISH_PERVERT", new VoiceLine(R.array.devilish_pervert, context));
            voiceLines.put("I_GUESS", new VoiceLine(R.array.i_guess, context));
            voiceLines.put("NICE", new VoiceLine(R.array.nice, context));
            voiceLines.put("PERVERT_CONFIRMED", new VoiceLine(R.array.pervert_confirmed, context));
            voiceLines.put("SORRY", new VoiceLine(R.array.sorry, context));
            voiceLines.put("SOUNDS_TOUGH", new VoiceLine(R.array.sounds_tough, context));
            voiceLines.put("HOPELESS", new VoiceLine(R.array.this_guy_hopeless, context));
            voiceLines.put("CHRISTINA", new VoiceLine(R.array.christina, context));
            voiceLines.put("GAH", new VoiceLine(R.array.gah, context));
            voiceLines.put("NO_TINA", new VoiceLine(R.array.dont_add_tina, context));
            voiceLines.put("WHY_CHRISTINA", new VoiceLine(R.array.why_christina, context));
            voiceLines.put("WHO_IS_CHRISTINA", new VoiceLine(R.array.who_the_hell_christina, context));
            voiceLines.put("ASK_ME", new VoiceLine(R.array.ask_me_whatever, context));
            voiceLines.put("COULD_I_HELP", new VoiceLine(R.array.could_i_help, context));
            voiceLines.put("WHAT_DO_YOU_WANT", new VoiceLine(R.array.what_do_you_want, context));
            voiceLines.put("WHAT_IS_IT", new VoiceLine(R.array.what_is_it, context));
            voiceLines.put("HEHEHE", new VoiceLine(R.array.heheh, context));
            voiceLines.put("WHY_SAY_THAT", new VoiceLine(R.array.huh_why_say, context));
            voiceLines.put("YOU_SURE", new VoiceLine(R.array.you_sure, context));
            voiceLines.put("NICE_TO_MEET_OKABE", new VoiceLine(R.array.nice_to_meet_okabe, context));
            voiceLines.put("LOOKING_FORWARD_TO_WORKING", new VoiceLine(R.array.look_forward_to_working, context));
            voiceLines.put("SENPAI_QUESTION", new VoiceLine(R.array.senpai_question, context));
            voiceLines.put("SENPAI_QUESTIONMARK", new VoiceLine(R.array.senpai_questionmark, context));
            voiceLines.put("SENPAI_WHAT_WE_TALKING", new VoiceLine(R.array.senpai_what_we_talkin, context));
            voiceLines.put("SENPAI_WHO_IS_THIS", new VoiceLine(R.array.senpai_who_is_this, context));
            voiceLines.put("SENPAI_DONT_TELL", new VoiceLine(R.array.senpai_please_dont_tell, context));
            voiceLines.put("STILL_NOT_HAPPY", new VoiceLine(R.array.still_not_happy, context));
            voiceLines.put("DONT_CALL_ME_LIKE_THAT", new VoiceLine(R.array.dont_call_me_like_that, context));
            voiceLines.put("TM_NONCENCE", new VoiceLine(R.array.tm_nonsense, context));
            voiceLines.put("TM_NO_EVIDENCE", new VoiceLine(R.array.tm_scientist_no_evidence, context));
            voiceLines.put("TM_DONT_KNOW", new VoiceLine(R.array.tm_we_dont_know, context));
            voiceLines.put("TM_YOU_SAID", new VoiceLine(R.array.tm_you_said, context));
            voiceLines.put("HUMANS_SOFTWARE", new VoiceLine(R.array.humans_software, context));
            voiceLines.put("MEMORY_COMPLEXITY", new VoiceLine(R.array.memory_complex, context));
            voiceLines.put("SECRET_DIARY", new VoiceLine(R.array.secret_diary, context));
            voiceLines.put("MODIFIYING_MEMORIES", new VoiceLine(R.array.modifying_memories_impossible, context));
            voiceLines.put("MEMORIES_CHRISTINA", new VoiceLine(R.array.memories_christina, context));
            voiceLines.put("GAH_EXTENDED", new VoiceLine(R.array.gah_extended, context));
            voiceLines.put("SHOULD_CHRISTINA", new VoiceLine(R.array.should_christina, context));
            voiceLines.put("OK", new VoiceLine(R.array.ok, context));
            voiceLines.put("TM_NOT_POSSIBLE", new VoiceLine(R.array.tm_not_possible, context));
            voiceLines.put("PLEASED_TO_MEET", new VoiceLine(R.array.pleased_to_meet_you, context));
            voiceLines.put("PERVERT_IDIOT", new VoiceLine(R.array.pervert_idiot_wanttodie, context));
            voiceLines.put("LESKINEN_AWESOME", new VoiceLine(R.array.leskinen_awesome, context));
            voiceLines.put("LESKINEN_NICE", new VoiceLine(R.array.leskinen_nice, context));
            voiceLines.put("LESKINEN_OH_NO", new VoiceLine(R.array.leskinen_oh_no, context));
            voiceLines.put("LESKINEN_SHAMAN", new VoiceLine(R.array.leskinen_shaman, context));
            voiceLines.put("LESKINEN_HOLY_COW", new VoiceLine(R.array.leskinen_holy_cow, context));

            return voiceLines;
        }
    }
}
