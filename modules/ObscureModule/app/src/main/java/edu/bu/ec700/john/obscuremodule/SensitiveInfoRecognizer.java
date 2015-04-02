package edu.bu.ec700.john.obscuremodule;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by John on 4/2/15.
 */
public class SensitiveInfoRecognizer {

    List<String> sensitive_strings = new ArrayList<>();
    List<Pattern> sensitive_patterns = new ArrayList<>();

    public void addSensitivePattern(String s) {
        Pattern pattern = Pattern.compile(s);
        sensitive_patterns.add(pattern);
    }

    public void addSensitiveString(String s) {
        sensitive_strings.add(s);
    }

    public void addSensitiveStrings(List<String> ss) {
        for (String s : ss) {
            sensitive_strings.add(s);
        }
    }

    public boolean isNodeSensitive(AccessibilityNodeInfo n) {
        String contents = n.getText().toString();
        for (String s : sensitive_strings) {
            if (contents.toLowerCase().contains(s.toLowerCase())) {
                return true;
            }
        }
        for (Pattern p : sensitive_patterns) {
            Matcher matcher = p.matcher(contents);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

}
