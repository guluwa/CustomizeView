package cn.guluwa.hencoderdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    JiKePraiseView mPraiseView;
    EditText etNum;
    TextView tvNum;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPraiseView = findViewById(R.id.mPraiseView);
        etNum = findViewById(R.id.etNum);
        tvNum = findViewById(R.id.tvNum);
        tvNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPraiseView.setNum(Integer.valueOf(etNum.getText().toString().trim()))
                        .setTrue(false);
            }
        });
        status = 0;
        mPraiseView.setListener(new JiKePraiseImageView.PraiseListener() {
            @Override
            public void praiseFinish() {
                System.out.println("点赞");
            }

            @Override
            public void cancelFinish() {
                System.out.println("取消");
            }
        });
    }
}
