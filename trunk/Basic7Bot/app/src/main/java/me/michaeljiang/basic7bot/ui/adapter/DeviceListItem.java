package me.michaeljiang.basic7bot.ui.adapter;

/**
 * Created by MichaelJiang on 2017/2/2.
 */

public class DeviceListItem {
    private String message;
    private boolean isSiri;

    public DeviceListItem(String msg, boolean siri) {
        message = msg;
        isSiri = siri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSiri() {
        return isSiri;
    }

    public void setSiri(boolean siri) {
        isSiri = siri;
    }
}
