package com.talentup.interview.assessment.solution.apiModels;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SmsRequest {

    @NotNull(message = "to is missing")
    @Size(min = 6, max = 16, message = "to is invalid")
    private String to;

    @NotNull(message = "from is missing")
    @Size(min = 6, max = 16, message = "from is invalid")
    private String from;

    @NotNull(message = "text is missing")
    @Size(min = 1, max = 120, message = "text is invalid")
    private String text;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
