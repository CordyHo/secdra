package com.junjie.secdraweb.controller

import com.junjie.secdracore.annotations.Auth
import com.junjie.secdracore.annotations.CurrentUserId
import com.junjie.secdracore.annotations.RestfulPack
import com.junjie.secdracore.util.IpUtil
import com.junjie.secdraservice.model.Statistical
import com.junjie.secdraservice.service.StatisticalService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


/**
 * @author fjj
 * 统计的控制器
 */
@RestController
@RequestMapping("statistical")
class StatisticalController(private val statisticalService: StatisticalService) {

    @Auth
    @PostMapping("/save")
    @RestfulPack
    fun save(@CurrentUserId userId: String,path:String,request: HttpServletRequest): Statistical {
        val statistical = Statistical()
        statistical.userId = userId
        statistical.ip = IpUtil.getIpAddress(request)
        statistical.path = path
        return statisticalService.save(statistical)
    }
}