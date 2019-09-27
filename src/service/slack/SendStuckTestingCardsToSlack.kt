package nl.teqplay.trelloextension.service.slack

import nl.teqplay.trelloextension.datasource.CardDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.AttachmentBuilder
import nl.teqplay.trelloextension.helper.SlackCall
import nl.teqplay.trelloextension.model.slack.Attachment
import nl.teqplay.trelloextension.service.BaseRequest

class SendStuckTestingCardsToSlack(
    private val token: String,
    private val boardId: String,
    private val testingListId: String,
    private val minimumDaysInList: Int
) : BaseRequest<Unit>() {
    private val slackCall = SlackCall(token)
    private val db = Database.instance

    override fun prepare() {
        slackCall.request = "/chat.postMessage"
        slackCall.parameters["channel"] = "CNPGUTFTR"
    }

    override suspend fun execute() {
        val stuckTestingCards = CardDataSource.findAllCardsOfList(
            boardId,
            testingListId,
            minimumDaysInList,
            db
        ).sortedBy { it.daysInList }.reversed()

        val attachments = mutableListOf<Attachment>()
        val builder = AttachmentBuilder()

        var totalAdded = 0
        for (stuckTestingCard in stuckTestingCards) {
            if (totalAdded == 3) break
            totalAdded++
            attachments.add(builder.buildTestingReportAttachment(stuckTestingCard))
        }

        if (attachments.count() == 0) {
            attachments.add(builder.buildPositiveTestingReportAttachment())
        }

        val jsonAttachments = gson.toJson(attachments.toTypedArray())
        slackCall.parameters["text"] = "Daily Testing Status Report"
        slackCall.parameters["attachments"] = jsonAttachments
        slackCall.execute(client)

        val moreToTest = stuckTestingCards.count() - totalAdded

        if (moreToTest > 0) {
            val extraSlackCall = SlackCall(token)
            extraSlackCall.request = "/chat.postMessage"
            extraSlackCall.parameters["channel"] = "CNPGUTFTR"
            extraSlackCall.parameters["text"] = "and $moreToTest other cards to test"
            extraSlackCall.execute(client)
        }
    }
}