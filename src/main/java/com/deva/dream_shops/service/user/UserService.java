package com.deva.dream_shops.service.user;

import com.deva.dream_shops.dto.UserDto;
import com.deva.dream_shops.exceptions.AlreadyExistsException;
import com.deva.dream_shops.exceptions.ResourceNotFoundException;
import com.deva.dream_shops.model.User;
import com.deva.dream_shops.repository.UserRepository;
import com.deva.dream_shops.request.CreateUserRequest;
import com.deva.dream_shops.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request).filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(request1 -> {
                    User user = new User();
                    user.setFirstName(request1.getFirstName());
                    user.setLastName(request1.getLastName());
                    user.setEmail(request1.getEmail());
                    user.setPassword(passwordEncoder.encode(request1.getPassword()));
                    return userRepository.save(user);
                }).orElseThrow(()->new AlreadyExistsException("Oops! "+request.getEmail()+" already exists"));
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setFirstName(request.getFirstName());
                    existingUser.setLastName(request.getLastName());
                    return userRepository.save(existingUser);
                }).orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(userRepository::delete,()->{
                  throw new RuntimeException("User not found");
                });
    }

    @Override
    public UserDto convertUserToDto(User user){
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }
}
