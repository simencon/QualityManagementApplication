package com.simenko.qmapp.utils

import com.simenko.qmapp.domain.DomainOrderComplete

object InvestigationsUtils {
    fun List<DomainOrderComplete>.getCurrentOrdersRange():Pair<Int,Int> =
        Pair(this[lastIndex].order.id, this[0].order.id)
}