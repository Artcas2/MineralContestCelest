package fr.synchroneyes.mineral.Utils.Log;

import java.text.DateFormat;
import java.util.Date;
import org.json.JSONObject;

public class Log {
    private String type;
    private String content;
    private Date hour;
    private String cause;
    private int id;

    public Log(String type, String content, String cause) {
        this.type = type;
        this.content = content;
        this.hour = new Date();
        this.cause = cause;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String toJson() {
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(3, 3);
        JSONObject value = new JSONObject();
        value.put("type", this.type);
        value.put("content", this.content);
        value.put("date", shortDateFormat.format(this.hour));
        value.put("cause", this.cause);
        return value.toString();
    }

    public JSONObject toJsonObject() {
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(3, 3);
        JSONObject value = new JSONObject();
        value.put("type", this.type);
        value.put("content", this.content);
        value.put("date", shortDateFormat.format(this.hour));
        value.put("cause", this.cause);
        return value;
    }
}

