package nl.teqplay.trelloextension.helper

import nl.teqplay.trelloextension.model.slack.Attachment
import nl.teqplay.trelloextension.model.slack.AttachmentField
import nl.teqplay.trelloextension.model.trello.Card

class AttachmentBuilder {

    fun buildPositiveTestingReportAttachment(): Attachment {
        return Attachment(
            "Testing Status",
            "good",
            "Nothing to test!",
            ""
        )
    }

    fun buildTestingReportAttachment(card: Card): Attachment {
        val attachmentColor = if (card.daysInList >= 5) "danger" else "warning"
        val formattedMembers = formatMembers(card)

        val attachment = Attachment(
            "Testing Status",
            attachmentColor,
            "Card Link",
            card.url
        )

        attachment.addField(AttachmentField("Card Name", card.name))
        attachment.addField(AttachmentField("Stuck on testing", "${card.daysInList} days"))
        attachment.addField(AttachmentField("Assigned to", formattedMembers))
        attachment.addField(AttachmentField("Date added to board", card.dateAdded))

        return attachment
    }

    private fun formatMembers(card: Card): String {
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