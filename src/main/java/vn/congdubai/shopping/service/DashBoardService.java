package vn.congdubai.shopping.service;

import org.springframework.stereotype.Service;

import vn.congdubai.shopping.repository.UserRepository;

@Service
public class DashBoardService {
    private final UserRepository userRepository;

    public DashBoardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lấy người dùng theo ngày
    public long HandleCountUserByDay() {
        return userRepository.countUsersByToday();
    }
}
