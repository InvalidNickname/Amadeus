package ru.redroundpanda.amadeus;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.view.View;

public class LoggerSession extends VoiceInteractionSession {

    private View contentView;

    public LoggerSession(Context context) {
        super(context);
    }

    @Override
    public void onHandleAssist(Bundle data, AssistStructure structure, AssistContent content) {
        super.onHandleAssist(data, structure, content);

    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTheme(R.style.Transparent);
        }
        super.onCreate();
    }

    @Override
    public View onCreateContentView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contentView = getLayoutInflater().inflate(R.layout.activity_assist, null);
            View background = contentView.findViewById(R.id.background);
            background.setOnClickListener(v -> onBackPressed());
            return contentView;
        } else {
            return super.onCreateContentView();
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hide();
        } else {
            super.onBackPressed();
        }
    }
}
