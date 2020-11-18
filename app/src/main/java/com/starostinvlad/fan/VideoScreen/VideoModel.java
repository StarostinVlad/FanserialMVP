package com.starostinvlad.fan.VideoScreen;

import org.jsoup.nodes.Document;

import okhttp3.Cookie;

public class VideoModel {

}

class Page {
    private Document document;
    private Cookie cookie;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    public Page(Document document, Cookie cookie) {
        this.document = document;
        this.cookie = cookie;
    }
}

