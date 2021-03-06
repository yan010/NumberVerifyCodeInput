package com.example.yanqi.verifycodeinput;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanqi on 2018/2/14.
 */

public class InputCodeView extends LinearLayout implements View.OnClickListener, InputTextView.OnKeyEventListener {
    private Context mContext;
    private int MAX_COUNT = 4;//验证码数量

    private int inputType;
    private int padding;
    private int leftPadding;
    private int rightPadding;
    private int topPadding;
    private int bottomPadding;
    private int margin;
    private int leftMargin;
    private int rightMargin;
    private int topMargin;
    private int bottomMargin;
    private Drawable focusDrawable;
    private Drawable normalDrawable;
    private int textSize = 15;
    private ColorStateList textColor;

    private OnKeyEvent mOnKeyEvent;

    public void setmOnKeyEvent(OnKeyEvent mOnKeyEvent) {
        this.mOnKeyEvent = mOnKeyEvent;
    }

    private int count = 0;

    private List<InputTextView> inputTextViews = new ArrayList<>();

    private List<String> codeData = new ArrayList<>();

    public InputCodeView(Context context) {
        this(context, null);
    }

    public InputCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.InputCode);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.InputCode_number:
                    MAX_COUNT = a.getInt(attr, 4);
                    break;
                case R.styleable.InputCode_inputType:
                    inputType = a.getInt(attr, EditorInfo.TYPE_NULL);
                    break;
                case R.styleable.InputCode_focusDrawable:
                    focusDrawable = a.getDrawable(attr);
                    break;
                case R.styleable.InputCode_normalDrawable:
                    normalDrawable = a.getDrawable(attr);
                    break;
                case R.styleable.InputCode_padding:
                    padding = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_paddingLeft:
                    leftPadding = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_paddingRight:
                    rightPadding = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_paddingTop:
                    topPadding = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_paddingBottom:
                    bottomPadding = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_margin:
                    margin = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_marginLeft:
                    leftMargin = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_marginRight:
                    rightMargin = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_marginTop:
                    topMargin = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_marginBottom:
                    bottomMargin = a.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputCode_textColor:
                    textColor = a.getColorStateList(attr);
                    break;
                case R.styleable.InputCode_textSize:
                    textSize = a.getDimensionPixelSize(attr, textSize);
                    break;
            }
        }
        initItemCode();

    }

    private void initItemCode() {
        for (int i = 0; i < MAX_COUNT; i++) {
            final InputTextView inputTextView = new InputTextView(mContext);
            inputTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (padding != 0) {
                inputTextView.setPadding(padding, padding, padding, padding);
            } else {
                inputTextView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
            }
            inputTextView.setTextSize(textSize);
            inputTextView.setTextColor(textColor != null ? textColor : ColorStateList.valueOf(0xFF000000));
            if (focusDrawable != null && normalDrawable != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    inputTextView.setBackground(setSelector(focusDrawable, normalDrawable));
                } else {
                    inputTextView.setBackgroundDrawable(setSelector(focusDrawable, normalDrawable));
                }
            }
            inputTextView.setGravity(Gravity.CENTER);
            LayoutParams layoutParams;
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            if (margin != 0) {
                layoutParams.setMargins(margin, margin, margin, margin);
            } else {
                layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            }
            inputTextView.setLayoutParams(layoutParams);
            inputTextView.setMinWidth(500);//解决输入文字移位
            inputTextView.setmOnKeyEventListener(this);

            final int finalI = i;
            addView(inputTextView, finalI);
            inputTextViews.add(inputTextView);
        }
    }

    /**
     * 构建选择背景Drawable
     *
     * @param normalDrawable 默认样式
     * @param focusDrawable  foucus样式
     * @return
     */
    public StateListDrawable setSelector(Drawable focusDrawable, Drawable normalDrawable) {
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_focused}, focusDrawable);
        bg.addState(new int[]{}, normalDrawable);
        return bg;
    }

    /**
     * 删除事件
     */
    private void del() {
        if (count > 0) {
            count--;
            inputTextViews.get(count).setText("");
            inputTextViews.get(count).requestFocus();
            if (mOnKeyEvent != null) {
                mOnKeyEvent.onCodeChange(count, "");
            }
        } else {
            count = 0;
        }
    }

    /**
     * 添加事件
     *
     * @param content 添加的内容
     */
    private void add(String content) {
        if (count < MAX_COUNT) {
            inputTextViews.get(count).setText(content);
            if (mOnKeyEvent != null) {
                mOnKeyEvent.onCodeChange(count, content);
            }
            count++;
            if (count == MAX_COUNT) {
                inputTextViews.get(MAX_COUNT - 1).requestFocus();
                if (mOnKeyEvent != null) {
                    mOnKeyEvent.onFinishCode(getCode());
                }
            } else {
                inputTextViews.get(count).requestFocus();
            }
        } else {
            count = MAX_COUNT;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.viewClicked(inputTextViews.get(count));
        }
        imm.showSoftInput(inputTextViews.get(count), 0);
        return true;
    }

    @Override
    public void onAdd(String content) {
        add(content);
    }

    @Override
    public void onDel() {
        del();
    }

    /**
     * 获取当前输入的验证码
     *
     * @return 验证码结果，以list形式返回
     */
    public List<String> getCode() {
        codeData.clear();
        for (TextView textView : inputTextViews) {
            codeData.add(textView.getText().toString());
        }
        return codeData;
    }

    /**
     * 获取当前将要输入的位置
     *
     * @return 位置
     */
    public int getPosition() {
        return count;
    }

    public interface OnKeyEvent {
        /**
         * 发生变化的code事件监听
         *
         * @param position
         * @param content
         */
        void onCodeChange(int position, String content);

        /**
         * 完成code事件监听
         *
         * @param codeData
         */
        void onFinishCode(List<String> codeData);

    }
}
