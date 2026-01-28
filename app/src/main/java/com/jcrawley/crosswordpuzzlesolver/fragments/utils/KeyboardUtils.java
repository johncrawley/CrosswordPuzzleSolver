package com.jcrawley.crosswordpuzzlesolver.fragments.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class KeyboardUtils {


    public static void hideKeyboard(Context context, EditText editText){
        var imm = getInputMethodManager(context);
        if(imm != null && imm.isActive()){
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }


    public static InputMethodManager getInputMethodManager(@Nullable Context context){
        if(context == null){
            return null;
        }
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    public static void setupKeyAction(final EditText editText, Runnable onDone, AtomicBoolean hasSearchStarted){
        setupKeyAction(editText, null, onDone, hasSearchStarted);
    }


    public static void setupKeyAction(final EditText editText, Runnable onNormalKeyInput, Runnable onDone, AtomicBoolean hasSearchStarted){
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if(hasSearchStarted.get()){
                return false;
            }
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(editText.getContext(), editText);
                onDone.run();
                return true;
            }
            else if(onNormalKeyInput != null){
                onNormalKeyInput.run();
            }
            return false;
        });
    }
}
