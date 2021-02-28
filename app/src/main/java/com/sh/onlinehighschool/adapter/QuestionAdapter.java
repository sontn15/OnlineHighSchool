package com.sh.onlinehighschool.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.model.Question;
import com.sh.onlinehighschool.utils.QuizHelper;
import com.sh.onlinehighschool.utils.Units;
import com.sh.onlinehighschool.utils.UniversalImageLoader;

import java.util.ArrayList;


public class QuestionAdapter extends PagerAdapter {

    private LayoutInflater inflater;
    private Context context;

    private int status;
    private ArrayList<Question> questions;

    public QuestionAdapter(Context context, int status, ArrayList<Question> questions) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.status = status;
        this.questions = questions;
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.item_question, parent, false);
        final LinearLayout layoutBody = view.findViewById(R.id.layout_body);
        final Question question = questions.get(position);

        /*Nội dung câu hỏi:*/
        //1. Câu hỏi
        String rowAsk = "<b>Câu " + (position + 1) + "</b>: <br>" + question.getAsk();
        //2. Đáp án
        String result = "Đáp án đúng: <b>" + question.getResult().toUpperCase() + "</b>";
        if (question.getChoice() != null){
            if (question.getChoice().equals(question.getResult().toUpperCase())){
                //Trả lời đúng
                result = "Bạn đã trả lời <b>đúng</b> | " + result;
            } else {
                //Trả lời sai
                result = "Bạn đã trả lời <b>sai<b><br>Bạn chọn <b>" + question.getChoice() + "</b> | " + result;
            }
        } else {
            result = "Bạn chưa trả lời câu hỏi này<br>" + result;
        }
        if (question.getDetail() != null && !question.getDetail().equals("")){
            //Nếu có đáp án chi tiết -> Hiển thị thêm đáp án chi tiết
            result = result + "<br><b>Hướng dẫn:</b> " + question.getDetail();
        }

        //Xóa bỏ giao diện cũ (nếu có)
        layoutBody.removeAllViews();
        if (!isMath(question)){
            /*Tạo giao diện thi cho các câu hỏi không có công thức toán học*/
            //1. Câu hỏi
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params1.bottomMargin = Units.dpToPx(12);
            TextView tvAsk = new TextView(context);
            tvAsk.setLayoutParams(params1);
            tvAsk.setLineSpacing(Units.dpToPx(6), 1);
            tvAsk.setText(Html.fromHtml(rowAsk));
            layoutBody.addView(tvAsk);
            //2. Hình ảnh (nếu có)
            if (question.getImg() != null && !question.getImg().equals("")){
                ImageView imageView = new ImageView(context);
                UniversalImageLoader.setImage(question.getImg(), imageView, null);
                layoutBody.addView(imageView);
            }
            //2. Các đáp án
            RecyclerView recyclerView = new RecyclerView(context);
            OptionValueAdapter adapter = new OptionValueAdapter(status, question);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(recyclerView, false);
            layoutBody.addView(recyclerView);
            //3. Nếu đang xem đáp án và cho phép hiển thị đáp án
            // -> Hiển thị đáp án
            if (status == 2){
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params2.topMargin = Units.dpToPx(12);
                TextView tvResult = new TextView(context);
                tvResult.setLayoutParams(params2);
                tvResult.setLineSpacing(Units.dpToPx(6), 1);
                tvResult.setText(Html.fromHtml(result));
                tvResult.setTextColor(context.getResources().getColor(R.color.red));
                layoutBody.addView(tvResult);
            }
        } else {
            /*Tạo giao diện thi cho các câu hỏi có công thức toán học*/
            //1. Câu hỏi
            WebView webView = new WebView(context);
            final ProgressBar progressBar = new ProgressBar(context);
            LinearLayout.LayoutParams  params1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params1.gravity = Gravity.CENTER;
            params1.topMargin = Units.dpToPx(32);
            params1.bottomMargin = Units.dpToPx(32);

            webView.getSettings().setAppCacheEnabled(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);

                    //2. Giao diện trả lời
                    RecyclerView recyclerView = new RecyclerView(context);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params2.gravity = Gravity.CENTER_HORIZONTAL;
                    params2.topMargin = Units.dpToPx(8);
                    recyclerView.setLayoutParams(params2);
                    OptionTitleAdapter adapter = new OptionTitleAdapter(status, question);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, question.getOptions().size()));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);
                    ViewCompat.setNestedScrollingEnabled(recyclerView, false);
                    layoutBody.addView(recyclerView);
                }
            });
            String img = "";
            if (question.getImg() != null && !question.getImg().equals("")){
                img = "<p><img src=\"" + question.getImg() + "\" /></p>";
            }
            String body = "<p>" + rowAsk + "</p>" + img +
                    "<div>" + options(question) + "</div>";
            if (status == 2){
                body = body + "<div  style=\"color:red;\">" + result + "</div>";
            }
            String html = startHTML() + body + endHTML();
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            layoutBody.addView(progressBar);
            layoutBody.addView(webView);
        }
        parent.addView(view);
        return view;
    }

    private String options(Question question){
        StringBuilder options = null;
        for (int i = 0; i < question.getOptions().size(); i++){
            options = (options == null ? new StringBuilder() : options)
                    .append(rowOption(question, i));
        }
        return options.toString();
    }

    private String rowOption(Question question, int position){
        String option = question.getOptions().get(position);
        return "<p><b>" + QuizHelper.choice(position) + "</b>. " + option + "</p>";
    }

    private boolean isMath(Question question){
        boolean isMathAsk = question.getAsk().contains("\\(") || question.getAsk().contains("$");
        boolean isMathOptions = false;
        for (int i = 0; i < question.getOptions().size(); i++){
            String option = question.getOptions().get(i);
            if (option.contains("\\(") || option.contains("$")){
                isMathOptions = true;
            }
        }
        return isMathAsk || isMathOptions;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    private static String startHTML (){
        return "<html><head><style>" +
                "p{" +
                "margin-bottom: 8px;" +
                "margin-top: 8px;" +
                "color: #000000;" +
                "line-height:1.8;" +
                "font-size: 16px;" +
                "}" +
                "</style>" +
                //math type
                "<script type=\"text/x-mathjax-config\">" +
                "MathJax.Hub.Config({" +
                "showMathMenu: false," +
                "messageStyle: \"none\"," +
                "SVG: {" +
                "scale: 90," +
                "linebreaks: {" +
                "automatic: true" +
                "}" +
                "}," +
                "\"HTML-CSS\": { linebreaks: { automatic: true } }," +
                "CommonHTML: { linebreaks: { automatic: true } }," +
                "tex2jax: {" +
                "inlineMath: [ ['$','$'], [\"\\\\(\",\"\\\\)\"] ]" +
                "}" +
                "})" +
                "</script>" +
                "<script " +
                "src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.5/latest.js?config=TeX-AMS-MML_SVG' " +
                "type='text/javascript'>" +
                "</script>" +
                "</head><body style=\"background-color: #FFFFFF;\">";
    }

    private static String endHTML(){
        return "</body> </html>";
    }
}
