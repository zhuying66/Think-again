package com.zhuying.zaikaolv.data.questions

/** 分类题的一个选项:分值 1..5 + 展示文案(分值不展示给用户,仅用于本地评分) */
data class ScaleOption(val score: Int, val label: String)

/** 一道「问题规模」分类题 */
data class ScaleQuestion(
    val id: String,
    val title: String,
    val options: List<ScaleOption>,
)

/**
 * 自动判断问题规模的 5 道标准题(完全本地,不接入任何 AI / API / 网络)。
 *
 * 五道题分别量化:影响范围、持续时间、不可逆性、代价、对他人的影响。
 * 每题 1~5 分,总分 5~25,由 [com.zhuying.zaikaolv.domain.ScaleClassifier] 判定归属。
 * 注意:Q3 的分值代表「不可逆程度」,越难改变分越高。
 */
object ScaleQuestions {

    private fun q(id: String, title: String, vararg options: Pair<Int, String>) =
        ScaleQuestion(id, title, options.map { ScaleOption(it.first, it.second) })

    val all: List<ScaleQuestion> = listOf(
        q(
            "q1", "这件事如果做错了，对你的生活影响有多大？",
            1 to "几乎没有影响",
            2 to "影响很小",
            3 to "有一定影响",
            4 to "影响比较大",
            5 to "可能产生非常大的影响",
        ),
        q(
            "q2", "这件事的结果会影响你多久？",
            1 to "几个小时以内",
            2 to "几天",
            3 to "几周",
            4 to "几个月",
            5 to "一年以上或长期",
        ),
        // 这一题的分值代表「不可逆程度」，越难改变分数越高
        q(
            "q3", "如果最后发现自己选错了，你能轻易改变回来吗？",
            1 to "随时都可以改变",
            2 to "比较容易改变",
            3 to "需要付出一些代价",
            4 to "改变起来比较困难",
            5 to "几乎无法恢复或改变",
        ),
        q(
            "q4", "做出这个决定可能需要承担多大的代价？",
            1 to "几乎没有代价",
            2 to "代价很小",
            3 to "有一定代价",
            4 to "代价比较大",
            5 to "代价非常大",
        ),
        q(
            "q5", "这个决定会影响其他重要的人吗？",
            1 to "基本只影响自己",
            2 to "可能轻微影响别人",
            3 to "会影响身边的人",
            4 to "会明显影响重要的人",
            5 to "会对多人产生长期或重大影响",
        ),
    )
}
