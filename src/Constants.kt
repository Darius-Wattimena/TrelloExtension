package nl.teqplay.trelloextension

object Constants {
    const val TRELLO_API_BASEURL = "https://api.trello.com/1"
    const val SLACK_API_BASEURL = "https://slack.com/api"
    const val SYNC_SUCCESS_RESPONSE = "Sync Successful"

    //TODO move the list names to environment variables
    const val NICETOHAVE_LIST_NAME = "2weeks-nicetohave"
    const val PRIO_LIST_NAME = "2weeks-prio"
    const val DOING_LIST_NAME = "doing"
    const val REVIEWING_LIST_NAME = "gandalf"
    const val TESTING_LIST_NAME = "testing"
    const val READY_LIST_NAME = "ready for production"
    const val DONE_LIST_NAME = "done"
    const val IMPEDIMENTS_LIST_NAME = "impediments"
}