package com.example.oa.biz.config

import com.example.oa.biz.config.properties.WechatProperties
import com.example.oa.biz.service.SysMenuService
import com.example.oa.biz.service.impl.auth.CustomAuthDenyHandler
import com.example.oa.biz.service.impl.auth.CustomAuthentication
import com.example.oa.biz.service.impl.auth.CustomTokenAuthPerRequest
import com.example.oa.util.jwt.JWTUtil
import com.github.benmanes.caffeine.cache.Cache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher
import org.springframework.web.filter.OncePerRequestFilter

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class CustomSecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val caffeine: Cache<Long, List<GrantedAuthority>>,
    private val sysMenuService: SysMenuService

) {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean(name = [BeanIds.AUTHENTICATION_MANAGER])
    fun authenticationMangerBean(): AuthenticationManager = authenticationConfiguration.authenticationManager


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        //安全设置
        http.csrf().disable()
        //跨域
        http.cors(Customizer.withDefaults())
        //拦截未认证路径
        http.authorizeHttpRequests {
            it.requestMatchers("/admin/system/index/login").permitAll()
                .requestMatchers("/admin/wechat/**").permitAll()
                .anyRequest().authenticated()
        }
        //添加自定义认证过滤器
        http.addFilterBefore(
            CustomAuthentication(authenticationConfiguration.authenticationManager),
            UsernamePasswordAuthenticationFilter::class.java
        )
        http.addFilterBefore(CustomTokenAuthPerRequest(caffeine, sysMenuService), LogoutFilter::class.java)
        //禁用session
        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        //无权限处理
        http.exceptionHandling {
            it.accessDeniedHandler(CustomAuthDenyHandler())
        }
        //登出
        http.logout {
            it.logoutUrl("/logout")
            it.logoutSuccessHandler { request, _, authentication ->
                authentication.isAuthenticated = false
                //删除登出用户缓存
                caffeine.invalidate(JWTUtil.getUserIdFromJWT(request.getHeader("token")))
            }
        }
        return http.build()
    }
}