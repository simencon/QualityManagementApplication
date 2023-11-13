package com.simenko.qmapp.room.entities.products

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.*
import com.simenko.qmapp.retrofit.entities.products.*
import com.simenko.qmapp.room.contract.DatabaseBaseModel
import com.simenko.qmapp.room.entities.DatabaseCompany
import com.simenko.qmapp.room.entities.DatabaseDepartment
import com.simenko.qmapp.room.entities.DatabaseEmployee
import com.simenko.qmapp.utils.ObjectTransformer

@Entity(
    tableName = "0_manufacturing_project",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCompany::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("companyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("factoryLocationDep"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseEmployee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("processOwner"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class DatabaseProductLine(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(index = true)
    var companyId: ID,
    @ColumnInfo(index = true)
    var factoryLocationDep: ID,
    var factoryLocationDetails: String? = null,
    var customerName: String? = null,
    var team: ID? = null,
    var modelYear: String? = null,
    var projectSubject: String? = null,
    var startDate: String? = null,
    var revisionDate: String? = null,
    var refItem: String? = null,
    var pfmeaNum: String? = null,
    @ColumnInfo(index = true)
    var processOwner: ID,
    var confLevel: ID? = null
) : DatabaseBaseModel<NetworkProductLine, DomainProductLine> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductLine::class, NetworkProductLine::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductLine::class, DomainProductLine::class).transform(this)

    @DatabaseView(
        viewName = "product_line_complete",
        value = "SELECT * FROM `0_manufacturing_project` ORDER BY id;"
    )
    data class DatabaseProductLineComplete(
        @Embedded
        val manufacturingProject: DatabaseProductLine,
        @Relation(
            entity = DatabaseCompany::class,
            parentColumn = "companyId",
            entityColumn = "id"
        )
        val company: DatabaseCompany,
        @Relation(
            entity = DatabaseDepartment::class,
            parentColumn = "factoryLocationDep",
            entityColumn = "id"
        )
        val designDepartment: DatabaseDepartment,
        @Relation(
            entity = DatabaseEmployee::class,
            parentColumn = "processOwner",
            entityColumn = "id"
        )
        val designManager: DatabaseEmployee
    ) : DatabaseBaseModel<Any?, DomainProductLine.DomainProductLineComplete> {
        override fun getRecordId() = manufacturingProject.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainProductLine.DomainProductLineComplete(
            manufacturingProject = this.manufacturingProject.toDomainModel(),
            company = this.company.toDomainModel(),
            designDepartment = this.designDepartment.toDomainModel(),
            designManager = this.designManager.toDomainModel()
        )
    }
}

@Entity(
    tableName = "0_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseKey(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var projectId: ID?,
    var componentKey: String?,
    var componentKeyDescription: String?
) : DatabaseBaseModel<NetworkKey, DomainKey> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseKey::class, NetworkKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseKey::class, DomainKey::class).transform(this)

    @DatabaseView(
        viewName = "keys_complete",
        value = "select * from `0_keys`"
    )
    data class DatabaseKeyComplete(
        @Embedded
        val productLineKey: DatabaseKey,
        @Relation(
            entity = DatabaseProductLine::class,
            parentColumn = "projectId",
            entityColumn = "id"
        )
        val productLine: DatabaseProductLine,
    ) : DatabaseBaseModel<Any?, DomainKey.DomainKeyComplete> {
        override fun getRecordId() = productLineKey.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainKey.DomainKeyComplete(
            productLineKey = this.productLineKey.toDomainModel(),
            productLine = this.productLine.toDomainModel()
        )

    }
}

@Entity(
    tableName = "0_products_bases",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseProductBase(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var projectId: ID?,
    var componentBaseDesignation: String?
) : DatabaseBaseModel<NetworkProductBase, DomainProductBase> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductBase::class, NetworkProductBase::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductBase::class, DomainProductBase::class).transform(this)
}

@Entity(
    tableName = "1_product_kinds",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseProductKind(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val projectId: ID,
    val productKindDesignation: String,
    val comments: String?
) : DatabaseBaseModel<NetworkProductKind, DomainProductKind> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductKind::class, NetworkProductKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductKind::class, DomainProductKind::class).transform(this)

    @DatabaseView(
        viewName = "product_kinds_complete",
        value = "select * from `1_product_kinds`"
    )
    data class DatabaseProductKindComplete(
        @Embedded
        val productKind: DatabaseProductKind,
        @Relation(
            entity = DatabaseProductLine.DatabaseProductLineComplete::class,
            parentColumn = "projectId",
            entityColumn = "id"
        )
        val productLine: DatabaseProductLine.DatabaseProductLineComplete
    ) : DatabaseBaseModel<Any?, DomainProductKind.DomainProductKindComplete> {
        override fun getRecordId() = productKind.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainProductKind.DomainProductKindComplete(
            productKind = productKind.toDomainModel(),
            productLine = productLine.toDomainModel()
        )
    }
}

@Entity(
    tableName = "3_component_kinds",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentKind(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val productKindId: ID,
    val componentKindOrder: Int,
    val componentKindDescription: String
) : DatabaseBaseModel<NetworkComponentKind, DomainComponentKind> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKind::class, NetworkComponentKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKind::class, DomainComponentKind::class).transform(this)
    @DatabaseView(
        viewName = "component_kinds_complete",
        value = "select * from `3_component_kinds` order by componentKindOrder"
    )
    data class DatabaseComponentKindComplete(
        @Embedded
        val componentKind: DatabaseComponentKind,
        @Relation(
            entity = DatabaseProductKind.DatabaseProductKindComplete::class,
            parentColumn = "productKindId",
            entityColumn = "id"
        )
        val productKind: DatabaseProductKind.DatabaseProductKindComplete
    ) : DatabaseBaseModel<Any?, DomainComponentKind.DomainComponentKindComplete> {
        override fun getRecordId() = componentKind.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainComponentKind.DomainComponentKindComplete(
            componentKind = componentKind.toDomainModel(),
            productKind = productKind.toDomainModel()
        )
    }
}

@Entity(
    tableName = "5_component_stage_kinds",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentStageKind(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val componentKindId: ID,
    val componentStageOrder: Int,
    val componentStageDescription: String
) : DatabaseBaseModel<NetworkComponentStageKind, DomainComponentStageKind> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStageKind::class, NetworkComponentStageKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStageKind::class, DomainComponentStageKind::class).transform(this)
    @DatabaseView(
        viewName = "component_stage_kinds_complete",
        value = "select * from `5_component_stage_kinds` order by componentStageOrder"
    )
    data class DatabaseComponentStageKindComplete(
        @Embedded
        val componentStageKind: DatabaseComponentStageKind,
        @Relation(
            entity = DatabaseComponentKind.DatabaseComponentKindComplete::class,
            parentColumn = "componentKindId",
            entityColumn = "id"
        )
        val componentKind: DatabaseComponentKind.DatabaseComponentKindComplete
    ) : DatabaseBaseModel<Any?, DomainComponentStageKind.DomainComponentStageKindComplete> {
        override fun getRecordId() = componentStageKind.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainComponentStageKind.DomainComponentStageKindComplete(
            componentStageKind = componentStageKind.toDomainModel(),
            componentKind = componentKind.toDomainModel()
        )
    }
}

@Entity(
    tableName = "1_1_product_kind_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseProductKindKey(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val productKindId: ID,
    @ColumnInfo(index = true)
    val keyId: ID
) : DatabaseBaseModel<NetworkProductKindKey, DomainProductKindKey> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductKindKey::class, NetworkProductKindKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductKindKey::class, DomainProductKindKey::class).transform(this)
    @DatabaseView(
        viewName = "product_kind_keys_complete",
        value = "select * from `1_1_product_kind_keys`"
    )
    data class DatabaseProductKindKeyComplete(
        @Embedded
        val productKindKey: DatabaseProductKindKey,
        @Relation(
            entity = DatabaseProductKind.DatabaseProductKindComplete::class,
            parentColumn = "productKindId",
            entityColumn = "id"
        )
        val productKind: DatabaseProductKind.DatabaseProductKindComplete,
        @Relation(
            entity = DatabaseKey.DatabaseKeyComplete::class,
            parentColumn = "keyId",
            entityColumn = "id"
        )
        val key: DatabaseKey.DatabaseKeyComplete
    ) : DatabaseBaseModel<Any?, DomainProductKindKey.DomainProductKindKeyComplete> {
        override fun getRecordId() = productKindKey.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainProductKindKey.DomainProductKindKeyComplete(
            productKindKey = productKindKey.toDomainModel(),
            productKind = productKind.toDomainModel(),
            key = key.toDomainModel()
        )

    }
}

@Entity(
    tableName = "3_1_component_kind_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentKindKey(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val componentKindId: ID,
    @ColumnInfo(index = true)
    val keyId: ID
) : DatabaseBaseModel<NetworkComponentKindKey, DomainComponentKindKey> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKindKey::class, NetworkComponentKindKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKindKey::class, DomainComponentKindKey::class).transform(this)
}

@Entity(
    tableName = "5_1_component_stage_kind_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentStageKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentStageKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentStageKindKey(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val componentStageKindId: ID,
    @ColumnInfo(index = true)
    val keyId: ID
) : DatabaseBaseModel<NetworkComponentStageKindKey, DomainComponentStageKindKey> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStageKindKey::class, NetworkComponentStageKindKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStageKindKey::class, DomainComponentStageKindKey::class).transform(this)
}

@Entity(
    tableName = "2_products",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductBase::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productBaseId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseProduct(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var productBaseId: ID?,
    @ColumnInfo(index = true)
    var keyId: ID?,
    var productDesignation: String?
) : DatabaseBaseModel<NetworkProduct, DomainProduct> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProduct::class, NetworkProduct::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProduct::class, DomainProduct::class).transform(this)
}

@Entity(
    tableName = "4_components",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseComponent(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var keyId: ID?,
    var componentDesignation: String?,
    var ifAny: Int?
) : DatabaseBaseModel<NetworkComponent, DomainComponent> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponent::class, NetworkComponent::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponent::class, DomainComponent::class).transform(this)
}

@Entity(
    tableName = "6_components_in_stages",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseComponentInStage(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var keyId: ID?,
    var componentInStageDescription: String?,
    var ifAny: Int?
) : DatabaseBaseModel<NetworkComponentInStage, DomainComponentInStage> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentInStage::class, NetworkComponentInStage::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentInStage::class, DomainComponentInStage::class).transform(this)
}

@Entity(
    tableName = "1_2_product_kinds_products",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseProduct::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseProductKindProduct(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val productKindId: ID,
    @ColumnInfo(index = true)
    val productId: ID
) : DatabaseBaseModel<NetworkProductKindProduct, DomainProductKindProduct> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductKindProduct::class, NetworkProductKindProduct::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductKindProduct::class, DomainProductKindProduct::class).transform(this)
}

@Entity(
    tableName = "3_4_component_kinds_components",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentKindComponent(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val componentKindId: ID,
    @ColumnInfo(index = true)
    val componentId: ID
) : DatabaseBaseModel<NetworkComponentKindComponent, DomainComponentKindComponent> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKindComponent::class, NetworkComponentKindComponent::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKindComponent::class, DomainComponentKindComponent::class).transform(this)
}

@Entity(
    tableName = "5_6_component_stage_kinds_component_stages",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentStageKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentStageKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponentInStage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentStageId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentStageKindComponentStage(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val componentStageKindId: ID,
    @ColumnInfo(index = true)
    val componentStageId: ID
) : DatabaseBaseModel<NetworkComponentStageKindComponentStage, DomainComponentStageKindComponentStage> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStageKindComponentStage::class, NetworkComponentStageKindComponentStage::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStageKindComponentStage::class, DomainComponentStageKindComponentStage::class).transform(this)
}

@Entity(
    tableName = "2_4_products_components",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProduct::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseProductComponent(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    val countOfComponents: Int,
    @ColumnInfo(index = true)
    val productId: ID,
    @ColumnInfo(index = true)
    val componentId: ID
) : DatabaseBaseModel<NetworkProductComponent, DomainProductComponent> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductComponent::class, NetworkProductComponent::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductComponent::class, DomainProductComponent::class).transform(this)
}

@Entity(
    tableName = "4_6_components_component_stages",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponentInStage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentStageId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentComponentStage(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val componentId: ID,
    @ColumnInfo(index = true)
    val componentStageId: ID
) : DatabaseBaseModel<NetworkComponentComponentStage, DomainComponentComponentStage> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentComponentStage::class, NetworkComponentComponentStage::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentComponentStage::class, DomainComponentComponentStage::class).transform(this)
}

@Entity(tableName = "0_versions_status")
data class DatabaseVersionStatus(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    var statusDescription: String?
) : DatabaseBaseModel<NetworkVersionStatus, DomainVersionStatus> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseVersionStatus::class, NetworkVersionStatus::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseVersionStatus::class, DomainVersionStatus::class).transform(this)
}

@Entity(
    tableName = "9_products_versions",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProduct::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseVersionStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseProductVersion(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var productId: ID,
    var versionDescription: String?,
    var versionDate: String?,
    @ColumnInfo(index = true)
    var statusId: ID?,
    var isDefault: Boolean
) : DatabaseBaseModel<NetworkProductVersion, DomainProductVersion> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductVersion::class, NetworkProductVersion::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductVersion::class, DomainProductVersion::class).transform(this)
}


@Entity(
    tableName = "10_components_versions",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseVersionStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseComponentVersion(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var componentId: ID,
    var versionDescription: String?,
    var versionDate: String?,
    @ColumnInfo(index = true)
    var statusId: ID?,
    var isDefault: Boolean
) : DatabaseBaseModel<NetworkComponentVersion, DomainComponentVersion> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentVersion::class, NetworkComponentVersion::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentVersion::class, DomainComponentVersion::class).transform(this)
}

@Entity(
    tableName = "11_component_in_stage_versions",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentInStage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentInStageId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseVersionStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseComponentInStageVersion(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var componentInStageId: ID,
    var versionDescription: String?,
    var versionDate: String?,
    @ColumnInfo(index = true)
    var statusId: ID?,
    var isDefault: Boolean
) : DatabaseBaseModel<NetworkComponentInStageVersion, DomainComponentInStageVersion> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentInStageVersion::class, NetworkComponentInStageVersion::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentInStageVersion::class, DomainComponentInStageVersion::class).transform(this)
}

@DatabaseView(
    viewName = "items",
    value = "SELECT p.id as id, ('p'|| p.Id) as fId, p.keyId, p.productDesignation as itemDesignation FROM `2_products` AS p " +
            "UNION ALL " +
            "SELECT c.id as id, ('c'|| c.Id) as fId, c.keyId, c.componentDesignation as itemDesignation FROM `4_components` AS c " +
            "UNION ALL " +
            "SELECT s.id as id, ('s'|| s.Id) as fId, s.keyId, s.componentInStageDescription as itemDesignation FROM `6_components_in_stages` AS s;"
)
data class DatabaseItem(
    val id: ID,
    val fId: String,
    val keyId: ID?,
    val itemDesignation: String?
) : DatabaseBaseModel<Any?, DomainItem> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItem::class, DomainItem::class).transform(this)
}

@DatabaseView(
    viewName = "item_versions",
    value = "SELECT pv.id as id, ('p'|| pv.id) as fId, pv.productId as itemId, ('p'|| pv.productId) as fItemId, pv.versionDescription, pv.versionDate, pv.statusId, pv.isDefault FROM `9_products_versions` AS pv " +
            "UNION ALL " +
            "SELECT cv.id as id, ('c'|| cv.id) as fId, cv.componentId as itemId, ('c'|| cv.componentId) as fItemId, cv.versionDescription, cv.versionDate, cv.statusId, cv.isDefault FROM `10_components_versions` AS cv " +
            "UNION ALL " +
            "SELECT sv.id as id, ('s'|| sv.id) as fId, sv.componentInStageId as itemId, ('s'|| sv.componentInStageId) as fItemId, sv.versionDescription, sv.versionDate, sv.statusId, sv.isDefault FROM `11_component_in_stage_versions` AS sv;"
)
data class DatabaseItemVersion(
    val id: ID,
    val fId: String,
    val itemId: ID,
    val fItemId: String,
    val versionDescription: String?,
    val versionDate: String?,
    val statusId: ID?,
    val isDefault: Boolean
) : DatabaseBaseModel<Any?, DomainItemVersion> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItemVersion::class, DomainItemVersion::class).transform(this)
}

@DatabaseView(
    viewName = "items_complete",
    value = "SELECT * FROM `items`"
)
data class DatabaseItemComplete(
    @Embedded
    val item: DatabaseItem,
    @Relation(
        parentColumn = "keyId",
        entityColumn = "id"
    )
    val key: DatabaseKey,
    @Relation(
        parentColumn = "fId",
        entityColumn = "fItemId"
    )
    val itemToLines: List<DatabaseItemToLine>
) : DatabaseBaseModel<Any?, DomainItemComplete> {
    override fun getRecordId() = item.id
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainItemComplete(
        item = item.toDomainModel(),
        key = key.toDomainModel(),
        itemToLines = itemToLines.map { it.toDomainModel() }
    )
}

@DatabaseView(
    viewName = "item_versions_complete",
    value = "SELECT * FROM `item_versions`"
)
data class DatabaseItemVersionComplete(
    @Embedded
    val itemVersion: DatabaseItemVersion,
    @Relation(
        parentColumn = "statusId",
        entityColumn = "id"
    )
    val versionStatus: DatabaseVersionStatus,
    @Relation(
        parentColumn = "fItemId",
        entityColumn = "fId"
    )
    val itemComplete: DatabaseItemComplete
) : DatabaseBaseModel<Any?, DomainItemVersionComplete> {
    override fun getRecordId() = itemVersion.id
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainItemVersionComplete(
        itemVersion = itemVersion.toDomainModel(),
        versionStatus = versionStatus.toDomainModel(),
        itemComplete = itemComplete.toDomainModel()
    )
}