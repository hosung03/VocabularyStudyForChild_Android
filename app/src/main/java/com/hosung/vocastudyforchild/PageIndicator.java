package com.hosung.vocastudyforchild;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * Created by mac on 2016. 8. 10..
 */

public class PageIndicator extends LinearLayout {
    private Context context;
    private int itemMargin = 10;
    private int animationDuration = 250;
    private int pageOffImage;
    private int pageOnImage;

    private ImageView[] imageDot;

    public void setAnimDuration(int animDuration) {
        this.animationDuration = animDuration;
    }

    public void setItemMargin(int itemMargin) {
        this.itemMargin = itemMargin;
    }

    public PageIndicator(Context context) {
        super(context);
        this.context = context;
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void createDotPanel(int count , int pgOffImage , int pgOnImage) {
        pageOffImage = pgOffImage;
        pageOnImage = pgOnImage;

        imageDot = new ImageView[count];

        for (int i = 0; i < count; i++) {
            imageDot[i] = new ImageView(this.context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = itemMargin;
            params.bottomMargin = itemMargin;
            params.leftMargin = itemMargin;
            params.rightMargin = itemMargin;
            params.gravity = Gravity.CENTER;

            imageDot[i].setLayoutParams(params);
            imageDot[i].setImageResource(pageOffImage);
            imageDot[i].setTag(imageDot[i].getId(), false);
            this.addView(imageDot[i]);
        }
        selectDot(0);
    }

    public void selectDot(int position) {
        for (int i = 0; i < imageDot.length; i++) {
            if (i == position) {
                imageDot[i].setImageResource(pageOnImage);
                selectScaleAnim(imageDot[i],1f,1.5f);
            } else {
                if((boolean)imageDot[i].getTag(imageDot[i].getId()) == true){
                    imageDot[i].setImageResource(pageOffImage);
                    defaultScaleAnim(imageDot[i], 1.5f, 1f);
                }
            }
        }
    }

    public void selectScaleAnim(View view, float startScale, float endScale) {
        Animation animation = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(animationDuration);
        view.startAnimation(animation);
        view.setTag(view.getId(),true);
    }

    public void defaultScaleAnim(View view, float startScale, float endScale) {
        Animation animation = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(animationDuration);
        view.startAnimation(animation);
        view.setTag(view.getId(),false);
    }
}
