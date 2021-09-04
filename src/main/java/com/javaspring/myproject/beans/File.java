package com.javaspring.myproject.beans;

public class File {
    private String filename;
    private String username;
    private String url;
    private String date_;
    private String size_;

    public File() {
    }

    public File(String filename, String username, String url, String date, String size) {
        this.filename = filename;
        this.username = username;
        this.url = url;
        this.date_ = date;
        this.size_ = size;
    }

    @Override
    public String toString() {
        return "File{" +
                "filename='" + filename + '\'' +
                ", username='" + username + '\'' +
                ", url='" + url + '\'' +
                ", date='" + date_ + '\'' +
                ", size='" + size_ + '\'' +
                '}';
    }

    public String getFilename() {
        return filename;
    }

    public String getUsername() {
        return username;
    }

    public String getUrl() {
        return url;
    }

    public String getDate_() {
        return date_;
    }

    public String getSize_() {
        return size_;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDate_(String date_) {
        this.date_ = date_;
    }

    public void setSize_(String size_) {
        this.size_ = size_;
    }

}
