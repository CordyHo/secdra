package com.junjie.secdraservice.model

import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * 回复
 * @author fjj
 */
@Entity
class Reply {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //这个是hibernate的注解/生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    var id: String? = null
    //评论id
    var commentId: String? = null
    //图片作者id
    var authorId: String? = null
    //评论人id
    var criticId: String? = null
    //回答者id
    var answererId: String? = null
    //图片id
    var drawId: String? = null

    var content: String? = null

    @CreatedDate
    var createDate: Date = Date()

    @LastModifiedDate
    var modifiedDate: Date = Date()
}