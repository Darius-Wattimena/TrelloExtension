package nl.teqplay.trelloextension.service.slack

import nl.teqplay.trelloextension.datasource.CardDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.SlackCall
import nl.teqplay.trelloextension.model.trello.Card
import nl.teqplay.trelloextension.model.slack.Attachment
import nl.teqplay.trelloextension.model.slack.AttachmentField
import nl.teqplay.trelloextension.service.BaseRequest

class SendStuckTestingCardsToSlack(token: String, private val boardId: String, private val testingListId: String, private val minimumDaysInList: Int) : BaseRequest<Unit>() {
    val slackCall = SlackCall(token)
    val db = Database.instance

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

        for (stuckTestingCard in stuckTestingCards) {
            val attachment = createAttachment(stuckTestingCard)
            attachments.add(attachment)
        }

        if (attachments.count() == 0) {
            attachments.add(createPositiveAttachment())
        }

        val jsonAttachments = gson.toJson(attachments.toTypedArray())
        slackCall.parameters["text"] = "Daily Testing Status Report"
        slackCall.parameters["attachments"] = jsonAttachments
        slackCall.execute(client)
    }

    private fun createPositiveAttachment() : Attachment {
        return Attachment(
            "Testing Status",
            "good",
            "Nothing to test!",
            "",
            emptyArray()
        )
    }

    private fun createAttachment(card: Card) : Attachment {
        val fields = mutableListOf<AttachmentField>()

        fields.add(createBaseField(card.name))
        fields.add(createDaysInListField(card.daysInList))
        fields.add(createAssignedField(card))

        val attachmentColor = if (card.daysInList >= 5) "danger" else "warning"
        return Attachment(
            "Testing Status",
            attachmentColor,
            "Card Link",
            card.url,
            fields.toTypedArray()
        )
    }

    private fun createBaseField(cardName: String) : AttachmentField {
        return AttachmentField(
            "Card Name",
            cardName
        )
    }

    private fun createDaysInListField(daysInList: Int) : AttachmentField {
        return AttachmentField(
            "Stuck on testing",
            "$daysInList days"
        )
    }

    private fun createAssignedField(card: Card) : AttachmentField {
        val formattedMembers = formatMembers(card)

        return AttachmentField(
            "Assigned to",
            formattedMembers
        )
    }

    private fun formatMembers(card: Card) : String {
        var result = ""

        if (card.members == null) return "nobody?"

        val memberNames = emptyList<String>().toMutableList()

        for (member in card.members!!) {
            memberNames.add(member.fullName)
        }

        result += memberNames.joinToString("\n")

        return result
    }
}