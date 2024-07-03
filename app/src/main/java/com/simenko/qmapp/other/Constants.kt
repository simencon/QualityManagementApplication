package com.simenko.qmapp.other

object Constants {

    const val DEFAULT_SPACE = 6
    const val FAB_HEIGHT = 72
    const val ACTION_ITEM_SIZE = 45
    const val CARD_OFFSET = 45f//135f
    const val TOP_TAB_ROW_HEIGHT = 40

    const val ANIMATION_DURATION = 500
    const val MIN_DRAG_AMOUNT = 6

    /**
     * To update local DB only in this range of Order Numbers
     * */

    const val UI_SAFETY_GAP = 100
    const val UI_TOTAL_VISIBLE = 10

    const val SYNC_NOTIFICATION_CHANNEL_ID = "sync_notification_channel_qm_app"
    const val SYNC_NOTIFICATION_CHANNEL_NAME = "Entity synchronization"

    const val DATABASE_NAME = "QualityManagementDB"

    /**
     * teamMembersTesting - to test with real table on DB side
     * teamMembers - the real one
     * */

    const val DEFAULT_REST_API_URL = "https://no.api.yet.com"

    const val BASED_ON_ACCESS = "basedOnAccess"
    const val WITH_RELATED_RECORDS = "withRelatedRecords"

    const val PRINCIPLES = "principles"
    const val COMPANY_DATA = "companyData"
    const val AUTHORIZE_USER = "authorizeUser"
    const val REMOVE_USER = "removeUser"
    const val ROLES = "roles"

    const val EMPLOYEES = "employees"
    const val COMPANIES = "companies"
    const val JOB_ROLES = "jobRoles"
    const val DEPARTMENTS = "departments"
    const val SUB_DEPARTMENTS = "subDepartments"
    const val MANUFACTURING_CHANNELS = "manufacturingChannels"
    const val MANUFACTURING_LINES = "manufacturingLines"
    const val MANUFACTURING_OPERATIONS = "manufacturingOperations"
    const val MANUFACTURING_OPERATIONS_FLOWS = "manufacturingOperationsFlows"

    const val PRODUCT_LINES = "manufacturingProjects"
    const val PRODUCTS_KEYS = "productsKeys"
    const val PRODUCT_BASES = "productBases"
    const val CHARACTERISTICS_GROUPS = "characteristicGroups"
    const val CHARACTERISTICS_SUB_GROUPS = "characteristicSubGroups"
    const val CHARACTERISTICS = "characteristics"
    const val METRICS = "metrics"
    const val VERSION_STATUSES = "versionStatuses"

    const val PRODUCT_KINDS = "productKinds"
    const val COMPONENT_KINDS = "componentKinds"
    const val COMPONENT_STAGE_KINDS = "componentStageKinds"

    const val PRODUCT_KINDS_KEYS = "productKindKeys"
    const val COMPONENT_KINDS_KEYS = "componentKindKeys"
    const val COMPONENT_STAGE_KINDS_KEYS = "componentStageKindKeys"

    const val CHARACTERISTICS_PRODUCT_KINDS = "characteristicsProductKinds"
    const val CHARACTERISTICS_COMPONENT_KINDS = "characteristicsComponentKinds"
    const val CHARACTERISTICS_COMPONENT_STAGE_KINDS = "characteristicsComponentStageKinds"

    const val PRODUCTS = "products"
    const val COMPONENTS = "components"
    const val COMPONENTS_IN_STAGE = "componentsInStage"

    const val PRODUCTS_TO_LINES = "productsToLines"
    const val COMPONENTS_TO_LINES = "componentsToLines"
    const val COMPONENTS_IN_STAGE_TO_LINES = "componentsInStageToLines"

    const val PRODUCT_KINDS_PRODUCTS = "productKindsProducts"
    const val COMPONENT_KINDS_COMPONENTS = "componentKindsComponents"
    const val COMPONENT_STAGE_KINDS_COMPONENT_STAGES = "componentStageKindsComponentStages"

    const val PRODUCTS_COMPONENTS = "productsComponents"
    const val COMPONENTS_COMPONENT_STAGES = "componentsComponentStages"

    const val PRODUCT_VERSIONS = "productVersions"
    const val COMPONENT_VERSIONS = "componentVersions"
    const val COMPONENT_IN_STAGE_VERSIONS = "componentInStageVersions"

    const val PRODUCT_TOLERANCES = "productTolerances"
    const val COMPONENT_TOLERANCES = "componentTolerances"
    const val COMPONENT_IN_STAGE_TOLERANCES = "componentInStageTolerances"

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