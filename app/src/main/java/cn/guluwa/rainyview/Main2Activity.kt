package cn.guluwa.rainyview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import cn.guluwa.hencoderdemo.JiKePraiseImageView
import cn.guluwa.hencoderdemo.JiKePraiseView

class Main2Activity : AppCompatActivity() {

     lateinit var mPraiseView: JiKePraiseView
     lateinit var etNum: EditText
     lateinit var tvNum: TextView
     var status: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        mPraiseView = findViewById(R.id.mPraiseView)
        etNum = findViewById(R.id.etNum)
        tvNum = findViewById(R.id.tvNum)
        tvNum.setOnClickListener {
            mPraiseView.setNum(Integer.valueOf(etNum.text.toString().trim { it <= ' ' }))
                .setTrue(false)
        }
        status = 0
        mPraiseView.setListener(object : JiKePraiseImageView.PraiseListener {
            override fun praiseFinish() {
                println("点赞")
            }

            override fun cancelFinish() {
                println("取消")
            }
        })
    }

}
