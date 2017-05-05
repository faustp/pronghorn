package com.pccw.bpss.qa.automation.data;

import java.util.Map;

/**
 * Created by FaustineP on 09/03/2017.
 */
public class TestCaseData {

    private Map<String, Map<String, String>> content;

    public TestCaseData(Map<String, Map<String, String>> content) {
        this.content = content;
    }

    public Map<String, Map<String, String>> getContent() {
        return content;
    }

    public void setContent(Map<String, Map<String, String>> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TestCaseData{" +
                "content=" + content +
                '}';
    }
}
