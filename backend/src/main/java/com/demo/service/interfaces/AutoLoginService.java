package com.demo.service.interfaces;

import com.demo.dto.AutoLoginRequest;
import com.demo.dto.AutoLoginResponse;

public interface AutoLoginService {
    AutoLoginResponse performAutoLogin(String userId, String systemId);
}