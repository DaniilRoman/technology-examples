package com.example.blogdb

import com.example.blogdb.entity.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class MainService(val userRepository: UserRepository,
                  val blogRepository: BlogRepository,
                  val articleRepository: ArticleRepository,
                  val commentRepository: CommentRepository) {

    @PostConstruct
    fun setup() {
        basicCreating()
    }

    fun basicCreating() {
        val user = User(name="user1")
        userRepository.save(user)

        println("=================== SAVE BLOG =====================")
        val blog = Blog(name="Java blog #1")
        blogRepository.save(blog)
        user.blog = blog
        println("=================== SAVE USER =====================")
        userRepository.save(user)

        println("=================== SAVE ARTICLE =====================")
        val article1 = Article(name="Java article #1", content = "Cool article, proove you 1", blog=blog)
        val article2 = Article(name="Java article #2", content = "Cool article, proove you 2", blog=blog)
        articleRepository.saveAll(listOf(article1, article2))

        println("=================== GET ARTICLE =====================")
        println(blogRepository.findWithArticles(blog.id!!).articles)

        println("=================== GET EVERYTHING =====================")
        println(userRepository.findEverythingById(user.id!!).get().blog!!.articles)

        println("=================== SAVE COMMENTS =====================")
        val comment1 = Comment(content="It's sucks", user = user)
        val comment2 = Comment(content="It's perfect", user = user)
        commentRepository.saveAll(listOf(comment1, comment2))
        article1.comments = listOf(comment1, comment2)
        articleRepository.save(article1)
        println("=================== SAVE INTERNAL COMMENTS =====================")
        val comment11 = Comment(content="It's sucks defenetly", user = user)
        val comment12 = Comment(content="It's defenetly defenetly", user = user)
        comment1.internalComments = listOf(comment11, comment12)
        commentRepository.save(comment1)

//        println("=================== DELETING =====================")
//        user.blog = null
//        userRepository.save(user)
//        blogRepository.delete(blog)
    }
}