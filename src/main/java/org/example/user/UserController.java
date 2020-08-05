package org.example.user;

import org.example.controller.BaseController;
import org.example.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("user")
@RestController
public class UserController extends BaseController<User> {

    @Autowired
    private UserService userService;

    @Override
    public BaseService service() {
        return userService;
    }

}
