package com.example.oa.util.jwt

import io.jsonwebtoken.CompressionCodecs
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*
import kotlin.jvm.Throws

class JWTUtil {
    companion object {
        private const val tokenExpirationMS: Long = 1000 * 60 * 60 * 24
        private const val tokenSignKey: String = "OA-SIGN-KEY"

        @JvmStatic
        fun generateJWT(userId: Long, username: String): String {
            return Jwts.builder()
                //设置主题 分类
                .setSubject("AUTH-USER")
                //有效期
                .setExpiration(Date(System.currentTimeMillis() + tokenExpirationMS))
                //设置携带载荷
                .claim("userId", userId)
                .claim("username", username)
                //设置签名
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                //压缩token
                .compressWith(CompressionCodecs.GZIP)
                .compact()
        }

        @JvmStatic
        @Throws(Exception::class)
        fun getUserIdFromJWT(jwtToken: String): Long? {
            try {
                if (jwtToken.isBlank()) return null
                //获取载荷
                val userId = Jwts.parser()
                    .setSigningKey(tokenSignKey)
                    .parseClaimsJws(jwtToken)
                    .body["userId"]
                return when (userId) {
                    is Long -> userId
                    is Int -> userId.toLong()
                    else -> null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception("转换jwt令牌时出错")
            }
        }

        @JvmStatic
        @Throws(Exception::class)
        fun getUsernameFromJWT(jwtToken: String): String? {
            try {
                if (jwtToken.isBlank()) return null
                //获取载荷
                val username = Jwts.parser()
                    .setSigningKey(tokenSignKey)
                    .parseClaimsJws(jwtToken)
                    .body["username"]
                return when (username) {
                    is String -> username
                    else -> null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception("转换jwt令牌时出错")
            }
        }
    }
}