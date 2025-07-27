package com.hiveform.controller.impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiveform.controller.IAuthController;

@RestController
@RequestMapping("/api/auth")
public class AuthController implements IAuthController {

}
