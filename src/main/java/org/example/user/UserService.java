package org.example.user;

import org.example.repository.BaseRepository;
import org.example.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User, Long> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public BaseRepository<User, Long> repository() {
        return userRepository;
    }
}
