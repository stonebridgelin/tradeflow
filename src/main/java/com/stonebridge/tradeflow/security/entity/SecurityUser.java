package com.stonebridge.tradeflow.security.entity;

import com.mysql.cj.util.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Thor
 * @公众号 Java架构栈
 */
public class SecurityUser implements UserDetails {

    //当前登陆的用户
    private User currentUserInfo;

    //存放权限列表
    private List<String> permissionValueList;

    public User getCurrentUserInfo() {
        return currentUserInfo;
    }

    public void setCurrentUserInfo(User currentUserInfo) {
        this.currentUserInfo = currentUserInfo;
    }

    public List<String> getPermissionValueList() {
        return permissionValueList;
    }

    public void setPermissionValueList(List<String> permissionValueList) {
        this.permissionValueList = permissionValueList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String permissionValue : permissionValueList) {
            if (!StringUtils.isNullOrEmpty(permissionValue)) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permissionValue);
                authorities.add(authority);
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.currentUserInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return this.currentUserInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
