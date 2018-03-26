package com.silvermoon.rocketboard;


import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.inputmethodservice.KeyboardView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.PopupWindow;

import com.silvermoon.smartkeyboard.R;

import java.lang.reflect.Field;
import java.util.List;

public class CustomKeyboard extends KeyboardView {

    static final int KEYCODE_OPTIONS = -100;
    // TODO: Move this into android.inputmethodservice.Keyboard
    static final int KEYCODE_LANGUAGE_SWITCH = -101;
    static final int KEYCODE_q = 113;
    static final int KEYCODE_w = 119;
    static final int KEYCODE_e = 101;
    static final int KEYCODE_r = 114;
    static final int KEYCODE_t = 116;
    static final int KEYCODE_y = 121;
    static final int KEYCODE_u = 117;
    static final int KEYCODE_i = 105;
    static final int KEYCODE_o = 111;
    static final int KEYCODE_p = 112;
    static final int KEYCODE_a = 97;
    static final int KEYCODE_s = 115;
    static final int KEYCODE_d = 100;
    static final int KEYCODE_f = 102;
    static final int KEYCODE_g = 103;
    static final int KEYCODE_h = 104;
    static final int KEYCODE_j = 106;
    static final int KEYCODE_k = 107;
    static final int KEYCODE_l = 108;
    static final int KEYCODE_z = 122;
    static final int KEYCODE_x = 120;
    static final int KEYCODE_c = 99;
    static final int KEYCODE_v = 118;
    static final int KEYCODE_b = 98;
    static final int KEYCODE_n = 110;
    static final int KEYCODE_m = 109;
    static final int KEYCODE_return = 10;
    static final int KEYCODE_space = 32;

    static final String [] packageList ={"gms","facebook","twitter","instagram","snapchat","whatsapp","hike"};
    static final String [] swearWords ={"fuck","hell","screw","bastard","scoundrel","bitch","dick",
                                        "penis","cock","pussy","cunt"};



    public CustomKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public CustomKeyboard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        }
        if(key.codes[0] == -113) {
            Variables.setCtrlOn();
            draw(new Canvas());
            return true;
        }
        if(key.codes[0] == -114) {
            Variables.setAltOn();
            draw(new Canvas());
            return true;
        }

        return super.onLongPress(key);
    }



    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard)getKeyboard();
        //keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(28);
        paint.setColor(Color.parseColor("#a5a7aa"));



        List<Key> keys = getKeyboard().getKeys();
        for(Key key: keys) {

            if(key.label != null) {
                if (key.label.equals("SPACE")) {
                    NinePatchDrawable npd = (NinePatchDrawable) getContext().getResources().getDrawable(R.drawable.space);
                    npd.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    npd.draw(canvas);
                    //     canvas.drawText("1", key.x + (key.width - 25), key.y + 40, paint);
                }
                if (Variables.isAnyOn()) {
                    if (Variables.isCtrl()) {
                        if (key.codes[0] == -113) {
                            NinePatchDrawable npd = (NinePatchDrawable) getContext().getResources().getDrawable(R.drawable.press);
                            npd.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                            npd.draw(canvas);
                        }
                    }
                    if (Variables.isAlt()){
                        if (key.codes[0] == -114) {
                            NinePatchDrawable npd = (NinePatchDrawable) getContext().getResources().getDrawable(R.drawable.press);
                            npd.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                            npd.draw(canvas);
                        }
                    }

                }
                else{
                    if(key.codes[0] == -113) {
                        Drawable npd = new ColorDrawable(Color.TRANSPARENT);
                        npd.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        npd.draw(canvas);
                    }
                    if(key.codes[0] == -114) {
                        Drawable npd = new ColorDrawable(Color.TRANSPARENT);
                        npd.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        npd.draw(canvas);
                    }
                }
            }
        }
    }
    public PopupWindow getPreview(){
        try {
            Field field = KeyboardView.class.getDeclaredField("mPreviewPopup");
            field.setAccessible(true);
            Object value = field.get(this);
            field.setAccessible(false);

            if (value == null) {
                return null;
            } else if (KeyboardView.class.isAssignableFrom(value.getClass())) {
                return (PopupWindow) value;
            }
            throw new RuntimeException("Wrong value");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPopupTheme(ColorMatrixColorFilter filter){
        Paint p = new Paint();
        p.setColorFilter(filter);
        getPreview().getContentView().setLayerType(LAYER_TYPE_HARDWARE, p);
    }
}
