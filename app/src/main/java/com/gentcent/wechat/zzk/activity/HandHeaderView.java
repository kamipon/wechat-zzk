package com.gentcent.wechat.zzk.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gentcent.wechat.zzk.R;

public class HandHeaderView extends LinearLayout {
    public Context a;
    public View hand_header_view;
    public RelativeLayout rly_header;
    public ImageView img_left_image;
    public TextView tv_left_title;
    public TextView tv_title;
    public ImageView img_right_image;
    public TextView tv_right_title;

    public HandHeaderView(Context context) {
        super(context);
        this.a = context;
    }

    public HandHeaderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.a = context;
        a(context, attributeSet);
    }

    public void setBackgroundResource() {
        setBackgroundResource(R.color.header_color);
    }

    public void setBackgroundResource(int i) {
        RelativeLayout relativeLayout = this.rly_header;
        if (relativeLayout != null) {
            relativeLayout.setBackgroundResource(i);
        }
    }

    public void setLeftInvisible() {
        ImageView imageView = this.img_left_image;
        if (imageView != null) {
            imageView.setVisibility(View.GONE);
        }
        TextView textView = this.tv_left_title;
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
    }

    public void setRightInvisible() {
        ImageView imageView = this.img_right_image;
        if (imageView != null) {
            imageView.setVisibility(View.GONE);
        }
        TextView textView = this.tv_right_title;
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
    }

    public void setLeftImage(int i) {
        ImageView imageView = this.img_left_image;
        if (imageView != null) {
            imageView.setImageResource(i);
            this.img_left_image.setVisibility(View.VISIBLE);
        }
        TextView textView = this.tv_left_title;
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
    }

    public void setLeftText(String str) {
        ImageView imageView = this.img_left_image;
        if (imageView != null) {
            imageView.setVisibility(View.GONE);
        }
        TextView textView = this.tv_left_title;
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
            this.tv_left_title.setText(str);
        }
    }

    public void setRightImage(int i) {
        ImageView imageView = this.img_right_image;
        if (imageView != null) {
            imageView.setImageResource(i);
            this.img_right_image.setVisibility(View.VISIBLE);
        }
        TextView textView = this.tv_right_title;
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
    }

    public void setRightText(String str) {
        setRightText(str, 0);
    }

    public void setRightText(String str, int i) {
        ImageView imageView = this.img_right_image;
        if (imageView != null) {
            imageView.setVisibility(View.GONE);
        }
        TextView textView = this.tv_right_title;
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
            this.tv_right_title.setText(str);
            if (i != 0) {
                this.tv_right_title.setTextColor(i);
            }
        }
    }

    public void setTitleTextColor(int i) {
        this.tv_title.setTextColor(i);
    }

    public void setTitleText(String str) {
        this.tv_title.setText(str);
    }

    private void a(Context context, AttributeSet attributeSet) {
        this.hand_header_view = LayoutInflater.from(context).inflate(R.layout.hand_header_view, null, false);
        this.rly_header = (RelativeLayout) this.hand_header_view.findViewById(R.id.rly_header);
        this.img_left_image = (ImageView) this.hand_header_view.findViewById(R.id.img_left_image);
        this.tv_left_title = (TextView) this.hand_header_view.findViewById(R.id.tv_left_title);
        this.tv_title = (TextView) this.hand_header_view.findViewById(R.id.tv_title);
        this.img_right_image = (ImageView) this.hand_header_view.findViewById(R.id.img_right_image);
        this.tv_right_title = (TextView) this.hand_header_view.findViewById(R.id.tv_right_title);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HandHeaderView);
//        Drawable drawable = obtainStyledAttributes.getDrawable(0);
//        if (drawable != null) {
//            this.rly_header.setBackground(drawable);
//        } else {
//            this.rly_header.setBackgroundResource(R.color.header_color);
//        }
        Drawable drawable2 = obtainStyledAttributes.getDrawable(0);
        if (drawable2 != null) {
            this.img_left_image.setVisibility(View.VISIBLE);
            a(this.img_left_image, drawable2);
            this.tv_left_title.setVisibility(View.GONE);
        } else {
            this.img_left_image.setVisibility(View.GONE);
        }
//        String valueOf = String.valueOf(obtainStyledAttributes.getText(2));
//        int color = obtainStyledAttributes.getColor(3, 0);
//        if (TextUtils.isEmpty(valueOf) || valueOf.equals("null")) {
//            this.tv_left_title.setVisibility(View.GONE);
//        } else {
//            this.tv_left_title.setVisibility(View.VISIBLE);
//            this.tv_left_title.setText(valueOf);
//            if (color != 0) {
//                this.tv_left_title.setTextColor(color);
//            }
//            this.img_left_image.setVisibility(View.GONE);
//        }
        String valueOf2 = String.valueOf(obtainStyledAttributes.getText(1));
        if (TextUtils.isEmpty(valueOf2) || valueOf2.equals("null")) {
            this.tv_title.setVisibility(View.GONE);
        } else {
            this.tv_title.setVisibility(View.VISIBLE);
            this.tv_title.setText(valueOf2);
        }
//        Drawable drawable3 = obtainStyledAttributes.getDrawable(5);
//        if (drawable3 != null) {
//            this.img_right_image.setVisibility(View.VISIBLE);
//            a(this.img_right_image, drawable3);
//            this.tv_right_title.setVisibility(View.GONE);
//        } else {
//            this.img_right_image.setVisibility(View.GONE);
//        }
//        String string = obtainStyledAttributes.getString(6);
//        int color2 = obtainStyledAttributes.getColor(7, 0);
//        if (TextUtils.isEmpty(string) || string.equals("null")) {
//            this.tv_right_title.setVisibility(View.GONE);
//        } else {
//            this.tv_right_title.setVisibility(View.VISIBLE);
//            this.tv_right_title.setText(string);
//            if (color2 != 0) {
//                this.tv_right_title.setTextColor(color2);
//            }
//            this.img_right_image.setVisibility(View.GONE);
//        }
        obtainStyledAttributes.recycle();
        addView(this.hand_header_view, new LayoutParams(-1, -2));
    }

    private void a(ImageView imageView, Drawable drawable) {
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    public void setLeftClickListener(OnClickListener onClickListener) {
        if (this.img_left_image.getVisibility() == View.VISIBLE) {
            this.img_left_image.setOnClickListener(onClickListener);
        } else if (this.tv_left_title.getVisibility() == View.VISIBLE) {
            this.tv_left_title.setOnClickListener(onClickListener);
        }
    }

    public void setRightClickListener(OnClickListener onClickListener) {
        if (this.img_right_image.getVisibility() == View.VISIBLE) {
            this.img_right_image.setOnClickListener(onClickListener);
        } else if (this.tv_right_title.getVisibility() == View.VISIBLE) {
            this.tv_right_title.setOnClickListener(onClickListener);
        }
    }
}
