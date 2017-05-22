package com.hosung.vocastudyforchild;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by mac on 2016. 8. 9..
 */

public class QuestionItem implements Serializable {
    String question;
    String question_type;
    String question_img;
    HashMap answers;
    String collect_answer;
    String user_answer;
}
