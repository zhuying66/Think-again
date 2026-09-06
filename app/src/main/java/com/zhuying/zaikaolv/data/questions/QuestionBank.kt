package com.zhuying.zaikaolv.data.questions

import com.zhuying.zaikaolv.data.model.Question
import com.zhuying.zaikaolv.data.model.QuestionMode

/**
 * 题库集中管理,与 UI / 算法解耦,便于后续替换、增删或改文案。
 * dir=+1:越认同越倾向「做」;dir=-1:越认同越倾向「不做」。
 */
object QuestionBank {

    private val SMALL = listOf(
        Question("s1", "这件事是否符合你目前真正想要的目标？", "目标契合", yesScore = 2, noScore = -1),
        Question("s2", "如果去做，这件事是否能给你带来实际的好处？", "实际收益", yesScore = 2, noScore = -1),
        Question("s3", "为了做这件事，你是否需要付出明显的代价？", "付出代价", yesScore = -2, noScore = 2),
        Question("s4", "做这件事是否存在可能让你难以承受的负面后果？", "负面后果", yesScore = -2, noScore = 2, highRisk = true),
        Question("s5", "如果现在不做，你以后是否可能因为错过它而后悔？", "错过后悔", yesScore = 2, noScore = -2),
        Question("s6", "如果去做这件事，你真的会感到开心吗？", "内心快乐", yesScore = 2, noScore = -2),
    )

    private val BIG = listOf(
        Question("b1", "这件事是否符合你目前真正想要的目标？", "目标契合", yesScore = 3, noScore = -2),
        Question("b2", "这件事是否能帮助你成为你希望成为的人？", "自我成长", yesScore = 2, noScore = -1),
        Question("b3", "即使没有人知道，你是否依然觉得这件事值得去做？", "内在价值", yesScore = 1, noScore = -1),
        Question("b4", "这件事是否能给你带来实际且长期的好处？", "长期收益", yesScore = 3, noScore = -2),
        Question("b5", "这件事是否能带来一个以后可能很难再次获得的机会？", "稀缺机会", yesScore = 2, noScore = -1),
        Question("b6", "如果去做，这件事得到的收益是否值得你付出的时间和精力？", "投入产出", yesScore = 3, noScore = -2),
        Question("b7", "为了做这件事，你是否需要明显牺牲其他重要的事情？", "牺牲代价", yesScore = -2, noScore = 2),
        Question("b8", "这件事是否会给你的时间、金钱或精力带来较大的负担？", "负担程度", yesScore = -2, noScore = 2),
        Question("b9", "如果结果没有达到预期，你是否能够接受付出的代价？", "承受预期", yesScore = 2, noScore = -2),
        Question("b10", "这件事是否存在可能造成严重负面后果的风险？", "严重后果", yesScore = -5, noScore = 3, highRisk = true),
        Question("b11", "如果事情的发展完全不如预期，你是否有能力承担后果？", "承担能力", yesScore = 2, noScore = -3, conditionGate = true),
        Question("b12", "你是否已经充分了解这件事可能带来的主要风险？", "风险了解", yesScore = 2, noScore = -2),
        Question("b13", "你现在是否已经具备做好这件事所需要的基本条件？", "基本条件", yesScore = 2, noScore = -2, conditionGate = true),
        Question("b14", "如果没有任何人的期待或压力，你是否仍然愿意去做这件事？", "自主意愿", yesScore = 3, noScore = -2),
        Question("b15", "如果现在选择不做，你以后是否因为错过这次机会而后悔？", "错过后悔", yesScore = 2, noScore = -1),
        Question("b16", "你做这件事会发自内心地开心吗？", "内心快乐", yesScore = 2, noScore = -1),
    )

    fun questionsOf(mode: QuestionMode): List<Question> =
        when (mode) {
            QuestionMode.SMALL -> SMALL
            QuestionMode.BIG -> BIG
        }
}
