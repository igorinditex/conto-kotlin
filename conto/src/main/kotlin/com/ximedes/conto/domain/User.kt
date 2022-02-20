package com.ximedes.conto.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

enum class Role : GrantedAuthority {
    ADMIN, USER;

    override fun getAuthority() = "ROLE_$name"
}


data class User(internal val username: String, internal val password: String, val role: Role) : UserDetails {


    override fun getAuthorities() = setOf(role)
    override fun getPassword() = password
    override fun getUsername() = username
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true

}

val User?.isAdmin: Boolean
    get() = (this?.role == Role.ADMIN)

fun User?.hasAccessTo(account: Account): Boolean {
    val isOwner = account.owner == this?.username
    return isOwner || isAdmin
}