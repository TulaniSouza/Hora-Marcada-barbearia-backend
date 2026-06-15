package dev.guilhermesilva.atom_backend.security;

import dev.guilhermesilva.atom_backend.repository.BarberRepository;
import dev.guilhermesilva.atom_backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final BarberRepository barberRepository;
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username.trim().toLowerCase();

        return barberRepository.findByEmail(email)
                .map(UserDetails.class::cast)
                .or(() -> customerRepository.findByEmail(email).map(UserDetails.class::cast))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}