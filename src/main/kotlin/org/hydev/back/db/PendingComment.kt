package org.hydev.back.db

import org.hibernate.annotations.Type
import org.hibernate.Hibernate
import org.hydev.back.bool
import org.hydev.back.int
import org.hydev.back.str
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.sql.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class PendingComment(
    @Id @GeneratedValue
    var id: int = 0,
    var personId: str = "",

    @Type(type = "text")
    var content: str = "",

    var submitter: str = "",
    var email: str = "",
    var date: Date = Date(0),
    var approved: bool = false,
    var ip: str = ""
)
{
    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as PendingComment

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
    override fun toString() = this::class.simpleName + "(id = $id )"
}

@Repository
interface PendingCommentRepo: JpaRepository<PendingComment, int>
{
    fun queryById(id: int): PendingComment?
    fun queryByPersonId(personId: str): PendingComment?
}
