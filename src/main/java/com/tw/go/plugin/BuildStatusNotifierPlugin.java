package com.tw.go.plugin;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.utils.HttpUtils;
import com.tw.go.plugin.utils.JSONUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;

@Extension
public class BuildStatusNotifierPlugin implements GoPlugin {

    public static final String EXTENSION_NAME = "notification";
    public static final List<String> goSupportedVersions = asList("1.0");
    public static final String PLUGIN_ID = "lean.status.notifier";

    public static final String GET_PLUGIN_SETTINGS = "go.processor.plugin-settings.get";
    public static final String PLUGIN_SETTINGS_GET_CONFIGURATION = "go.plugin-settings.get-configuration";
    public static final String PLUGIN_SETTINGS_GET_VIEW = "go.plugin-settings.get-view";
    public static final String PLUGIN_SETTINGS_VALIDATE_CONFIGURATION = "go.plugin-settings.validate-configuration";
    public static final String REQUEST_NOTIFICATIONS_INTERESTED_IN = "notifications-interested-in";


    public static final String REQUEST_STAGE_STATUS = "stage-status";

    public static final String PLUGIN_SETTINGS_SERVER_BASE_URL = "server_base_url";

    public static final int NOT_FOUND_RESPONSE_CODE = 404;
    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int INTERNAL_ERROR_RESPONSE_CODE = 500;

    private GoApplicationAccessor goApplicationAccessor;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        this.goApplicationAccessor = goApplicationAccessor;
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) throws UnhandledRequestTypeException {
        String requestName = goPluginApiRequest.requestName();

        System.out.println("->>>>>>>>" + requestName);

        if (requestName.equals(PLUGIN_SETTINGS_GET_CONFIGURATION)) {
            return handleGetPluginSettingsConfiguration();
        } else if (requestName.equals(PLUGIN_SETTINGS_GET_VIEW)) {
            try {
                return handleGetPluginSettingsView();
            } catch (IOException e) {
                return renderJSON(500, String.format("Failed to find template: %s", e.getMessage()));
            }
        } else if (requestName.equals(REQUEST_NOTIFICATIONS_INTERESTED_IN)) {
            return handleNotificationsInterestedIn();

        } else if (requestName.equals(PLUGIN_SETTINGS_VALIDATE_CONFIGURATION)) {
            return handleValidatePluginSettingsConfiguration(goPluginApiRequest);
        } else if (requestName.equals(REQUEST_STAGE_STATUS)) {
            return handleStageNotification(goPluginApiRequest);
        }

        return renderJSON(NOT_FOUND_RESPONSE_CODE, null);
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return getGoPluginIdentifier();
    }

    private GoPluginApiResponse handleGetPluginSettingsConfiguration() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleGetPluginSettingsView() throws IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("template", IOUtils.toString(getClass().getResourceAsStream("/plugin-settings.template.html"), "UTF-8"));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleNotificationsInterestedIn() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("notifications", Arrays.asList(REQUEST_STAGE_STATUS));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleStageNotification(GoPluginApiRequest goPluginApiRequest) {
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.fromJSON(goPluginApiRequest.requestBody());
        System.out.println(dataMap.toString());

        Map<String, Object> response = new HashMap<String, Object>();
        List<String> messages = new ArrayList<String>();

        int responseCode = SUCCESS_RESPONSE_CODE;
        try {
            PluginSettings pluginSettings = getPluginSettings();
            HttpUtils.doPost(pluginSettings.getServerBaseURL(), goPluginApiRequest.requestBody());
        } catch (Exception e) {
            responseCode = INTERNAL_ERROR_RESPONSE_CODE;
            response.put("status", "failure");
            messages.add(e.getMessage());
        }

        response.put("messages", messages);
        return renderJSON(responseCode, response);
    }

    private GoPluginApiResponse handleValidatePluginSettingsConfiguration(GoPluginApiRequest goPluginApiRequest) {
        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private Map<String, Object> createField(String displayName, String defaultValue, boolean isRequired, boolean isSecure, String displayOrder) {
        Map<String, Object> fieldProperties = new HashMap<String, Object>();
        fieldProperties.put("display-name", displayName);
        fieldProperties.put("default-value", defaultValue);
        fieldProperties.put("required", isRequired);
        fieldProperties.put("secure", isSecure);
        fieldProperties.put("display-order", displayOrder);
        return fieldProperties;
    }

    public PluginSettings getPluginSettings() {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("plugin-id", PLUGIN_ID);
        GoApiResponse response = goApplicationAccessor.submit(
                createGoApiRequest(GET_PLUGIN_SETTINGS, JSONUtils.toJSON(requestMap))
        );

        Map<String, String> responseBodyMap = response.responseBody() == null ?
                new HashMap<String, String>() :
                (Map<String, String>) JSONUtils.fromJSON(response.responseBody());

        return new PluginSettings(responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL));
    }

    private GoApiRequest createGoApiRequest(final String api, final String responseBody) {
        return new GoApiRequest() {
            @Override
            public String api() {
                return api;
            }

            @Override
            public String apiVersion() {
                return "1.0";
            }

            @Override
            public GoPluginIdentifier pluginIdentifier() {
                return getGoPluginIdentifier();
            }

            @Override
            public Map<String, String> requestParameters() {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders() {
                return null;
            }

            @Override
            public String requestBody() {
                return responseBody;
            }
        };
    }

    private GoPluginIdentifier getGoPluginIdentifier() {
        return new GoPluginIdentifier(EXTENSION_NAME, goSupportedVersions);
    }

    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        final String json = response == null ? null : new GsonBuilder().create().toJson(response);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }


}