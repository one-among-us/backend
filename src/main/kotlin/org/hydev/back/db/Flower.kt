package org.hydev.back.db

import org.hydev.back.int
import org.hydev.back.str
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Flower(
    @Id @GeneratedValue
    var id: int = 0,
    var personId: str = "",
    var flowers: int = 0
)

@Repository
interface FlowerRepo: JpaRepository<Flower, int>
{
    fun queryByPersonId(personId: str): Flower?
}
