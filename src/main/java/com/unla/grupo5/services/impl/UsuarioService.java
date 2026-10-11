package com.unla.grupo5.services.impl;

import com.unla.grupo5.entities.Rol;
import com.unla.grupo5.entities.Usuario;
import com.unla.grupo5.repositories.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("ServicioUsuario")
public class UsuarioService implements UserDetailsService {

    private UsuarioRepository repositorioUsuario;

    public UsuarioService(UsuarioRepository repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repositorioUsuario.findByUsuarioAndFetchRol(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        return buildUser(usuario, buildGrantedAuthorities(usuario.getRol()));
    }

    private UserDetails buildUser(Usuario usuario, List<GrantedAuthority> grantedAuthorities) {
        return new org.springframework.security.core.userdetails.User(usuario.getNombreUsuario(),
                usuario.getPassword(), usuario.isActivo(), true, true, true, grantedAuthorities);
    }

    private List<GrantedAuthority> buildGrantedAuthorities(Rol rol) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getRol()));
        return new ArrayList<>(grantedAuthorities);
    }

}
