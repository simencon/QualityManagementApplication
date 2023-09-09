package com.simenko.qmapp.other

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

object Constants {

    val CARDS_PADDING: PaddingValues = PaddingValues(all = 2.dp)
    const val ACTION_ITEM_SIZE = 45
    const val CARD_HEIGHT = 45
    const val CARD_OFFSET = 90f//135f

    const val ANIMATION_DURATION = 500
    const val MIN_DRAG_AMOUNT = 6

    /**
     * To update local DB only in this range of Order Numbers
     * */
    const val BTN_ORDER_ID = 1681452721020L
    const val TOP_ORDER_ID = 1684923458020L

    const val UI_SAFETY_GAP = 100
    const val UI_TOTAL_VISIBLE = 10

    const val INITIAL_UPDATE_PERIOD_H = 24L

    const val SYNC_NOTIFICATION_CHANNEL_ID = "sync_notification_channel_qm_app"
    const val SYNC_NOTIFICATION_CHANNEL_NAME = "Entity synchronization"

    const val NOTIFICATION_ID_KEY = "notificationIdKey"
    const val NOTIFICATION_ID = 0

    const val DATABASE_NAME = "QualityManagementDB"

    /**
     * teamMembersTesting - to test with real table on DB side
     * teamMembers - the real one
     * */

    const val DEFAULT_REST_API_URL = "https://no.api.yet.com"

    const val BASED_ON_ACCESS = "basedOnAccess"

    const val PRINCIPLES = "principles"
    const val COMPANY_DATA = "companyData"
    const val ROLES = "roles"

    const val EMPLOYEES = "employees"
    const val COMPANIES = "companies"
    const val JOB_ROLES = "positionLevels"
    const val DEPARTMENTS = "departments"
    const val SUB_DEPARTMENTS = "subDepartments"
    const val MANUFACTURING_CHANNELS = "manufacturingChannels"
    const val MANUFACTURING_LINES = "manufacturingLines"
    const val MANUFACTURING_OPERATIONS = "manufacturingOperations"
    const val MANUFACTURING_OPERATIONS_FLOWS = "manufacturingOperationsFlows"

    const val MANUFACTURING_PROJECTS = "manufacturingProjects"
    const val PRODUCTS_KEYS = "productsKeys"
    const val PRODUCT_BASES = "productBases"
    const val CHARACTERISTICS_GROUPS = "characteristicGroups"
    const val CHARACTERISTICS_SUB_GROUPS = "characteristicSubGroups"
    const val CHARACTERISTICS = "characteristics"
    const val METRICS = "metrics"
    const val VERSION_STATUSES = "versionStatuses"
    const val PRODUCTS = "products"
    const val PRODUCT_VERSIONS = "productVersions"
    const val PRODUCT_TOLERANCES = "productTolerances"
    const val PRODUCTS_TO_LINES = "productsToLines"
    const val COMPONENTS = "components"
    const val COMPONENT_VERSIONS = "componentVersions"
    const val COMPONENT_TOLERANCES = "componentTolerances"
    const val COMPONENTS_TO_LINES = "componentsToLines"
    const val COMPONENTS_IN_STAGE = "componentsInStage"
    const val COMPONENT_IN_STAGE_VERSIONS = "componentInStageVersions"
    const val COMPONENT_IN_STAGE_TOLERANCES = "componentInStageTolerances"
    const val COMPONENTS_IN_STAGE_TO_LINES = "componentsInStageToLines"

    const val INPUT_TO_PLACE_INVESTIGATION = "inputToPlaceInvestigation"
    const val INVESTIGATION_TYPES = "investigationTypes"
    const val INVESTIGATION_REASONS = "investigationReasons"
    const val INVESTIGATION_STATUSES = "investigationStatuses"
    const val RESULT_DECRYPTIONS = "resultsDecryptions"

    const val RECORDS = "records"
    const val HASH_CODE = "hashCode"

    const val ORDERS = "orders"
    const val LATEST_ORDER = "latestOrder"
    const val EARLIEST_ORDER = "earliestOrder"

    const val SUB_ORDERS = "subOrders"
    const val SUB_ORDER_TASKS = "subOrderTasks"
    const val SAMPLES = "samples"

    const val RESULTS = "results"
    const val RESULT_TASK = "task"

    const val MEASUREMENTS_REGISTER = "measurementsRegister"
    const val MEASUREMENT_RESULTS = "measurementResults"
}