package com.junjie.secdraservice.dao

import com.junjie.secdraservice.model.Reply
import org.springframework.data.jpa.repository.JpaRepository

interface ReplyDAO : JpaRepository<Reply, String> {
    fun findAllByCommentIdOrderByCreateDateDesc(commentId: String): List<Reply>
}