package com.example.blogdb.entity

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, Int> {

    @EntityGraph(value = "user-with-blog-with-articles")
    fun findEverythingById(id: Int): Optional<User> {
        return findById(id)
    }
}

@Repository
interface BlogRepository: JpaRepository<Blog, Int> {
    @Query("SELECT blog FROM Blog blog JOIN FETCH blog.articles WHERE blog.id = ?1")
    fun findWithArticles(id: Int): Blog
}

@Repository
interface ArticleRepository: JpaRepository<Article, Int>

@Repository
interface CommentRepository: JpaRepository<Comment, Int>
