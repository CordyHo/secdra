package com.junjie.secdraservice.service

import com.junjie.secdraservice.model.User
import java.util.*

/**
 * 用户的服务
 *
 * @author fjj
 */
interface IUserService {
    /**
     * 注册
     */
    fun register(user: User): User

    /**
     * 登录
     */
    fun login(phone: String, password: String): User

    /**
     * 修改密码
     */
    fun rePassword(phone: String, password: String, rePasswordTime: Date): User

    /**
     * 获取用户信息
     */
    fun getInfo(id: String): User

    /**
     * 修改用户信息
     */
    fun updateInfo(user: User): User
}