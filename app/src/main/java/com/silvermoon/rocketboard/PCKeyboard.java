package com.silvermoon.rocketboard;



import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.*;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.silvermoon.smartkeyboard.R;
import com.silvermoon.rocketboard.data.SmartKeyContract;
import com.silvermoon.rocketboard.data.UserAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Main class  of the keyboard, extending InputMethodService. Here we handle all the user interaction with the keyboard itself. */

public class PCKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, SpellCheckerSession.SpellCheckerSessionListener {


    /**
     * As we don't want to bother changing the app theme, we use filters to theme the keyboard.
     * Each filter needs an array of colors. The arrays are declared below.
     */

    // TODO: Add the arrays in a separate, static class, so they are eay to access and modify

    private static final float[] sNoneColorArray = {
            1.0f, 0, 0, 0, 0, // red
            0, 1.0f, 0, 0, 0, // green
            0, 0, 1.0f, 0, 0, // blue
            0, 0, 0, 1.0f, 0 // alpha
    };

    private static final float[] sNegativeColorArray = {
            -1.0f, 0, 0, 0, 255, // red
            0, -1.0f, 0, 0, 255, // green
            0, 0, -1.0f, 0, 255, // blue
            0, 0, 0, 1.0f, 0 // alpha
    };
    private static final float[] sBlueBlackColorArray = {
            -0.6f, 0, 0, 0, 41, // red
            0, -0.6f, 0, 0, 128, // green
            0, 0, -0.6f, 0, 185, // blue
            0, 0, 0, 1.0f, 0 // alpha
    };
    private static final float[] sBlueWhiteColorArray = {
            1.0f, 0, 0, 0, 41, // red
            0, 1.0f, 0, 0, 128, // green
            0, 0, 1.0f, 0, 185, // blue
            0, 0, 0, 1.0f, 1 // alpha
    };
    private static final float[] sRedWhiteColorArray = {
            1.0f, 0, 0, 0, 192, // red
            0, 1.0f, 0, 0, 57, // green
            0, 0, 1.0f, 0, 43, // blue
            0, 0, 0, 1.0f, 0 // alpha
    };
    private static final float[] sRedBlackColorArray = {
            -0.6f, 0, 0, 0, 192, // red
            0, -0.6f, 0, 0, 57, // green
            0, 0, -0.6f, 0, 43, // blue
            0, 0, 0, 1.0f, 0 // alpha
    };
    private static final float[] sOrangeBlackColorArray = {
            1.0f, 0, 0, 0, 230, // red
            0, 1.0f, 0, 0, 126, // green
            0, 0, 1.0f, 0, 34, // blue
            0, 0, 0, 1.0f, 0 // alpha
    };
    private static final float[] sMaterialDarkColorArray = {
            1.0f, 0, 0, 0, 55, // red
            0, 1.0f, 0, 0, 71, // green
            0, 0, 1.0f, 0, 79, // blue
            0, 0, 0, 1.0f, 1 // alpha
    };

    static final boolean PROCESS_HARD_KEYS = true;

    //Sagar Das - Creating a variable for smart button

    private boolean isSmartKeyPressed = false;
    private boolean isWriteSettingsGranted = false;
    private boolean isNotificationSettingsGranted = false;
    private boolean isDecencySensorActive = false;
    private boolean isSocialApp = false;
    private String pName;
    private Keyboard.Key spaceKey=null;
    private View appView;
    private String [] keyMappings = new String[256];
    private String queryURI;
    private Cursor mCursor;
    private UserAction userAction;


    private HashMap map;

    private InputMethodManager mInputMethodManager;

    private CustomKeyboard mInputView;
    private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;

    private StringBuilder mComposing = new StringBuilder();
    private boolean mPredictionOn;
    private boolean mCompletionOn;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;

    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mQwertyKeyboard;



    private String mWordSeparators;

    private SpellCheckerSession mScs;
    private List<String> mSuggestions;

    private boolean firstCaps = false;
    private boolean isSysmbols = false;
    private boolean shiftSim = false;
    private boolean isDpad = false;
    private boolean isProgramming = false;
    private InputMethodManager mServ;
    private float[] mDefaultFilter;
    long shift_pressed=0;


    private CustomKeyboard kv;

    private LatinKeyboard currentKeyboard;
    private LatinKeyboard mCurKeyboard;
    private LatinKeyboard qwertyKeyboard;

    private int qwertyKeyboardID = R.xml.qwerty;
    /**
     * Main initialization of the input method component. Be sure to call
     * to super class.
     */

    @Override public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);



        if(mScs==null){
            mScs = tsm.newSpellCheckerSession(null, Locale.ENGLISH,this,false);
        }

        queryURI = SmartKeyContract.CONTENT_URI.toString();
        String [] projection = new String[]{SmartKeyContract.TABLE_USER_ACTION};
        int isAssigned = 1;
        mCursor = getContentResolver().query(Uri.parse(queryURI),projection,null,new String[] {String.valueOf(isAssigned)},SmartKeyContract.SORT_ORDER);


        if(mCursor!=null){

          //  if(mCursor.moveToFirst()) {
              //  userAction = new UserAction(mCursor);
             //   keyMappings[userAction.keyId] = userAction.packageName;

                while (mCursor.moveToNext()) {
                    userAction = new UserAction(mCursor);
                    keyMappings[userAction.keyId] = userAction.packageName;
                }
           // }

        }
        else{
            Log.i("PCKeyboard", "onCreate: Cursor is null ");
        }


    }



    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override public void onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;


        }
        mQwertyKeyboard = new LatinKeyboard(this, qwertyKeyboardID);
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols2);




    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     *
     * We also s
     */
    @Override public View onCreateInputView() {
        mInputView = (CustomKeyboard) getLayoutInflater().inflate(
                R.layout.keyboard, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setPreviewEnabled(false);
        setLatinKeyboard(mQwertyKeyboard);
        return mInputView;
    }

    private void setLatinKeyboard(LatinKeyboard nextKeyboard) {
        final boolean shouldSupportLanguageSwitchKey =
                mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
        //     nextKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);
        mInputView.setKeyboard(nextKeyboard);

    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        setTheme();
        Paint mPaint = new Paint();
        ColorMatrixColorFilter filterInvert = new ColorMatrixColorFilter(mDefaultFilter);
        mPaint.setColorFilter(filterInvert);
        mCandidateView.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);


        return mCandidateView;
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     *
     *
     * And we have to reinitialize all we've one to make sure the keyboard aspect matches
     * The one selected in settings.
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        setTheme();

        //Sagar
        //check the name of the current app's package

        pName = attribute.packageName;



        for(String pn : CustomKeyboard.packageList){
            if(pName.contains(pn)){
                isSocialApp = true;
                break;
            }
        }

        Log.i("Package name", pName);

        Toast.makeText(this,pName,Toast.LENGTH_SHORT);


        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);

        if(mScs==null){
            mScs = tsm.newSpellCheckerSession(null, Locale.ENGLISH,this,false);
        }



        mComposing.setLength(0);
        updateCandidates();
        /**
         * Some code on here is based on the SoftKeyboard Sample. I don't fully understand it.
         * I need to look it up and delete any unnecessary stuff.
         * */
        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }
        mCompletions = null;

        if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("dec",false)){
            isDecencySensorActive = true;
        }
        else{
            isDecencySensorActive = false;
        }

        if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("bord",false)){
            kv = (CustomKeyboard) getLayoutInflater().inflate(R.layout.keyboard_key_back, null);
        }
        else {
            kv = (CustomKeyboard) getLayoutInflater().inflate(R.layout.keyboard, null);
        }
        setQwertyKeyboard();
        setInputType();

        List<Keyboard.Key> keys = currentKeyboard.getKeys();
        Keyboard.Key key = null;

        spaceKey = keys.get(31); // gets CustomKeyboard.KEYCODE_space
        key = keys.get(30); //CustomKeyboard.KEYCODE_LANGUAGE_SWITCH

        /*for(int i = 0; i < keys.size() - 1; i++ )
        {
            key = keys.get(i);

            if(key.codes[0]== CustomKeyboard.KEYCODE_space){
                spaceKey = key;

            }

            //If your Key contains more than one code, then you will have to check if the codes array contains the primary code
            if(key.codes[0] == CustomKeyboard.KEYCODE_LANGUAGE_SWITCH)
            {
                break; // leave the loop once you find your match
            }
        }*/
        if(isSmartKeyPressed){

            key.icon = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_circle_temp);
        }
        else{

            key.icon = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_more_horiz);
        }


        Paint mPaint = new Paint();
        ColorMatrixColorFilter filterInvert = new ColorMatrixColorFilter(mDefaultFilter);
        mPaint.setColorFilter(filterInvert);
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);

        kv.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);
        kv.setKeyboard(currentKeyboard);
        capsOnFirst();
        kv.setOnKeyboardActionListener(this);

        mPredictionOn = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("pred", false);
        mCompletionOn = false;

        mCandidateView.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);

        setInputView(kv);


        setCandidatesView(mCandidateView);

    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override public void onFinishInput() {
        super.onFinishInput();

        // Clear current composing text and candidates.
        mComposing.setLength(0);
        updateCandidates();

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null) {
            mInputView.closing();
        }
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                            int newSelStart, int newSelEnd,
                                            int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<String>();
            for (int i = 0; i < completions.length; i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        boolean dead = false;

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() -1 );
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length()-1);
            }
        }

        onKey(c, null);

        return true;
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {

        return super.onKeyUp(keyCode, event);
    }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
            updateCandidates();
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null
                && mInputView != null && mQwertyKeyboard == mInputView.getKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mInputView.setShifted(mCapsLock || caps != 0);
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }


    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());

    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);
        if(mScs==null){
            mScs = tsm.newSpellCheckerSession(null, Locale.ENGLISH,this,false);
        }



        if (!mCompletionOn) {
            if (mComposing.length() > 0) {

                ArrayList<String> list = new ArrayList<String>();
                list.add(mComposing.toString());

               mScs.getSentenceSuggestions(new TextInfo[] {new TextInfo(mComposing.toString())}, 5);

                setSuggestions(list, true, true);
            } else {
                setSuggestions(null, false, false);
            }
        }
    }

    private boolean containsAlready(String[] arr, String string) {
        for (String s : arr) {
            if (s.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        mSuggestions = suggestions;
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (kv.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        if (mPredictionOn && !mWordSeparators.contains(String.valueOf((char)primaryCode))) {
            mComposing.append((char) primaryCode);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateShiftKeyState(getCurrentInputEditorInfo());
            updateCandidates();
        }
        if(mPredictionOn && mWordSeparators.contains(String.valueOf((char)primaryCode))){
            char code = (char) primaryCode;
            if (Character.isLetter(code) && firstCaps || Character.isLetter(code) && Variables.isShift()) {
                code = Character.toUpperCase(code);
            }
            getCurrentInputConnection().setComposingRegion(0,0);
            getCurrentInputConnection().commitText(String.valueOf(code), 1);
            firstCaps = false;
            setCapsOn(false);
        }
        if(!mPredictionOn){
            char code = (char) primaryCode;
            if (Character.isLetter(code) && firstCaps || Character.isLetter(code) && Variables.isShift()) {
                code = Character.toUpperCase(code);
            }
            getCurrentInputConnection().setComposingRegion(0,0);
            getCurrentInputConnection().commitText(String.valueOf(code), 1);
            firstCaps = false;
            setCapsOn(false);
        }
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    private void handleLanguageSwitch() {
        mInputMethodManager.switchToNextInputMethod(getToken(), false /* onlyCurrentIme */);
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {

            if (mPredictionOn && mSuggestions != null && index >= 0) {
                mComposing.replace(0, mComposing.length(), mSuggestions.get(index));
            }
            commitTyped(getCurrentInputConnection());

        }
    }

    public void swipeRight() {
        keyDownUp(KeyEvent.KEYCODE_DPAD_LEFT);

        if (mCompletionOn || mPredictionOn) {
            pickDefaultCandidate();
        }
    }

    public void swipeLeft() {
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }

    public void onPress(int primaryCode) {
        if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("vib", false)) {
            Vibrator v = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(40);
        }
    }


    public void onRelease(int primaryCode) {

    }
    /**
     * http://www.tutorialspoint.com/android/android_spelling_checker.htm
     * Sort of copy-paste, huh.
     *
     * I need to find time to refine this code
     *
     *
     * @param results results
     */
    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < results.length; ++i) {
            // Returned suggestions are contained in SuggestionsInfo
            final int len = results[i].getSuggestionsCount();
            sb.append('\n');

            for (int j = 0; j < len; ++j) {
                sb.append("," + results[i].getSuggestionAt(j));
            }

            sb.append(" (" + len + ")");
        }
    }

    private void dumpSuggestionsInfoInternal(
            final List<String> sb, final SuggestionsInfo si, final int length, final int offset) {
        // Returned suggestions are contained in SuggestionsInfo
        final int len = si.getSuggestionsCount();
        for (int j = 0; j < len; ++j) {
            sb.add(si.getSuggestionAt(j));
        }
    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        try {
            final List<String> sb = new ArrayList<>();
            for (int i = 0; i < results.length; ++i) {
                final SentenceSuggestionsInfo ssi = results[i];

                for (int j = 0; j < ssi.getSuggestionsCount(); ++j) {
                    dumpSuggestionsInfoInternal(
                            sb, ssi.getSuggestionsInfoAt(j), ssi.getOffsetAt(j), ssi.getLengthAt(j));
                }
            }

            setSuggestions(sb, true, true);
        }
        catch(Exception e){}

    }



    private void setCapsOn(boolean on) {

        /** Simple function that enables us to rapidly set the keyboard shifted or not.
         * */
        if(Variables.isShift()){
            kv.getKeyboard().setShifted(true);
            kv.invalidateAllKeys();
        }
        else {
            kv.getKeyboard().setShifted(on);
            kv.invalidateAllKeys();
        }

    }
    private void processKeyCombo(int keycode) {
        /** Ass the function name says, we process key combinations here*/

        if (Variables.isAnyOn()) {
            if (Variables.isCtrl() && Variables.isAlt()) {
                getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, getHardKeyCode(keycode), 0, KeyEvent.META_CTRL_ON | KeyEvent.META_ALT_ON));
                getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_UP, getHardKeyCode(keycode), 0, KeyEvent.META_CTRL_ON | KeyEvent.META_ALT_ON));
            } else {
                if (Variables.isCtrl()) {
                    getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, getHardKeyCode(keycode), 0, KeyEvent.META_CTRL_ON));
                    getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_UP, getHardKeyCode(keycode), 0, KeyEvent.META_CTRL_ON));
                }
                if (Variables.isAlt()) {
                    getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, getHardKeyCode(keycode), 0, KeyEvent.META_ALT_ON));
                    getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_UP, getHardKeyCode(keycode), 0, KeyEvent.META_ALT_ON));
                }
            }
        }

    }
    private int getHardKeyCode(int keycode) {
        /** Seems like the actual soft key code doesn't match the hard key code*/
        PopupWindow p = new PopupWindow();
        char code = (char) keycode;
        switch (String.valueOf(code)) {
            case "a":
                return KeyEvent.KEYCODE_A;
            case "b":
                return KeyEvent.KEYCODE_B;
            case "c":
                return KeyEvent.KEYCODE_C;

            case "d":
                return KeyEvent.KEYCODE_D;

            case "e":
                return KeyEvent.KEYCODE_E;

            case "f":
                return KeyEvent.KEYCODE_F;


            case "g":
                return KeyEvent.KEYCODE_G;

            case "h":
                return KeyEvent.KEYCODE_H;

            case "i":
                return KeyEvent.KEYCODE_I;

            case "j":
                return KeyEvent.KEYCODE_J;


            case "k":
                return KeyEvent.KEYCODE_K;

            case "l":
                return KeyEvent.KEYCODE_L;

            case "m":
                return KeyEvent.KEYCODE_M;

            case "n":
                return KeyEvent.KEYCODE_N;

            case "o":
                return KeyEvent.KEYCODE_O;

            case "p":
                return KeyEvent.KEYCODE_P;


            case "q":
                return KeyEvent.KEYCODE_Q;

            case "r":
                return KeyEvent.KEYCODE_R;


            case "s":
                return KeyEvent.KEYCODE_S;

            case "t":
                return KeyEvent.KEYCODE_T;

            case "u":
                return KeyEvent.KEYCODE_U;

            case "v":
                return KeyEvent.KEYCODE_V;


            case "w":
                return KeyEvent.KEYCODE_W;

            case "x":
                return KeyEvent.KEYCODE_X;

            case "y":
                return KeyEvent.KEYCODE_Y;

            case "z":
                return KeyEvent.KEYCODE_Z;
            default:
                return keycode;
        }
    }

    private void handleAction() {
        EditorInfo curEditor = getCurrentInputEditorInfo();
        switch (curEditor.imeOptions & EditorInfo.IME_MASK_ACTION) {
            case EditorInfo.IME_ACTION_DONE:
                getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_DONE);
                break;
            case EditorInfo.IME_ACTION_GO:
                getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_GO);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_NEXT);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_SEARCH);
                break;
            case EditorInfo.IME_ACTION_SEND:

                getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_SEND);

                break;
            default:

                break;
        }
    }

    public void setTheme() {
        switch (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("theme", "2")) {
            case "1":
                mDefaultFilter = sNoneColorArray;
                break;
            case "2":
                mDefaultFilter = sNegativeColorArray;
                break;
            case "3":
                mDefaultFilter = sBlueWhiteColorArray;
                break;
            case "4":
                mDefaultFilter = sBlueBlackColorArray;
                break;
            case "5":
                mDefaultFilter = sRedWhiteColorArray;
                break;
            case "6":
                mDefaultFilter = sRedBlackColorArray;
                break;
            case "7":
                mDefaultFilter = sOrangeBlackColorArray;
                break;
            case "8":
                mDefaultFilter = sMaterialDarkColorArray;
                break;

        }
    }
    private void setInputType() {

        /** Checks the preferences for the default keyboard layout.
         * If qwerty, we start out whether in qwerty or numbers, depending on the input type.
         * */

        EditorInfo attribute = getCurrentInputEditorInfo();

        if (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("start", "1").equals("1")) {
            switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
                case InputType.TYPE_CLASS_NUMBER:
                case InputType.TYPE_CLASS_DATETIME:
                case InputType.TYPE_CLASS_PHONE:
                    currentKeyboard = new LatinKeyboard(this, R.xml.numbers);
                    break;
                case InputType.TYPE_CLASS_TEXT:
                    int webInputType = attribute.inputType & InputType.TYPE_MASK_VARIATION;

                    if (webInputType == InputType.TYPE_TEXT_VARIATION_URI ||
                            webInputType == InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT ||
                            webInputType == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            || webInputType == InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS) {
                        currentKeyboard = new LatinKeyboard(this, qwertyKeyboardID);
                    } else {
                        currentKeyboard = new LatinKeyboard(this, qwertyKeyboardID);
                    }

                    break;

                default:
                    currentKeyboard = new LatinKeyboard(this, qwertyKeyboardID);
                    break;
            }
        } else {
            setDefaultKeyboard();
        }
        if (kv != null) {
            kv.setKeyboard(currentKeyboard);
        }
    }
    public void setDefaultKeyboard() {
        switch (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("start", "1")) {
            case "1":
                currentKeyboard = qwertyKeyboard;
                break;
            case "2":
                currentKeyboard = new LatinKeyboard(this, R.xml.arrow_keys);
                break;
            case "3":
                currentKeyboard = new LatinKeyboard(this, R.xml.programming);
                break;
        }
    }
    private void capsOnFirst() {

        /** Huh, a method that calls getCursorCapsMode() and performs a check.
         * Accordingly to the official android documentation, if the caps mode is not equal to 0,
         * We should start in caps mode. Although, tests have proven that additionally checks are needed.
         * I'll see what I can do on this.
         * */
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("caps",true)){
            if (getCursorCapsMode(getCurrentInputConnection(), getCurrentInputEditorInfo()) != 0) {
                firstCaps = true;
                setCapsOn(true);
            }
        }
        else {
            firstCaps = false;
            setCapsOn(false);
        }

    }
    private int getCursorCapsMode(InputConnection ic, EditorInfo attr) {

        /** A rudimentary method to find out whether we should start with caps on or not.
         * */
        // TODO: Perform additional checks.

        int caps = 0;
        EditorInfo ei = getCurrentInputEditorInfo();
        if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
            caps = ic.getCursorCapsMode(attr.inputType);
        }
        return caps;
    }
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //Sagar Das - rowEdgeFlags is as per android documentation
        /*Keyboard.Row keyRow = new Keyboard.Row(mQwertyKeyboard);
        keyRow.rowEdgeFlags = 8;
        Keyboard.Key key = new Keyboard.Key(keyRow);
        int [] codesArray = {primaryCode};
        key.codes = codesArray;*/


        /** Here we handle the key events. */

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                handleBackspace();
                break;
            case Keyboard.KEYCODE_SHIFT:

                /** We need to check whether we are on symbols layout or not.
                 * Then, perform the operation accordingly.
                 * Also, we check for double tab on the shift, and, if detected
                 * We set a global variable that tells us that the Shift is in the lock position.
                 * */

                if (isSysmbols) {
                    if (!shiftSim) {
                        currentKeyboard = new LatinKeyboard(this, R.xml.symbols2);
                        kv.setKeyboard(currentKeyboard);
                        shiftSim = true;
                    } else {
                        currentKeyboard = new LatinKeyboard(this, R.xml.symbols);
                        kv.setKeyboard(currentKeyboard);
                        shiftSim = false;
                    }
                } else {
                    if (shift_pressed + 200 > System.currentTimeMillis()){
                        Variables.setShiftOn();
                        setCapsOn(true);
                        kv.draw(new Canvas());
                    }
                    else{
                        if(Variables.isShift()){
                            Variables.setShiftOff();
                            firstCaps = false;
                            setCapsOn(firstCaps);
                            shift_pressed = System.currentTimeMillis();
                        }
                        else{
                            firstCaps = !firstCaps;
                            setCapsOn(firstCaps);
                            shift_pressed = System.currentTimeMillis();
                        }
                    }


                }
                break;
            case 10:

                /** Handle the 'done' action accordingly to the IME Options. */

                EditorInfo curEditor = getCurrentInputEditorInfo();
                switch (curEditor.imeOptions & EditorInfo.IME_MASK_ACTION) {
                    case EditorInfo.IME_ACTION_DONE:
                        getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_DONE);
                        break;
                    case EditorInfo.IME_ACTION_GO:
                        getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_GO);
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        keyDownUp(66);
                        break;
                    case EditorInfo.IME_ACTION_SEARCH:
                        getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_SEARCH);
                        break;
                    case EditorInfo.IME_ACTION_SEND:
                        keyDownUp(66);
                        break;
                    default:
                        keyDownUp(66);
                        break;
                }
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:

                /** Switch between qwerty/symbols layout. */

                if (!isSysmbols) {
                    isSysmbols = !isSysmbols;
                    currentKeyboard = new LatinKeyboard(this, R.xml.symbols);
                    kv.setKeyboard(currentKeyboard);
                } else {
                    isSysmbols = false;
                    currentKeyboard = new LatinKeyboard(this, qwertyKeyboardID);
                    kv.setKeyboard(currentKeyboard);
                }
                break;

            case CustomKeyboard.KEYCODE_LANGUAGE_SWITCH:

                /** Language Switch is a custom value defined in the CustomKeyboard class.
                 * We use it to switch between qwerty/arrow keys/programming layouts. */
                // Keyboard currentKeyboard = mInputView.getKeyboard();
                List<Keyboard.Key> keys = currentKeyboard.getKeys();
                Keyboard.Key key = null;
                for(int i = 0; i < keys.size() - 1; i++ )
                {
                    key = keys.get(i);

                    //If your Key contains more than one code, then you will have to check if the codes array contains the primary code
                    if(key.codes[0] == CustomKeyboard.KEYCODE_LANGUAGE_SWITCH)
                    {
                        break; // leave the loop once you find your match
                    }
                }
                if(isSmartKeyPressed){
                    isSmartKeyPressed = false;
                    key.icon = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_more_horiz);
                }
                else{
                    isSmartKeyPressed = true;
                    key.icon = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_circle_temp);
                }


                break;
            case CustomKeyboard.KEYCODE_q:
                if(isSmartKeyPressed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(getApplicationContext())) {
                            isWriteSettingsGranted = true;
                        } else {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + this.getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    if(isWriteSettingsGranted){
                        changeBrightness(0);
                    }
                }
                else{
                    handleKeyNormalOperation(primaryCode, keyCodes);
                }

                break;
            case CustomKeyboard.KEYCODE_w:
                if(isSmartKeyPressed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(getApplicationContext())) {
                            isWriteSettingsGranted = true;
                        } else {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + this.getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    if(isWriteSettingsGranted){
                        changeBrightness(1);
                    }
                }
                else{
                    handleKeyNormalOperation(primaryCode, keyCodes);
                }
                break;
            case CustomKeyboard.KEYCODE_e:

                if(isSmartKeyPressed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (notificationManager.isNotificationPolicyAccessGranted()) {
                            isNotificationSettingsGranted = true;
                        } else {
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    }
                    if(isNotificationSettingsGranted){
                        toggleSound(0);
                    }
                }

                else{
                    handleKeyNormalOperation(primaryCode, keyCodes);
                }
                break;
            case CustomKeyboard.KEYCODE_r:

                if(isSmartKeyPressed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (notificationManager.isNotificationPolicyAccessGranted()) {
                            isNotificationSettingsGranted = true;
                        } else {
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    }
                    if(isNotificationSettingsGranted){
                        toggleSound(1);
                    }
                }

                else{
                    handleKeyNormalOperation(primaryCode, keyCodes);
                }
                break;
            case CustomKeyboard.KEYCODE_t:
                if(isSmartKeyPressed){}
                else{handleKeyNormalOperation(primaryCode, keyCodes);}
                break;
            case CustomKeyboard.KEYCODE_y:
            case CustomKeyboard.KEYCODE_u:
            case CustomKeyboard.KEYCODE_i:
            case CustomKeyboard.KEYCODE_o:
            case CustomKeyboard.KEYCODE_p:
            case CustomKeyboard.KEYCODE_a:
            case CustomKeyboard.KEYCODE_s:
            case CustomKeyboard.KEYCODE_d:
            case CustomKeyboard.KEYCODE_f:
            case CustomKeyboard.KEYCODE_g:
            case CustomKeyboard.KEYCODE_h:
            case CustomKeyboard.KEYCODE_j:
            case CustomKeyboard.KEYCODE_k:
            case CustomKeyboard.KEYCODE_l:
            case CustomKeyboard.KEYCODE_z:
            case CustomKeyboard.KEYCODE_x:
            case CustomKeyboard.KEYCODE_c:
            case CustomKeyboard.KEYCODE_v:
            case CustomKeyboard.KEYCODE_b:
            case CustomKeyboard.KEYCODE_n:
            case CustomKeyboard.KEYCODE_m:
                if(isSmartKeyPressed){
                    if(keyMappings[primaryCode]!=null) {
                        PackageManager packageManager = getPackageManager();
                        Intent launchintent = packageManager.getLaunchIntentForPackage(keyMappings[primaryCode]);
                        startActivity(launchintent);
                    }
                    else{
                        Toast.makeText(this, "No action has been assigned to this key", Toast.LENGTH_SHORT).show();
                    }
                }
                else{handleKeyNormalOperation(primaryCode, keyCodes);}
                break;
            case CustomKeyboard.KEYCODE_space:

                if(isDecencySensorActive){
                    boolean isFword = false;
                    //if(isSocialApp){
                        CharSequence word = getCurrentInputConnection().getTextBeforeCursor(10,0);

                        for(String sWord: CustomKeyboard.swearWords){
                            if(word.toString().contains(sWord)){
                                isFword=true;
                                break;
                            }



                        }
                    //}
                    if(isFword){
                        spaceKey.label = getString(R.string.decency);

                    }
                    else{
                        spaceKey.label = "SPACE";
                    }

                    handleKeyNormalOperation(primaryCode, keyCodes);
                }
                else{spaceKey.label = "SPACE";
                    handleKeyNormalOperation(primaryCode, keyCodes);}

                break;


            case -108:

                /** Another custom keycode. */

                // TODO: declare custom code in the CustomKeyboard class


                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT));

                break;

            case -111:

                // TODO: declare custom code in the CustomKeyboard class

                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT));
                break;
            case -107:

                // TODO: declare custom code in the CustomKeyboard class

                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP));
                break;
            case -109:

                // TODO: declare custom code in the CustomKeyboard class

                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN));
                break;
            case -112:

                // TODO: declare custom code in the CustomKeyboard class


                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE, 0));
                getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(100, 100, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ESCAPE, 0));
                break;
            case -113:

                // TODO: declare custom code in the CustomKeyboard class

                if (Variables.isCtrl()) {
                    Variables.setCtrlOff();
                    kv.draw(new Canvas());
                } else {
                    Variables.setCtrlOn();
                    kv.draw(new Canvas());
                }
                break;
            case -114:

                // TODO: declare custom code in the CustomKeyboard class

                if (Variables.isAlt()) {
                    Variables.setAltOff();
                    kv.draw(new Canvas());
                } else {
                    Variables.setAltOn();
                    kv.draw(new Canvas());
                }
                break;
            case -117:

                /** This key enables the user to switch rapidly between qwerty/arrow keys layouts.*/

                // TODO: declare custom code in the CustomKeyboard class

                currentKeyboard = new LatinKeyboard(getBaseContext(), qwertyKeyboardID);
                kv.setKeyboard(currentKeyboard);
                isDpad = false;
                break;
            case -121:
                /** Procces DEL key*/

                if(Variables.isAnyOn()){
                    if(Variables.isCtrl() && Variables.isAlt()) {
                        getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_ALT_ON));
                    }
                    if(Variables.isAlt()){
                        getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, KeyEvent.META_ALT_ON));
                    }
                    if(Variables.isCtrl()){
                        getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, KeyEvent.META_CTRL_ON));
                    }
                }
                else{
                    getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL , 0));
                    getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0));
                }
                break;
            case -122:
                if(Variables.isAnyOn()){
                    if(Variables.isCtrl() && Variables.isAlt()) {
                        getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_ALT_ON));
                    }
                    if(Variables.isAlt()){
                        getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB, 0, KeyEvent.META_ALT_ON));
                    }
                    if(Variables.isCtrl()){
                        getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB, 0, KeyEvent.META_CTRL_ON));
                    }
                }
                else{
                    getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB , 0));
                    getCurrentInputConnection().sendKeyEvent(new KeyEvent(100, 100, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_TAB, 0));
                }
                break;
            default:

                if (Variables.isAnyOn()) {
                    processKeyCombo(primaryCode);
                } else {
                    handleCharacter(primaryCode, keyCodes);
                }
        }



        try {

            /** Some text processing. Helps some guys improve their writing skills, huh*/

            //TODO: Handle this better
            if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("caps",true)) {
                if (ic.getTextBeforeCursor(2, 0).toString().contains(". ")) {
                    setCapsOn(true);
                    firstCaps = true;
                }
            }
        } catch (Exception e) {
        }
    }

    public void handleKeyNormalOperation(int primaryCode, int[] keyCodes){
        if (Variables.isAnyOn()) {
            processKeyCombo(primaryCode);
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    public void setQwertyKeyboard(){
        if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("arr_qrt", false) && PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("nbr_qrt", false)){
            qwertyKeyboardID = R.xml.qwerty_arrow_numbers;
        }
        else{
            if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("arr_qrt", false)){
                qwertyKeyboardID = R.xml.qwerty_arrows;
            }
            else if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("nbr_qrt", false)){
                qwertyKeyboardID = R.xml.qwerty_numbers;
            }
            else {
                qwertyKeyboardID = R.xml.qwerty;
            }
        }
    }
    public void changeBrightness(int code){

        int curBrightnessValue= 0;
        int sysBackLightValue = 0;
        PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);

        try {
            curBrightnessValue = Settings.System.getInt(
                    getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        /*if(curBrightnessValue==0||curBrightnessValue==100){
            return;
        }*/

        if(code==0){
            sysBackLightValue = (curBrightnessValue - 25) < 1 ? 1 : curBrightnessValue-25;
        }
        else{
            sysBackLightValue = (curBrightnessValue + 25) > 255 ? 255 : curBrightnessValue+25;
        }

        android.provider.Settings.System.putInt(getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                sysBackLightValue);
    }

    public void toggleSound(int code){

        AudioManager am;
        am= (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        //For Normal mode
        if(code==1) {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

        //For Silent mode
        if(code==0) {
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }

        //For Vibrate mode
        //  if(code==0) {
        //   am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        //  }

    }
}
