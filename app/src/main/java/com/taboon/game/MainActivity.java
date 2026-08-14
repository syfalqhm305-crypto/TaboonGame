package com.taboon.game;

import android.Manifest;
import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import java.util.Random;

public class MainActivity extends Activity {

    SharedPreferences prefs;

    int score;
    int level;

    String personName;

    TextView title;
    TextView scoreText;
    TextView levelText;
    TextView messageText;
    TextView fruit;

    EditText nameInput;

    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);

        score = prefs.getInt("score", 0);
        level = prefs.getInt("level", 1);
        personName = prefs.getString("name", "أيهم");

        createGame();

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }
    }

    TextView text(String value, int size) {

        TextView t = new TextView(this);

        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        t.setGravity(Gravity.CENTER);
        t.setPadding(10, 10, 10, 10);

        return t;
    }

    Button button(String value) {

        Button b = new Button(this);

        b.setText(value);
        b.setTextSize(18);

        return b;
    }

    void createGame() {

        root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(25, 25, 25, 25);

        root.setBackgroundColor(Color.rgb(52, 20, 82));

        title = text("🍑 طبون أيهم 🍑", 30);

        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        root.addView(title);

        nameInput = new EditText(this);

        nameInput.setHint("اكتب اسم الشخص");
        nameInput.setText(personName);
        nameInput.setTextColor(Color.WHITE);
        nameInput.setHintTextColor(Color.LTGRAY);
        nameInput.setGravity(Gravity.CENTER);

        root.addView(
                nameInput,
                new LinearLayout.LayoutParams(
                        -1,
                        65
                )
        );

        Button saveName = button("حفظ الاسم");

        saveName.setOnClickListener(v -> {

            String newName =
                    nameInput.getText().toString().trim();

            if (newName.length() == 0) {

                Toast.makeText(
                        this,
                        "اكتب اسم أولاً",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            personName = newName;

            prefs.edit()
                    .putString("name", personName)
                    .apply();

            title.setText(
                    "🍑 طبون " + personName + " 🍑"
            );

            messageText.setText(
                    "تم تغيير الاسم إلى " +
                    personName +
                    " 😂"
            );
        });

        root.addView(saveName);

        scoreText = text("", 23);
        levelText = text("", 20);

        root.addView(scoreText);
        root.addView(levelText);

        LinearLayout gameArea =
                new LinearLayout(this);

        gameArea.setGravity(Gravity.CENTER);
        gameArea.setOrientation(
                LinearLayout.VERTICAL
        );

        fruit = text("🍑", 100);

        gameArea.addView(
                fruit,
                new LinearLayout.LayoutParams(
                        -1,
                        230
                )
        );

        root.addView(gameArea);

        Button hitButton =
                button("🪢 اضرب!");

        hitButton.setTextSize(25);

        hitButton.setOnClickListener(
                v -> hit()
        );

        root.addView(
                hitButton,
                new LinearLayout.LayoutParams(
                        -1,
                        75
                )
        );

        messageText =
                text("جاهز؟ 😈", 19);

        root.addView(messageText);

        Button reset =
                button("إعادة البداية");

        reset.setOnClickListener(v -> {

            new AlertDialog.Builder(this)

                    .setTitle("إعادة اللعبة")

                    .setMessage(
                            "هل تريد تصفير النقاط؟"
                    )

                    .setNegativeButton(
                            "إلغاء",
                            null
                    )

                    .setPositiveButton(
                            "نعم",
                            (dialog, which) -> {

                                score = 0;
                                level = 1;

                                save();

                                updateScreen();

                                messageText.setText(
                                        "بدأنا من جديد 😈"
                                );
                            }
                    )

                    .show();
        });

        root.addView(reset);

        setContentView(root);

        updateScreen();
    }

    void hit() {

        score++;

        int newLevel =
                (score / 10) + 1;

        if (newLevel > level) {

            level = newLevel;

            messageText.setText(
                    "🔥 نعم! أنت في الطريق الصحيح " +
                    "بضربك طبون " +
                    personName +
                    "! المستوى " +
                    level
            );

        } else {

            String[] messages = {

                    "😂 ضربة!",

                    "🔥 ممتاز!",

                    "👏 استمر!",

                    "😈 ضربة قوية!",

                    "🍑 أحسنت!",

                    "💥 لا توقف!"

            };

            Random random =
                    new Random();

            messageText.setText(
                    messages[
                            random.nextInt(
                                    messages.length
                            )
                    ]
            );
        }

        save();

        updateScreen();

        fruit.animate()
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setDuration(80)
                .withEndAction(() -> {

                    fruit.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(80)
                            .start();

                })
                .start();

        if (Build.VERSION.SDK_INT >= 26) {

            android.os.Vibrator vibrator =
                    (android.os.Vibrator)
                            getSystemService(
                                    VIBRATOR_SERVICE
                            );

            if (vibrator != null) {

                vibrator.vibrate(
                        VibrationEffect.createOneShot(
                                70,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                );
            }
        }
    }

    void updateScreen() {

        scoreText.setText(
                "النقاط: " + score
        );

        levelText.setText(
                "المستوى: " + level
        );

        title.setText(
                "🍑 طبون " +
                personName +
                " 🍑"
        );
    }

    void save() {

        prefs.edit()
                .putInt("score", score)
                .putInt("level", level)
                .putString("name", personName)
                .apply();
    }
}
