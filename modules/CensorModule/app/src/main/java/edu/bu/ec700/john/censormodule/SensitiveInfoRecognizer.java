package edu.bu.ec700.john.censormodule;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by John on 4/2/15.
 */
public class SensitiveInfoRecognizer {

    List<String> sensitive_strings = new ArrayList<>();
    HashMap<String, Pattern> sensitive_patterns = new HashMap<>();

    public void addSensitivePattern(String s) {
        Pattern pattern = Pattern.compile(s);
        sensitive_patterns.put(s, pattern);
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
        for (Pattern p : sensitive_patterns.values()) {
            Matcher matcher = p.matcher(contents);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public String sanitize(AccessibilityNodeInfo node) {
        String contents = node.getText().toString();
        for (String s : sensitive_strings) {
            if (contents.toLowerCase().contains(s.toLowerCase())) {
                contents = contents.replaceAll("(?i)" + s, "*****");
            }
        }
        Iterator it = sensitive_patterns.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Matcher matcher = ((Pattern) pair.getValue()).matcher(contents);
            if (matcher.matches()) {
                contents = contents.replaceAll((String) pair.getKey(), "*****");
            }
        }
        return contents;
    }

}
