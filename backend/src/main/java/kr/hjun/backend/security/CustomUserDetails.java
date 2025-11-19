package kr.hjun.backend.security;

import kr.hjun.backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean active;

    public CustomUserDetails(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities, boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.active = active;
    }

    // 사용자 ID를 반환한다.
    public Long getId() {
        return id;
    }

    // 사용자 이메일을 반환한다.
    @Override
    public String getUsername() {
        return email;
    }

    // 암호화된 비밀번호를 반환한다.
    @Override
    public String getPassword() {
        return password;
    }

    // 권한 목록을 반환한다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 계정 만료 여부를 반환한다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부를 반환한다.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명 만료 여부를 반환한다.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성 여부를 반환한다.
    @Override
    public boolean isEnabled() {
        return active;
    }

    // 엔티티를 UserDetails로 변환한다.
    public static CustomUserDetails from(User user) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), authorities, user.isActive());
    }
}
