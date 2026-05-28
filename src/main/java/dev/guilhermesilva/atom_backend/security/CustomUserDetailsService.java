package dev.guilhermesilva.atom_backend.security;

import dev.guilhermesilva.atom_backend.repository.BarberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final BarberRepository barberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return barberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Barber not found"));
    }
}