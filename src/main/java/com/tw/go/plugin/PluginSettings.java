package com.tw.go.plugin;

public class PluginSettings {
    private String serverBaseURL;

    public PluginSettings() {
    }

    public PluginSettings(String serverBaseURL) {
        this.serverBaseURL = serverBaseURL;
    }

    public String getServerBaseURL() {
        return serverBaseURL;
    }

    public void setServerBaseURL(String serverBaseURL) {
        this.serverBaseURL = serverBaseURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginSettings that = (PluginSettings) o;
        return !(serverBaseURL != null ? !serverBaseURL.equals(that.serverBaseURL) : that.serverBaseURL != null);
    }

    @Override
    public int hashCode() {
        return serverBaseURL != null ? serverBaseURL.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PluginSettings{" +
                "serverBaseURL='" + serverBaseURL + '\'' +
                '}';
    }
}




