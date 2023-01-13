package org.hydev.back.db

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Ban(
    @Id
    var ip: String = "",
    var reason: String = "",
)

@Repository
interface BanRepo: JpaRepository<Ban, Int>
{
    fun queryByIp(ip: String): Ban?
}
