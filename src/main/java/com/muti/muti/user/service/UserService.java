package com.muti.muti.user.service;

import com.muti.muti.user.domain.User;
import com.muti.muti.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(com.muti.muti.user.dto.UserRegisterRequest request) {
        if (userRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 ID입니다.");
        }

        // 이메일 중복 검사 로직 변경
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (!user.getProvider().equals("local")) {
                // 소셜 계정으로 가입된 이메일인 경우
                throw new IllegalArgumentException("이미 " + user.getProvider() + " 계정으로 가입된 이메일입니다. " + user.getProvider() + " 로그인을 이용해주세요.");
            } else {
                // 로컬 계정으로 가입된 이메일인 경우
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        });

        User newUser = new User(request.getUserId(), passwordEncoder.encode(request.getPassword()), request.getEmail());
        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(),
                new ArrayList<>());
    }
}