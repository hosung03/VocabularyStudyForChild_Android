package com.hosung.vocastudyforchild;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hosung.vocastudyforchild.R;

import java.util.ArrayList;

public class TestResultActivity extends AppCompatActivity {

    private ArrayList<QuestionItem> testItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        testItemList = (ArrayList<QuestionItem>)getIntent().getSerializableExtra("TestItemList");

        ArrayList<Integer> imageIDs = new ArrayList<Integer>();
        Integer right = R.drawable.right;
        Integer wrong = R.drawable.wrong;

        Integer testScore = 0;
        for (QuestionItem item : testItemList){
            if(item.user_answer.equals(item.collect_answer)) {
                imageIDs.add(right);
                testScore += 1;
            } else
                imageIDs.add(wrong);
        }

        GridView gridView = (GridView) findViewById(R.id.resultGridView);
        gridView.setAdapter(new GridViewAdapter(this,imageIDs));

        TextView scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        scoreTextView.setText("Score: " + testScore.toString() +"/5");

        TextView evaluationTextView = (TextView) findViewById(R.id.evaluationTextView);
        if(testScore == 5) evaluationTextView.setText("You Are A Genius!");
        else if (testScore == 4) evaluationTextView.setText("Excellent Work!");
        else if (testScore == 3) evaluationTextView.setText("Good Job!");
        else evaluationTextView.setText("Please Try Again!");

        Button testReviewButton = (Button) findViewById(R.id.testReviewButton);
        testReviewButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button testAgainButton = (Button) findViewById(R.id.testAgainButton);
        testAgainButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("TestAgain",true);
                startActivity(intent);
            }
        });
    }

//    private void printTestItemList(){
//        for (QuestionItem item : this.testItemList) {
//            Log.d("MainActivity", "item.question :"+item.question);
//            Log.d("MainActivity", "item.question_type :"+item.question_type);
//            Log.d("MainActivity", "item.question_img :"+item.question_img);
//            Log.d("MainActivity", "item.collect_answer :"+item.collect_answer);
//            Log.d("MainActivity", "item.user_answer :"+item.user_answer);
//            for(Object key : item.answers.keySet()){
//                String value = (String) item.answers.get(key);
//                Log.d("MainActivity", "item.answers :"+key+","+value);
//            }
//        }
//    }

    public class GridViewAdapter extends BaseAdapter
    {
        private Context context;
        private ArrayList<Integer> imageIDs;

        public GridViewAdapter(Context context, ArrayList<Integer> imageIDs)
        {
            this.context = context;
            this.imageIDs = imageIDs;
        }

        @Override
        public int getCount() {
            return imageIDs.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;
            if (view == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //imageView.setPadding(5, 5, 5, 5);
            } else {
                imageView = (ImageView) view;
            }
            imageView.setImageResource(imageIDs.get(i));
            return imageView;
        }

    }
}
