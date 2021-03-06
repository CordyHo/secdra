package com.junjie.secdraadmin.controller

import com.junjie.secdracore.util.EmojiUtil
import com.junjie.secdraservice.dao.*
import com.junjie.secdraservice.document.DrawDocument
import com.junjie.secdraservice.model.Draw
import com.junjie.secdraservice.model.PixivDraw
import com.junjie.secdraservice.model.PixivError
import com.junjie.secdraservice.model.Tag
import com.junjie.secdraservice.service.DrawService
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.imageio.ImageIO
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.data.elasticsearch.core.query.SearchQuery




@RestController
@RequestMapping("draw")
class DrawController(private var drawDAO: DrawDAO, private var userDAO: UserDAO, private val drawService: DrawService, private val pixivDrawDAO: PixivDrawDAO, private val pixivErrorDAO: PixivErrorDAO, private val elasticsearchTemplate: ElasticsearchTemplate

                     , private val drawDocumentDAO: DrawDocumentDAO) {
    @PostMapping("/init")
    fun init(folderPath: String): Any {
        var i = 0
        val map = HashMap<String, Array<String>>()
        val userList = userDAO.findAll()
        try {
            val typeNameList = File(folderPath).list()
            for (typeName in typeNameList) {
                val typePath = "$folderPath/$typeName"
                val fileNameList = File(typePath).list()
                fileNameList.toList().filter { it.toLowerCase().endsWith(".png") || it.toLowerCase().endsWith(".jpg") || it.toLowerCase().endsWith(".jpeg") }
                map[typeName] = fileNameList
                for (fileName in fileNameList) {
                    val picture = File("$typePath/$fileName")
                    val read = ImageIO.read(FileInputStream(picture))
                    val draw = Draw()
                    draw.userId = userList.shuffled().first().id
                    draw.width = read.width.toLong()
                    draw.height = read.height.toLong()
                    draw.url = fileName
                    draw.name = typeName
                    draw.introduction = "这是一张很好看的图片，这是我从p站上下载回来的，侵删！"
                    val tag = Tag()
                    tag.name = typeName
                    val tagList = mutableListOf<Tag>()
                    tagList.add(tag)
                    draw.tagList = tagList
                    drawDAO.save(draw)
                    i++
                }
            }
            println(i)
            return map
        } catch (e: Exception) {
            println(e.message)
            throw e
        }
    }

    //保存pixiv
    @PostMapping("/pixivSave")
    fun pixivSave(pixivId: String, name: String, userName: String, userId: String, tagList: String): Boolean {
        val p = PixivDraw()
        p.pixivId = pixivId
        p.name = EmojiUtil.emojiChange(name).trim()
        p.userName = EmojiUtil.emojiChange(userName).trim()
        p.userId = userId
        p.tagList = EmojiUtil.emojiChange(tagList).trim()
        try {
            pixivDrawDAO.save(p)
        } catch (e: Exception) {
            val pixivError = PixivError()
            pixivError.pixivId = pixivId
            pixivError.message = e.message
            pixivErrorDAO.save(pixivError)
        }
        return true
    }

    //保存pixiv采集错误
    @PostMapping("/pixivError")
    fun pixivSave(pixivId: String, message: String): PixivError {
        val pixivError = PixivError()
        pixivError.pixivId = pixivId
        pixivError.message = message
        return pixivErrorDAO.save(pixivError)
    }

    //从pixiv初始化标签
    @GetMapping("/initTag")
    fun initTag(): Boolean {
        val pixivDrawList = pixivDrawDAO.findAllByInit(false)
        for (pixivDraw in pixivDrawList) {
            val drawList = drawDAO.findAllByUrlLike(pixivDraw.pixivId!! + "%")
            for (draw in drawList) {
                pixivDraw.init = true

                draw.name = if (pixivDraw.name.isNullOrBlank()) {
                    draw.name
                } else {
                    pixivDraw.name
                }

                val tagList = pixivDraw.tagList!!.split("|")
                val tagNameList = draw.tagList.map { it.name }
                for (addTagName in tagList) {
                    if (tagNameList.indexOf(addTagName) == -1) {
                        val tag = Tag()
                        tag.name = addTagName
                        draw.tagList.add(tag)
                    }
                }
                for (tag in draw.tagList.toList()) {
                    if (tagList.indexOf(tag.name) == -1) {
                        draw.tagList.remove(tag)
                    }
                }

                drawDAO.save(draw)
            }

            pixivDrawDAO.save(pixivDraw)

        }
        return true
    }

    @GetMapping("/check")
    fun check(): Int {
        val drawList = drawDAO.findAll()
        var i = 0
        for (draw in drawList) {
            if (draw.tagList.size == 0) {
                i++
            }
        }
        return i
    }

    //获取任务
    @GetMapping("/listTask")
    fun listTask(): MutableList<String> {
        val drawList = drawDAO.findAll()
        val result = mutableListOf<String>()
        for (draw in drawList) {
            val pixivId = draw.url!!.split("_")[0]
            val pixivDrawList = pixivDrawDAO.findAllByPixivId(pixivId)
            val pixivErrorList = pixivErrorDAO.findAllByPixivId(pixivId)
            if (pixivDrawList.isEmpty() && pixivErrorList.isEmpty()) {
                result.add(pixivId)
            }
        }
        return result
    }


    @GetMapping("/initIndex")
    fun initIndex() {
        elasticsearchTemplate.createIndex(DrawDocument::class.java)
    }

    @GetMapping("/initEs")
    fun initEs(): Long {
        return drawService.synchronizationIndexDraw()
    }

    @GetMapping("/getEs")
    fun getEs(@PageableDefault(value = 20) pageable: Pageable): Page<DrawDocument> {
        return drawDocumentDAO.findAll(pageable)
    }

    @PostMapping("/update")
    fun update(@RequestParam("tagList") tagList: Array<String>?): DrawDocument {
        //57150551_p0.jpg
        val draw = drawService.get("402880e567208788016720898f230008")
        if (tagList != null && !tagList.isEmpty()) {
            val tagNameList = draw.tagList.map { it.name }
            for (addTagName in tagList) {
                if (tagNameList.indexOf(addTagName) == -1) {
                    val tag = Tag()
                    tag.name = addTagName

                    draw.tagList.add(tag)
                }
            }
            for (tag in draw.tagList.toList()) {
                if (tagList.indexOf(tag.name) == -1) {
                    draw.tagList.remove(tag)
                }
            }
        }
        return drawService.save(draw)
    }

    @PostMapping("/delete")
    fun delete() {
        //57150551_p0.jpg
        drawDAO.deleteById("402880e567208788016720898f230008")
    }

}
