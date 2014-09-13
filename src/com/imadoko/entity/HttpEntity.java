package com.imadoko.entity;

import java.util.Map;

public class HttpEntity {
    
    private String _url;
    
    private Map<String, String> _params;

    public String getUrl() {
        return _url;
    }

    public void setUrl(String url) {
        _url = url;
    }
    
    public Map<String, String> getParams() {
        return _params;
    }
    
    public void setParams(Map<String, String> params) {
        _params = params;
    }
    
}
