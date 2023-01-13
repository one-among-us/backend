package org.hydev.back.ai

enum class HarmLevel(msg: String? = null) {
    SAFE,
    HARMFUL("❌ AI 标记: 有害"),
    MAYBE("⚠️ AI 标记: 可能有害"),
    OFFLINE("🔌 AI 连接失败"),
    INVALID("😕 AI 没有理解"),
    TOO_LONG("😴 AI 因为太长无法分析")
}
