package com.hosung.vocastudyforchild;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hosung.vocastudyforchild.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    // for Tool Bar
    private TextView titleTextView;
    private Button goTestButton;

    // for Test Review Check
    private Boolean reviewState = false;

    // for xml
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    private ArrayList<QuestionItem> testXMLList;
    private ArrayList<QuestionItem> testItemList;
    private String testTitle = "";

    // for indicator
    private PageIndicator pageIndicator;

    // for test pages
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Boolean testAgain = intent.getBooleanExtra("TestAgain",false);
        if(testAgain) reviewState = false;

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        goTestButton = (Button) findViewById(R.id.goTestButton);
        goTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTestButton.setVisibility(View.GONE);
                reviewState = false;
                initializeTest();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.testItemPager);
        pageIndicator = (PageIndicator) findViewById(R.id.pageIndicator);

        initializeTest();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (reviewState)
            menu.add(Menu.NONE, 0, Menu.NONE, "Retest").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (reviewState) goTestButton.setVisibility(View.VISIBLE);
    }

    private void initializeTest(){
        if(testXMLList==null) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (parsingComplete) {
                        LinearLayout prograssBarLayout = (LinearLayout) findViewById(R.id.prograssBarLayout);
                        prograssBarLayout.setVisibility(View.GONE);
                    }
                }
            }, 1000); // 1 second delay (takes millis)
            if (!xmlParing()) return;
        }
        else  {
            for (QuestionItem questionItem : testXMLList) {
                if(!questionItem.user_answer.equals(""))
                    questionItem.user_answer = "";
            }
        }

        // for selecting random questions
        if (testItemList != null) testItemList = null;
        testItemList = new ArrayList<QuestionItem>();
        ArrayList<Integer> selectedIndexArray = makeRandomIndexArray(testXMLList.size());
        for (Integer index : selectedIndexArray) {
            testItemList.add(testXMLList.get(index));
        }

        // for setting view pager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                titleTextView.setText("Question "+(position+1));
                pageIndicator.selectDot(position);

                if(!reviewState) return;

                QuestionItem questionItem  = testItemList.get(position);
                if (!questionItem.user_answer.equals("")) {
                    RadioButton answer = null;
                    switch (questionItem.user_answer) {
                        case "1" :
                            answer = (RadioButton) findViewById(R.id.answer1);
                            answer.setChecked(true);
                            break;
                        case "2" :
                            answer = (RadioButton) findViewById(R.id.answer2);
                            answer.setChecked(true);
                            break;
                        case "3" :
                            answer = (RadioButton) findViewById(R.id.answer3);
                            answer.setChecked(true);
                            break;
                        case "4" :
                            answer = (RadioButton) findViewById(R.id.answer4);
                            answer.setChecked(true);
                            break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // for setting view page indicator
        pageIndicator.setItemMargin(10);
        pageIndicator.setAnimDuration(300);
        pageIndicator.createDotPanel(testItemList.size(), R.drawable.pageoff , R.drawable.pageon);
    }

    private boolean xmlParing() {
        parsingComplete = false;
        try {
            AssetManager assetManager = getAssets();
            InputStream xmlStream = assetManager.open("workbook.xml");
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser xmlParser = xmlFactoryObject.newPullParser();
            xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlParser.setInput(xmlStream, null);
            parseXMLAndMakeList(xmlParser);
        } catch (XmlPullParserException e) {
            parsingComplete = true;
            e.printStackTrace();
            Log.e("MainActivity", "error :"+e.getMessage());
            return false;
        } catch (IOException e) {
            parsingComplete = true;
            e.printStackTrace();
            Log.e("MainActivity", "error :"+e.getMessage());
            return false;
        }
        return true;
    }

    private void parseXMLAndMakeList(XmlPullParser xmlParser) throws XmlPullParserException, IOException {
        int event;
        String text = null;
        String attr = null;
        QuestionItem testXMLItem = null;
        int i=0;

        this.testXMLList = new ArrayList<QuestionItem>();
        event = xmlParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            String name = xmlParser.getName();
            switch (event) {
                case XmlPullParser.START_TAG:
                    if (name.equals("workbook")) {
                        for(i=0; i<xmlParser.getAttributeCount(); i++){
                            if(xmlParser.getAttributeName(i).equals("subject")){
                                this.testTitle = xmlParser.getAttributeValue(i);
                                break;
                            }
                        }
                    }
                    if (name.equals("item")) {
                        testXMLItem = new QuestionItem();
                        for(i=0; i<xmlParser.getAttributeCount(); i++){
                            if(xmlParser.getAttributeName(i).equals("type")){
                                testXMLItem.question_type = new String(xmlParser.getAttributeValue(i));
                                break;
                            }
                        }
                        testXMLItem.user_answer = "";
                    }
                    if (name.equals("answer")) {
                        for (i = 0; i < xmlParser.getAttributeCount(); i++) {
                            if (xmlParser.getAttributeName(i).equals("id")) {
                                attr = new String(xmlParser.getAttributeValue(i));
                                break;
                            }
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    text = xmlParser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (name != null && testXMLItem != null) {
                        if (name.equals("item")) {
                            this.testXMLList.add(testXMLItem);
                            testXMLItem = null;
                            text = null;
                        }
                        else if (name.equals("question") && text!=null){
                            testXMLItem.question = new String(text);
                            text = null;
                        }
                        else if (name.equals("question_img") && text!=null){
                            testXMLItem.question_img = new String(text);
                            text = null;
                        }
                        else if (name.equals("collect_answer") && text!=null){
                            testXMLItem.collect_answer = new String(text);
                            text = null;
                        }
                        else if (name.equals("answer") && text!=null && attr!=null) {
                            if (testXMLItem.answers==null) testXMLItem.answers = new HashMap();
                            testXMLItem.answers.put(attr, text);
                            attr = null;
                            text = null;
                        }
                    }
                    break;
            }
            event = xmlParser.next();
        }
        parsingComplete = true;
    }

//    private void printTestXMLList(){
//        for (QuestionItem item : this.testXMLList) {
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

    private ArrayList<Integer> makeRandomIndexArray(int max){
        ArrayList<Integer> indexArray = new ArrayList<Integer>();
        while(indexArray.size()<5) {
            Integer value = (int) (Math.random() * max);
            if (!indexArray.contains(value)) {
                indexArray.add(value);
            }
        }
        return indexArray;
    }

    class ViewPagerAdapter extends PagerAdapter {
        private Context context;

        public ViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return testItemList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            if(position==0) titleTextView.setText("Question "+(position+1));

            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.test_page, null);
            container.addView(view, 0);

            QuestionItem questionItem  = testItemList.get(position);

            TextView questionTextView = (TextView) findViewById(R.id.questionTextView);
            questionTextView.setText(questionItem.question);

            try {
                AssetManager assetManager = this.context.getAssets();
                InputStream xmlStream = assetManager.open(questionItem.question_img+".png");
                Bitmap bitmap = BitmapFactory.decodeStream(xmlStream);
                ImageView questionImageView = (ImageView) findViewById(R.id.questionImageView);
                questionImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("MainActivity", "error :"+e.getMessage());
            }

            RadioButton answer1 = (RadioButton) findViewById(R.id.answer1);
            answer1.setText((String) questionItem.answers.get("1"));

            RadioButton answer2 = (RadioButton) findViewById(R.id.answer2);
            answer2.setText((String) questionItem.answers.get("2"));

            RadioButton answer3 = (RadioButton) findViewById(R.id.answer3);
            answer3.setText((String) questionItem.answers.get("3"));

            RadioButton answer4 = (RadioButton) findViewById(R.id.answer4);
            answer4.setText((String) questionItem.answers.get("4"));

            RadioGroup answerGroup = (RadioGroup) findViewById(R.id.answerGroup);
            answerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                   QuestionItem questionItem  = testItemList.get(position);
                    switch (i) {
                       case R.id.answer1:
                           questionItem.user_answer = "1";
                           break;
                       case R.id.answer2:
                           questionItem.user_answer = "2";
                           break;
                       case R.id.answer3:
                           questionItem.user_answer = "3";
                           break;
                       case R.id.answer4:
                           questionItem.user_answer = "4";
                           break;
                    }
                    if(!reviewState && position + 1 == testItemList.size()) {
                        callAlertDialog();
                    }
                }
            });

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private void callAlertDialog(){
            LinearLayout prograssBarLayout = (LinearLayout) findViewById(R.id.prograssBarLayout);
            prograssBarLayout.setVisibility(View.VISIBLE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LinearLayout prograssBarLayout = (LinearLayout) findViewById(R.id.prograssBarLayout);
                    prograssBarLayout.setVisibility(View.GONE);
                }
            }, 1000);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("You want to get a result!");
            builder.setCancelable(true);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    Intent intent = new Intent(getBaseContext(), TestResultActivity.class);
                    intent.putExtra("TestItemList", (ArrayList<QuestionItem>)testItemList);
                    startActivity(intent);
                    reviewState = true;
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
