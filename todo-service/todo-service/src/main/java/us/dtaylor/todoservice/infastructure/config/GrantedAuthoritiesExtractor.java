package us.dtaylor.todoservice.infastructure.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {

    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, List<String>> realmAccess = (Map<String, List<String>>) jwt.getClaims().getOrDefault("realm_access", Collections.emptyMap());
        Stream<String> rolesStream = realmAccess.getOrDefault("roles", Collections.emptyList())
                .stream()
                .map(this::transformRole);

        Stream<String> scopesStream = Arrays.stream(
                jwt.getClaims()
                        .getOrDefault("scope", "")
                        .toString()
                        .split(" "))
                .map(String::trim);

        return Stream.concat(rolesStream, scopesStream)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    private String transformRole(String role) {
        return "ROLE_" + role.toUpperCase();
    }


}
