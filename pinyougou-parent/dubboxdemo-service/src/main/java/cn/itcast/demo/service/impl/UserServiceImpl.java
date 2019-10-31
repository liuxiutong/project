package cn.itcast.demo.service.impl;

import cn.itcast.demo.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class UserServiceImpl implements UserService {


    @Override
    public String getName() {
        return "itcast";
    }
}
