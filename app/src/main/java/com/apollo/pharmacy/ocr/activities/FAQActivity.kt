package com.apollo.pharmacy.ocr.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollo.pharmacy.ocr.R
import com.apollo.pharmacy.ocr.adapters.FAQAdapter
import com.apollo.pharmacy.ocr.interfaces.OnRecyclerItemClickListener
import com.apollo.pharmacy.ocr.model.FAQData
import kotlinx.android.synthetic.main.activity_faq.*
import kotlinx.android.synthetic.main.view_close_layout.*

class FAQActivity : BaseActivity(), OnRecyclerItemClickListener {

    override fun onViewClick(position: Int) {
        val item: FAQData = data.get(position)
        for (i in data.indices) {
            data.get(i).visible = false
        }
        for (i in data.indices) {
            if (item.id.equals(data.get(i).id)) {
                data.get(i).visible = true
            }
        }
        adapter.notifyDataSetChanged()
    }

    val data = ArrayList<FAQData>()
    lateinit var adapter: FAQAdapter

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        var decorView: View = getWindow().getDecorView();
        var uiOptions: Int = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        fagRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = FAQAdapter(data, this, this)
        fagRecyclerView.adapter = adapter

        parent_close_layout.setOnClickListener({
            onBackPressed()
        })

        createFAQ()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun createFAQ(): ArrayList<FAQData> {
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_one), applicationContext.resources.getString(R.string.label_question_one), applicationContext.resources.getString(R.string.label_answer_one)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_two), applicationContext.resources.getString(R.string.label_question_two), applicationContext.resources.getString(R.string.label_answer_two)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_three), applicationContext.resources.getString(R.string.label_question_three), applicationContext.resources.getString(R.string.label_answer_three)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_four), applicationContext.resources.getString(R.string.label_question_four), applicationContext.resources.getString(R.string.label_answer_four)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_five), applicationContext.resources.getString(R.string.label_question_five), applicationContext.resources.getString(R.string.label_answer_five)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_six), applicationContext.resources.getString(R.string.label_question_six), applicationContext.resources.getString(R.string.label_answer_six)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_seven), applicationContext.resources.getString(R.string.label_question_seven), applicationContext.resources.getString(R.string.label_answer_seven)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_eight), applicationContext.resources.getString(R.string.label_question_eight), applicationContext.resources.getString(R.string.label_answer_eight)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_nine), applicationContext.resources.getString(R.string.label_question_nine), applicationContext.resources.getString(R.string.label_answer_nine)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_ten), applicationContext.resources.getString(R.string.label_question_ten), applicationContext.resources.getString(R.string.label_answer_ten)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_eleven), applicationContext.resources.getString(R.string.label_question_eleven), applicationContext.resources.getString(R.string.label_answer_eleven)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_twelve), applicationContext.resources.getString(R.string.label_question_twelve), applicationContext.resources.getString(R.string.label_answer_twelve)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_thirteen), applicationContext.resources.getString(R.string.label_question_thirteen), applicationContext.resources.getString(R.string.label_answer_thirteen)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_fourteen), applicationContext.resources.getString(R.string.label_question_fourteen), applicationContext.resources.getString(R.string.label_answer_fourteen)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_fifteen), applicationContext.resources.getString(R.string.label_question_fifteen), applicationContext.resources.getString(R.string.label_answer_fifteen)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_sixteen), applicationContext.resources.getString(R.string.label_question_sixteen), applicationContext.resources.getString(R.string.label_answer_sixteen)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_seventeen), applicationContext.resources.getString(R.string.label_question_seventeen), applicationContext.resources.getString(R.string.label_answer_seventeen)))
        data.add(FAQData(applicationContext.resources.getString(R.string.label_serial_number_eighteen), applicationContext.resources.getString(R.string.label_question_eighteen), applicationContext.resources.getString(R.string.label_answer_eighteen)))
        return data
    }
}
