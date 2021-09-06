package com.javaspring.myproject.service;

import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

public interface IHttpClientService {
   String clientService(String url, HttpMethod method, MultiValueMap<String, String> params);
}
