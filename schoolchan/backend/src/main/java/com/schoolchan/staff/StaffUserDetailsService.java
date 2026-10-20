package com.schoolchan.staff;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffUserDetailsService implements UserDetailsService {

    private final StaffUserRepository repository;

    public StaffUserDetailsService(StaffUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StaffUser staff = repository.findByUsername(username)
                .filter(StaffUser::isEnabled)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(
                staff.getUsername(),
                staff.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + staff.getRole())));
    }
}
