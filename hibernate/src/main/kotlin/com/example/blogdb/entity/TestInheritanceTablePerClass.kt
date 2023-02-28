package com.example.blogdb.entity

import jakarta.annotation.PostConstruct
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.UUID

@Entity
@Table(name="free_article_1")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
open class FreeArticle1(
    @Column(nullable = false, unique = false)
    open val viewCount: Int = 0,
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    open val id: UUID?=null,
) {
    override fun toString(): String {
        return id.toString()
    }
}

@Entity
@Table(name="paid_article_1")
class PaidArticle1(
    var tags: String,
    viewCount: Int = 10,
    id: UUID?=null,
): FreeArticle1(viewCount, id) {
    override fun toString(): String {
        return id.toString()
    }
}

@Repository
interface PaidArticleRepository1: JpaRepository<PaidArticle1, Int>

@Repository
interface FreeArticleRepository1: JpaRepository<FreeArticle1, Int> {
    fun findByViewCountGreaterThan(viewCount: Int): List<FreeArticle1>
}

@Service
class ArticleMetadataService1(val articleMetadataRepository: FreeArticleRepository1) {
    @PostConstruct
    fun setup() {
        val metadata = mutableListOf<FreeArticle1>()
        metadata.add(FreeArticle1(viewCount = 1))
        metadata.add(PaidArticle1("sport,football"))
        articleMetadataRepository.saveAll(metadata)
        println("=========== INHERITANCE QUERY (TABLE PER CLASS) ==================")
        println(articleMetadataRepository.findByViewCountGreaterThan(0))
    }
}