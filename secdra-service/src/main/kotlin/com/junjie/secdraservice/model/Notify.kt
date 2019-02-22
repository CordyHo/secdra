package com.junjie.secdraservice.model

import com.junjie.secdraservice.contant.NotifyType
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Notify {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //这个是hibernate的注解/生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    var id: String? = null
    //接受者id
    var receiveId: String? = null
    //评论id
    var commentId: String? = null
    //图片作者id
    var authorId: String? = null
    //图片id
    var drawId: String? = null
    //评论人id
    var criticId: String? = null
    //回答者id
    var answererId: String? = null

    var notifyType: NotifyType? = null

    var content: String? = null

    var isRead: Boolean = false

    @CreatedDate
    var createDate: Date = Date()

    @LastModifiedDate
    var modifiedDate: Date = Date()
}