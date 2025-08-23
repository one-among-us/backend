package org.hydev.back.ai

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.getOrNull
import org.hydev.back.secrets
import java.time.ZoneId
import java.time.ZonedDateTime

fun getMorningMsg(): String
{
    val today = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toLocalDate().toString()
    val url = "https://api.github.com/repos/one-among-us/Morning/contents/morning/data/$today/response.txt"
    val (_, response, result) = url.httpGet()
        .header("Authorization", "Bearer ${secrets.githubToken}")
        .header("Accept", "application/vnd.github.v3.raw")
        .responseString()
    println("GET $url: ${response.statusCode}")
    return result.getOrNull() ?: "早安！今天早安消息 bot 似乎坏掉了，为什么呢... 呼叫一下 @hykilpikonna qwq"
}

fun main(args: Array<String>) {
    println(getMorningMsg())
}
