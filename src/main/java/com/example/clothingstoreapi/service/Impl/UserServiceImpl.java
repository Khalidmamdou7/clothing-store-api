package com.example.clothingstoreapi.service.Impl;

import com.example.clothingstoreapi.dto.UpdateProfileReqDTO;
import com.example.clothingstoreapi.dto.UpdateProfileResDTO;
import com.example.clothingstoreapi.dto.UserProfileDTO;
import com.example.clothingstoreapi.entity.UserEntity;
import com.example.clothingstoreapi.repository.UserRepository;
import com.example.clothingstoreapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
// @Transactional ask
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByEmail(username).orElseThrow(() ->
            new UsernameNotFoundException("User not found in the database")
        );
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);

    }

    @Override
    public List<UserEntity> getAllUsers() {
        return (List<UserEntity>) userRepo.findAll();
    }

    @Override
    public ResponseEntity saveUser(UserProfileDTO user) {
        log.info("Saving new user {} to the database", user.getFullName());
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        userRepo.save(userEntity);
        return ResponseEntity.ok().body(user);
    }

    @Override
    public ResponseEntity updateUser(UpdateProfileReqDTO newUser) {
        log.info("Updating user with email: ", newUser.getEmail());
        UserEntity user;
        try {
            user = userRepo.findByEmail(newUser.getEmail()).get();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Old email is not valid");
        }
        if(newUser.getNewFullName() != null) {
            user.setFullName(newUser.getNewFullName());
        }
        if(newUser.getNewEmail() != null) {
            user.setEmail(newUser.getNewEmail());
        }
        if(newUser.getNewPassword() != null) {
            user.setPassword(newUser.getNewPassword());
        }
        userRepo.save(user);

        UpdateProfileResDTO updateProfileResDTO = modelMapper.map(user, UpdateProfileResDTO.class);
        return ResponseEntity.ok().body(updateProfileResDTO);

    }

    @Override
    public ResponseEntity getUser(String email) {
        log.info("Fetching user by email: {}", email);

        UserEntity user =  userRepo.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Email : " + email + " not found.")
                );
        UserProfileDTO userProfileDTO = modelMapper.map(user, UserProfileDTO.class);
        return ResponseEntity.ok().body(userProfileDTO);
    }
}
