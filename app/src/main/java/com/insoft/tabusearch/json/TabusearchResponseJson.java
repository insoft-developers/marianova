package com.insoft.tabusearch.json;

import com.insoft.tabusearch.model.Tabulist;

import java.util.ArrayList;
import java.util.List;

public class TabusearchResponseJson {
    private String message;
    private List<Tabulist> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Tabulist> getData() {
        return data;
    }

    public void setData(List<Tabulist> data) {
        this.data = data;
    }
}
