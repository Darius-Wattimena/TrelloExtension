package nl.teqplay.trelloextension.helper

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.model.BoardLists
import nl.teqplay.trelloextension.model.trello.List
import nl.teqplay.trelloextension.service.list.GetBoardLists

object BoardHelper {
    suspend fun getBoardLists(boardId: String, key: String, token: String) : BoardLists {
        val lists = GetBoardLists(
            boardId, key, token
        ).execute()

        return createBoardLists(lists)
    }

    fun createBoardLists(lists: Array<List>) : BoardLists {
        var niceToHaveListId = ""
        var prioListId = ""
        var doingListId = ""
        var reviewingListId = ""
        var testingListId = ""
        var readyListId = ""
        var impedimentsListId = ""
        var doneListId = ""

        val potentialDoneLists = mutableListOf<List>()

        for (list in lists) {
            when (list.name.toLowerCase()) {
                Constants.NICETOHAVE_LIST_NAME -> niceToHaveListId = list.id
                Constants.PRIO_LIST_NAME -> prioListId = list.id
                Constants.DOING_LIST_NAME -> doingListId = list.id
                Constants.REVIEWING_LIST_NAME -> reviewingListId = list.id
                Constants.TESTING_LIST_NAME -> testingListId = list.id
                Constants.READY_LIST_NAME -> readyListId = list.id
                Constants.IMPEDIMENTS_LIST_NAME -> impedimentsListId = list.id
                else -> {
                    if (list.name.toLowerCase().contains(Constants.DONE_LIST_NAME)) {
                        potentialDoneLists.add(list)
                    }
                }
            }
        }

        var listWithHighestNumber : List? = null
        var numberOfHighestList = 0

        val regex = Regex("""([0-9])\w+""")
        for (list in potentialDoneLists) {
            val result = regex.find(list.name)
            if (result != null) {
                val currentListNumber = result.value.toInt()
                if (currentListNumber > numberOfHighestList) {
                    listWithHighestNumber = list
                    numberOfHighestList = currentListNumber
                }
            }
        }

        if (listWithHighestNumber != null) {
            doneListId = listWithHighestNumber.id
        }

        return BoardLists(
            niceToHaveListId,
            prioListId,
            doingListId,
            reviewingListId,
            testingListId,
            readyListId,
            impedimentsListId,
            doneListId
        )
    }
}