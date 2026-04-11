package tn.esprit.pi.security;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return buildUserDetails(user);
    }

    public CustomUserDetails loadUserById(Long userId) {
        User user = userRepository.findByIdWithRoleAndTenant(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return buildUserDetails(user);
    }

    private CustomUserDetails buildUserDetails(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getRole().name())
        );
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(), authorities);
    }
}
