package nl.teqplay.trelloextension.model.slack

data class Attachment(
    var fallback: String,
    var color: String,
    var title: String,
    var title_link: String,
    var fields: MutableList<AttachmentField> = mutableListOf()
) {
    fun addField(field: AttachmentField) {
        fields.add(field)
    }
}