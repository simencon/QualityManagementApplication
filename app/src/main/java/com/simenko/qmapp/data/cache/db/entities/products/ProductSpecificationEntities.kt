package com.simenko.qmapp.data.cache.db.entities.products

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.simenko.qmapp.data.cache.db.contract.DatabaseBaseModel
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.*
import com.simenko.qmapp.data.remote.entities.products.*
import com.simenko.qmapp.data.cache.db.entities.DatabaseCompany
import com.simenko.qmapp.data.cache.db.entities.DatabaseDepartment
import com.simenko.qmapp.data.cache.db.entities.DatabaseEmployee
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
    val id: ID,
    @ColumnInfo(index = true)
    val companyId: ID,
    @ColumnInfo(index = true)
    val factoryLocationDep: ID,
    val factoryLocationDetails: String? = null,
    val customerName: String? = null,
    val team: ID? = null,
    val modelYear: String? = null,
    val projectSubject: String? = null,
    val startDate: String? = null,
    val revisionDate: String? = null,
    val refItem: String? = null,
    val pfmeaNum: String? = null,
    @ColumnInfo(index = true)
    val processOwner: ID,
    val confLevel: ID? = null
) : DatabaseBaseModel<NetworkProductLine, DomainProductLine, ID, ID> {
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
    ) : DatabaseBaseModel<Any?, DomainProductLine.DomainProductLineComplete, ID, ID> {
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
    val id: ID,
    @ColumnInfo(index = true)
    val projectId: ID,
    val componentKey: String,
    val componentKeyDescription: String?
) : DatabaseBaseModel<NetworkKey, DomainKey, ID, ID> {
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
    ) : DatabaseBaseModel<Any?, DomainKey.DomainKeyComplete, ID, ID> {
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
    val id: ID,
    @ColumnInfo(index = true)
    val projectId: ID?,
    val componentBaseDesignation: String?
) : DatabaseBaseModel<NetworkProductBase, DomainProductBase, ID, ID> {
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
) : DatabaseBaseModel<NetworkProductKind, DomainProductKind, ID, ID> {
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
    ) : DatabaseBaseModel<Any?, DomainProductKind.DomainProductKindComplete, ID, ID> {
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
    val componentKindDescription: String,
    val quantityUnits: String
) : DatabaseBaseModel<NetworkComponentKind, DomainComponentKind, ID, ID> {
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
    ) : DatabaseBaseModel<Any?, DomainComponentKind.DomainComponentKindComplete, ID, ID> {
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
    val componentStageDescription: String,
    val quantityUnits: String
) : DatabaseBaseModel<NetworkComponentStageKind, DomainComponentStageKind, ID, ID> {
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
    ) : DatabaseBaseModel<Any?, DomainComponentStageKind.DomainComponentStageKindComplete, ID, ID> {
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
) : DatabaseBaseModel<NetworkProductKindKey, DomainProductKindKey, ID, ID> {
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
    ) : DatabaseBaseModel<Any?, DomainProductKindKey.DomainProductKindKeyComplete, ID, ID> {
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
) : DatabaseBaseModel<NetworkComponentKindKey, DomainComponentKindKey, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKindKey::class, NetworkComponentKindKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKindKey::class, DomainComponentKindKey::class).transform(this)

    @DatabaseView(
        viewName = "component_kind_keys_complete",
        value = "select * from `3_1_component_kind_keys`"
    )
    data class DatabaseComponentKindKeyComplete(
        @Embedded
        val componentKindKey: DatabaseComponentKindKey,
        @Relation(
            entity = DatabaseComponentKind.DatabaseComponentKindComplete::class,
            parentColumn = "componentKindId",
            entityColumn = "id"
        )
        val componentKind: DatabaseComponentKind.DatabaseComponentKindComplete,
        @Relation(
            entity = DatabaseKey.DatabaseKeyComplete::class,
            parentColumn = "keyId",
            entityColumn = "id"
        )
        val key: DatabaseKey.DatabaseKeyComplete
    ) : DatabaseBaseModel<Any?, DomainComponentKindKey.DomainComponentKindKeyComplete, ID, ID> {
        override fun getRecordId() = componentKindKey.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainComponentKindKey.DomainComponentKindKeyComplete(
            componentKindKey = componentKindKey.toDomainModel(),
            componentKind = componentKind.toDomainModel(),
            key = key.toDomainModel()
        )
    }
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
) : DatabaseBaseModel<NetworkComponentStageKindKey, DomainComponentStageKindKey, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStageKindKey::class, NetworkComponentStageKindKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStageKindKey::class, DomainComponentStageKindKey::class).transform(this)

    @DatabaseView(
        viewName = "component_stage_kind_keys_complete",
        value = "select * from `5_1_component_stage_kind_keys`"
    )
    data class DatabaseComponentStageKindKeyComplete(
        @Embedded
        val componentStageKindKey: DatabaseComponentStageKindKey,
        @Relation(
            entity = DatabaseComponentStageKind.DatabaseComponentStageKindComplete::class,
            parentColumn = "componentStageKindId",
            entityColumn = "id"
        )
        val componentStageKind: DatabaseComponentStageKind.DatabaseComponentStageKindComplete,
        @Relation(
            entity = DatabaseKey.DatabaseKeyComplete::class,
            parentColumn = "keyId",
            entityColumn = "id"
        )
        val key: DatabaseKey.DatabaseKeyComplete
    ) : DatabaseBaseModel<Any?, DomainComponentStageKindKey.DomainComponentStageKindKeyComplete, ID, ID> {
        override fun getRecordId() = componentStageKindKey.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainComponentStageKindKey.DomainComponentStageKindKeyComplete(
            componentStageKindKey = componentStageKindKey.toDomainModel(),
            componentStageKind = componentStageKind.toDomainModel(),
            key = key.toDomainModel()
        )
    }
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
    val id: ID,
    @ColumnInfo(index = true)
    val productBaseId: ID,
    @ColumnInfo(index = true)
    val keyId: ID,
    val productDesignation: String
) : DatabaseBaseModel<NetworkProduct, DomainProduct, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProduct::class, NetworkProduct::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProduct::class, DomainProduct::class).transform(this)

    @DatabaseView(
        viewName = "products_complete",
        value = "select * from `2_products`"
    )
    data class DatabaseProductComplete(
        @Embedded
        val product: DatabaseProduct,
        @Relation(
            entity = DatabaseProductBase::class,
            parentColumn = "productBaseId",
            entityColumn = "id"
        )
        val productBase: DatabaseProductBase,
        @Relation(
            entity = DatabaseKey::class,
            parentColumn = "keyId",
            entityColumn = "id"
        )
        val key: DatabaseKey
    ) : DatabaseBaseModel<Any?, DomainProduct.DomainProductComplete, ID, ID> {
        override fun getRecordId() = product.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainProduct.DomainProductComplete(
            product = this.product.toDomainModel(),
            productBase = this.productBase.toDomainModel(),
            key = this.key.toDomainModel()
        )
    }
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
    val id: ID,
    @ColumnInfo(index = true)
    val keyId: ID,
    val componentDesignation: String,
    val ifAny: Int?
) : DatabaseBaseModel<NetworkComponent, DomainComponent, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponent::class, NetworkComponent::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponent::class, DomainComponent::class).transform(this)

    @DatabaseView(
        viewName = "components_complete",
        value = "select * from `4_components`"
    )
    data class DatabaseComponentComplete(
        @Embedded
        val component: DatabaseComponent,
        @Relation(
            entity = DatabaseKey::class,
            parentColumn = "keyId",
            entityColumn = "id"
        )
        val key: DatabaseKey
    ) : DatabaseBaseModel<Any?, DomainComponent.DomainComponentComplete, ID, ID> {
        override fun getRecordId() = component.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainComponent.DomainComponentComplete(
            component = this.component.toDomainModel(),
            key = this.key.toDomainModel()
        )
    }
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
data class DatabaseComponentStage(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val keyId: ID,
    val componentInStageDescription: String,
    val ifAny: Int?
) : DatabaseBaseModel<NetworkComponentStage, DomainComponentStage, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStage::class, NetworkComponentStage::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStage::class, DomainComponentStage::class).transform(this)
    @DatabaseView(
        viewName = "component_stages_complete",
        value = "select * from `6_components_in_stages`"
    )
    data class DatabaseComponentStageComplete(
        @Embedded
        val componentStage: DatabaseComponentStage,
        @Relation(
            entity = DatabaseKey::class,
            parentColumn = "keyId",
            entityColumn = "id"
        )
        val key: DatabaseKey
    ) : DatabaseBaseModel<Any?, DomainComponentStage.DomainComponentStageComplete, ID, ID> {
        override fun getRecordId() = componentStage.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainComponentStage.DomainComponentStageComplete(
            componentStage = this.componentStage.toDomainModel(),
            key = this.key.toDomainModel()
        )
    }
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
) : DatabaseBaseModel<NetworkProductKindProduct, DomainProductKindProduct, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductKindProduct::class, NetworkProductKindProduct::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductKindProduct::class, DomainProductKindProduct::class).transform(this)

    @DatabaseView(
        viewName = "product_kinds_products_complete",
        value = "select * from `1_2_product_kinds_products`"
    )
    data class DatabaseProductKindProductComplete(
        @Embedded
        val productKindProduct: DatabaseProductKindProduct,
        @Relation(
            entity = DatabaseProductKind::class,
            parentColumn = "productKindId",
            entityColumn = "id"
        )
        val productKind: DatabaseProductKind,
        @Relation(
            entity = DatabaseProduct.DatabaseProductComplete::class,
            parentColumn = "productId",
            entityColumn = "id"
        )
        val product: DatabaseProduct.DatabaseProductComplete,
        @Relation(
            entity = DatabaseProductVersion::class,
            parentColumn = "productId",
            entityColumn = "productId"
        )
        val versions: List<DatabaseProductVersion>
    ) : DatabaseBaseModel<Any?, DomainProductKindProduct.DomainProductKindProductComplete, ID, ID> {
        override fun getRecordId() = productKindProduct.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainProductKindProduct.DomainProductKindProductComplete(
            productKindProduct = this.productKindProduct.toDomainModel(),
            productKind = this.productKind.toDomainModel(),
            product = this.product.toDomainModel(),
            versions = this.versions.map { it.toDomainModel() }
        )
    }
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
) : DatabaseBaseModel<NetworkComponentKindComponent, DomainComponentKindComponent, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKindComponent::class, NetworkComponentKindComponent::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKindComponent::class, DomainComponentKindComponent::class).transform(this)

    @DatabaseView(
        viewName = "component_kinds_components_complete",
        value = "select * from `3_4_component_kinds_components`"
    )
    data class DatabaseComponentKindComponentComplete(
        @Embedded
        val componentKindComponent: DatabaseComponentKindComponent,
        @Relation(
            entity = DatabaseComponentKind::class,
            parentColumn = "componentKindId",
            entityColumn = "id"
        )
        val componentKind: DatabaseComponentKind,
        @Relation(
            entity = DatabaseComponent.DatabaseComponentComplete::class,
            parentColumn = "componentId",
            entityColumn = "id"
        )
        val component: DatabaseComponent.DatabaseComponentComplete,
        @Relation(
            entity = DatabaseComponentVersion::class,
            parentColumn = "componentId",
            entityColumn = "componentId"
        )
        val versions: List<DatabaseComponentVersion>
    ) : DatabaseBaseModel<Any?, DomainComponentKindComponent.DomainComponentKindComponentComplete, ID, ID> {
        override fun getRecordId() = componentKindComponent.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainComponentKindComponent.DomainComponentKindComponentComplete(
            componentKindComponent = this.componentKindComponent.toDomainModel(),
            componentKind = this.componentKind.toDomainModel(),
            component = this.component.toDomainModel(),
            versions = this.versions.map { it.toDomainModel() }
        )
    }
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
            entity = DatabaseComponentStage::class,
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
) : DatabaseBaseModel<NetworkComponentStageKindComponentStage, DomainComponentStageKindComponentStage, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStageKindComponentStage::class, NetworkComponentStageKindComponentStage::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStageKindComponentStage::class, DomainComponentStageKindComponentStage::class).transform(this)
    @DatabaseView(
        viewName = "component_stage_kinds_component_stages_complete",
        value = "select * from `5_6_component_stage_kinds_component_stages`"
    )
    data class DatabaseComponentStageKindComponentStageComplete(
        @Embedded
        val componentStageKindComponentStage: DatabaseComponentStageKindComponentStage,
        @Relation(
            entity = DatabaseComponentStageKind::class,
            parentColumn = "componentStageKindId",
            entityColumn = "id"
        )
        val componentStageKind: DatabaseComponentStageKind,
        @Relation(
            entity = DatabaseComponentStage.DatabaseComponentStageComplete::class,
            parentColumn = "componentStageId",
            entityColumn = "id"
        )
        val componentStage: DatabaseComponentStage.DatabaseComponentStageComplete,
        @Relation(
            entity = DatabaseComponentStageVersion::class,
            parentColumn = "componentStageId",
            entityColumn = "componentInStageId"
        )
        val versions: List<DatabaseComponentStageVersion>
    ) : DatabaseBaseModel<Any?, DomainComponentStageKindComponentStage.DomainComponentStageKindComponentStageComplete, ID, ID> {
        override fun getRecordId() = componentStageKindComponentStage.componentStageId
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainComponentStageKindComponentStage.DomainComponentStageKindComponentStageComplete(
            componentStageKindComponentStage = this.componentStageKindComponentStage.toDomainModel(),
            componentStageKind = this.componentStageKind.toDomainModel(),
            componentStage = this.componentStage.toDomainModel(),
            versions = this.versions.map { it.toDomainModel() }
        )
    }
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
            entity = DatabaseComponentKindComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentKindComponentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseProductComponent(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    val quantity: Float,
    @ColumnInfo(index = true)
    val productId: ID,
    @ColumnInfo(index = true)
    val componentKindComponentId: ID
) : DatabaseBaseModel<NetworkProductComponent, DomainProductComponent, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductComponent::class, NetworkProductComponent::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductComponent::class, DomainProductComponent::class).transform(this)

    @DatabaseView(
        viewName = "products_components_complete",
        value = "select * from `2_4_products_components`"
    )
    data class DatabaseProductComponentComplete(
        @Embedded
        val productComponent: DatabaseProductComponent,
        @Relation(
            entity = DatabaseProduct.DatabaseProductComplete::class,
            parentColumn = "productId",
            entityColumn = "id"
        )
        val product: DatabaseProduct.DatabaseProductComplete,
        @Relation(
            entity = DatabaseComponentKindComponent.DatabaseComponentKindComponentComplete::class,
            parentColumn = "componentKindComponentId",
            entityColumn = "id"
        )
        val component: DatabaseComponentKindComponent.DatabaseComponentKindComponentComplete,
    ) : DatabaseBaseModel<Any?, DomainProductComponent.DomainProductComponentComplete, ID, ID> {
        override fun getRecordId() = productComponent.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainProductComponent.DomainProductComponentComplete(
            productComponent = this.productComponent.toDomainModel(),
            product = this.product.toDomainModel(),
            component = this.component.toDomainModel(),
        )
    }
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
            entity = DatabaseComponentStageKindComponentStage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("stageKindStageId"),
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
    val stageKindStageId: ID,
    val quantity: Float,
) : DatabaseBaseModel<NetworkComponentComponentStage, DomainComponentComponentStage, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentComponentStage::class, NetworkComponentComponentStage::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentComponentStage::class, DomainComponentComponentStage::class).transform(this)
    @DatabaseView(
        viewName = "components_component_stages_complete",
        value = "select * from `4_6_components_component_stages`"
    )
    data class DatabaseComponentComponentStageComplete(
        @Embedded
        val componentComponentStage: DatabaseComponentComponentStage,
        @Relation(
            entity = DatabaseComponent.DatabaseComponentComplete::class,
            parentColumn = "componentId",
            entityColumn = "id"
        )
        val component: DatabaseComponent.DatabaseComponentComplete,
        @Relation(
            entity = DatabaseComponentStageKindComponentStage.DatabaseComponentStageKindComponentStageComplete::class,
            parentColumn = "stageKindStageId",
            entityColumn = "id"
        )
        val componentStage: DatabaseComponentStageKindComponentStage.DatabaseComponentStageKindComponentStageComplete
    ) : DatabaseBaseModel<Any?, DomainComponentComponentStage.DomainComponentComponentStageComplete, ID, ID> {
        override fun getRecordId() = componentComponentStage.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainComponentComponentStage.DomainComponentComponentStageComplete(
            componentComponentStage = this.componentComponentStage.toDomainModel(),
            component = this.component.toDomainModel(),
            componentStage = this.componentStage.toDomainModel(),
        )
    }
}

@Entity(tableName = "0_versions_status")
data class DatabaseVersionStatus(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    val statusDescription: String?
) : DatabaseBaseModel<NetworkVersionStatus, DomainVersionStatus, ID, ID> {
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
    val id: ID,
    @ColumnInfo(index = true)
    val productId: ID,
    val versionDescription: String,
    val versionDate: Long,
    @ColumnInfo(index = true)
    val statusId: ID,
    val isDefault: Boolean
) : DatabaseBaseModel<NetworkProductVersion, DomainProductVersion, ID, ID> {
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
    val id: ID,
    @ColumnInfo(index = true)
    val componentId: ID,
    val versionDescription: String,
    val versionDate: Long,
    @ColumnInfo(index = true)
    val statusId: ID,
    val isDefault: Boolean
) : DatabaseBaseModel<NetworkComponentVersion, DomainComponentVersion, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentVersion::class, NetworkComponentVersion::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentVersion::class, DomainComponentVersion::class).transform(this)
}

@Entity(
    tableName = "11_component_in_stage_versions",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentStage::class,
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
data class DatabaseComponentStageVersion(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val componentInStageId: ID,
    val versionDescription: String,
    val versionDate: Long,
    @ColumnInfo(index = true)
    val statusId: ID,
    val isDefault: Boolean
) : DatabaseBaseModel<NetworkComponentStageVersion, DomainComponentStageVersion, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStageVersion::class, NetworkComponentStageVersion::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStageVersion::class, DomainComponentStageVersion::class).transform(this)
}

@DatabaseView(
    viewName = "itemKinds",
    value = """
        select ('p'||pk.id) as fId, pk.id as id, pk.projectId as pId, 0 as itemKindOrder, pk.productKindDesignation as itemKindDesignation, pk.comments as comments from `1_product_kinds` as pk
        union all
        select ('c'||ck.id) as fId, ck.id as id, ck.productKindId as pId, ck.componentKindOrder as itemKindOrder, ck.componentKindDescription as itemKindDesignation, null as comments from `3_component_kinds` as ck
        union all
        select ('s'||sk.id) as fId, sk.id as id, sk.componentKindId as pId, sk.componentStageOrder as itemKindOrder, sk.componentStageDescription as itemKindDesignation, null as comments from `5_component_stage_kinds` as sk
        """
)
data class DatabaseItemKind(
    val fId: String,
    val id: ID,
    val pId: ID,
    val itemKindOrder: Int,
    val itemKindDesignation: String,
    val comments: String?
): DatabaseBaseModel<Any?, DomainItemKind, String, ID> {
    override fun getRecordId() = fId
    override fun toNetworkModel() =null
    override fun toDomainModel() = ObjectTransformer(DatabaseItemKind::class, DomainItemKind::class).transform(this)
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
) : DatabaseBaseModel<Any?, DomainItem, String, ID> {
    override fun getRecordId() = fId
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
    val versionDate: Long,
    val statusId: ID?,
    val isDefault: Boolean
) : DatabaseBaseModel<Any?, DomainItemVersion, String, ID> {
    override fun getRecordId() = fId
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
) : DatabaseBaseModel<Any?, DomainItemComplete, String, ID> {
    override fun getRecordId() = item.fId
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
) : DatabaseBaseModel<Any?, DomainItemVersionComplete, String, ID> {
    override fun getRecordId() = itemVersion.fId
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainItemVersionComplete(
        itemVersion = itemVersion.toDomainModel(),
        versionStatus = versionStatus.toDomainModel(),
        itemComplete = itemComplete.toDomainModel()
    )
}