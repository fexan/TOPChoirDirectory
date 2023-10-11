package com.topchoir.directory.service;


import com.topchoir.directory.domain.Member;
import com.topchoir.directory.repository.MemberRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Arrays;

@Service
public class CustomMemberDetailsService implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String userId) {
         User currentUser = new User(userId, "tempPassword",
                 Arrays.asList(
                         new SimpleGrantedAuthority("ROLE_USER"),
                         new SimpleGrantedAuthority("PERM_FOO_READ"),
                         new SimpleGrantedAuthority("PERM_FOO_WRITE")));

        if (currentUser == null) {
            throw new UsernameNotFoundException(userId);
        }

        return currentUser;
    }
}