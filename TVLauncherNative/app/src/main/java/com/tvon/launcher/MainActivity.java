package com.tvon.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;
import java.util.List;

public class MainActivity extends Activity {
    private final AppItem[] apps = new AppItem[] {
            new AppItem("TVON ELITE", R.drawable.card_elite, "ELITE", "https://pub-c1cdb0cc6fa04ac794aad058b3923ff8.r2.dev/elitebin.apk"),
            new AppItem("TVON PRO", R.drawable.card_pro, "PRO", "https://pub-c1cdb0cc6fa04ac794aad058b3923ff8.r2.dev/TV%20ON%20PRO%20V3.apk"),
            new AppItem("TVON SUPER", R.drawable.card_super, "SUPER", "https://pub-c1cdb0cc6fa04ac794aad058b3923ff8.r2.dev/TV%20ON%20USER.apk")
    };

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FrameLayout root = new FrameLayout(this);
        ImageView bg = new ImageView(this);
        bg.setImageResource(R.drawable.background);
        bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        root.addView(bg, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(70, 40, 70, 45);
        root.addView(main, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        main.addView(top, new LinearLayout.LayoutParams(-1, 210));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.mipmap.ic_launcher);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        top.addView(logo, new LinearLayout.LayoutParams(170, 170));

        TextView title = new TextView(this);
        title.setText("TVLauncher");
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(25,0,0,0);
        top.addView(title, new LinearLayout.LayoutParams(0, -1, 1));

        TextView time = new TextView(this);
        time.setText("TVON  •  3 APPS POR R$ 19,90/MÊS");
        time.setTextColor(Color.WHITE);
        time.setTextSize(28);
        time.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
        time.setTypeface(Typeface.DEFAULT_BOLD);
        top.addView(time, new LinearLayout.LayoutParams(700, -1));

        Space sp1 = new Space(this);
        main.addView(sp1, new LinearLayout.LayoutParams(1, 95));

        TextView appsLabel = new TextView(this);
        appsLabel.setText("  Apps");
        appsLabel.setTextColor(Color.WHITE);
        appsLabel.setTextSize(30);
        appsLabel.setTypeface(Typeface.DEFAULT_BOLD);
        main.addView(appsLabel, new LinearLayout.LayoutParams(-1, 60));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        main.addView(row, new LinearLayout.LayoutParams(-1, 390));

        for (AppItem app : apps) row.addView(createCard(app), new LinearLayout.LayoutParams(460, 310));

        Space sp2 = new Space(this);
        main.addView(sp2, new LinearLayout.LayoutParams(1, 35));

        TextView hint = new TextView(this);
        hint.setText("Use o controle remoto: esquerda/direita para escolher • OK para abrir ou instalar");
        hint.setTextColor(Color.rgb(180,220,255));
        hint.setTextSize(22);
        hint.setGravity(Gravity.CENTER);
        main.addView(hint, new LinearLayout.LayoutParams(-1, 60));

        setContentView(root);
    }

    private View createCard(final AppItem app) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setFocusable(true);
        card.setClickable(true);
        card.setPadding(18,18,18,18);
        card.setGravity(Gravity.CENTER);
        card.setBackground(makeBg(false));
        card.setOnFocusChangeListener((v, hasFocus) -> {
            v.animate().scaleX(hasFocus ? 1.18f : 1f).scaleY(hasFocus ? 1.18f : 1f).setDuration(140).start();
            v.setBackground(makeBg(hasFocus));
            v.setElevation(hasFocus ? 30f : 3f);
        });
        card.setOnClickListener(v -> openAppOrDownload(app));

        ImageView img = new ImageView(this);
        img.setImageResource(app.imageRes);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        card.addView(img, new LinearLayout.LayoutParams(-1, 210));

        TextView label = new TextView(this);
        label.setText(app.name);
        label.setTextColor(Color.WHITE);
        label.setTextSize(24);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setGravity(Gravity.CENTER);
        card.addView(label, new LinearLayout.LayoutParams(-1, 70));
        return card;
    }

    private GradientDrawable makeBg(boolean focus) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(focus ? Color.rgb(3,60,135) : Color.argb(210, 8, 15, 30));
        g.setCornerRadius(26);
        g.setStroke(focus ? 5 : 2, focus ? Color.rgb(0, 210, 255) : Color.rgb(35, 80, 130));
        return g;
    }

    private void openAppOrDownload(AppItem app) {
        String pkg = findInstalledPackage(app.searchKey);
        if (pkg != null) {
            Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
            if (launch != null) { startActivity(launch); return; }
        }
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(app.downloadUrl));
        startActivity(i);
    }

    private String findInstalledPackage(String key) {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> list = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : list) {
            CharSequence label = pm.getApplicationLabel(info);
            String text = ((label == null ? "" : label.toString()) + " " + info.packageName).toUpperCase();
            if (text.contains("TV") && text.contains("ON") && text.contains(key.toUpperCase())) return info.packageName;
            if (text.contains("TVON") && text.contains(key.toUpperCase())) return info.packageName;
        }
        return null;
    }

    static class AppItem {
        String name; int imageRes; String searchKey; String downloadUrl;
        AppItem(String n, int r, String k, String u) { name=n; imageRes=r; searchKey=k; downloadUrl=u; }
    }

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }
}
