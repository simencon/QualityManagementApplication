package com.simenko.qmapp.other

object Constants {

    const val ACTION_ITEM_SIZE = 45
    const val CARD_HEIGHT = 45
    const val CARD_OFFSET = 90f//135f

    const val ANIMATION_DURATION = 500
    const val MIN_DRAG_AMOUNT = 6

    const val DATABASE_NAME = "QualityManagementDB"

    const val BASE_URL = "https://qualityappspring.azurewebsites.net/api/v1/"

    /**
     * teamMembersTesting - to test with real table on DB side
     * teamMembers - the real one
     * */

    const val TEAM_URL = "teamMembers"

    /**
     * To update local DB only in this range of Order Numbers
     * */
    const val BTN_ORDER_ID = 1681452721020L
    const val TOP_ORDER_ID = 1684923458020L

    const val UI_SAFETY_GAP = 100
    const val UI_TOTAL_VISIBLE = 10
}