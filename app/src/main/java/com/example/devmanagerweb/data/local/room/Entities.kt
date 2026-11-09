package com.example.devmanagerweb.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverter
import com.example.devmanagerweb.domain.model.Perfil
import com.example.devmanagerweb.domain.model.StatusFatura
import com.example.devmanagerweb.domain.model.StatusOrcamento
import com.example.devmanagerweb.domain.model.StatusProjeto

class Converters {
    @TypeConverter
    fun fromPerfil(value: Perfil) = value.name
    @TypeConverter
    fun toPerfil(value: String) = enumValueOf<Perfil>(value)

    @TypeConverter
    fun fromStatusProjeto(value: StatusProjeto) = value.name
    @TypeConverter
    fun toStatusProjeto(value: String) = enumValueOf<StatusProjeto>(value)

    @TypeConverter
    fun fromStatusOrcamento(value: StatusOrcamento) = value.name
    @TypeConverter
    fun toStatusOrcamento(value: String) = enumValueOf<StatusOrcamento>(value)

    @TypeConverter
    fun fromStatusFatura(value: StatusFatura) = value.name
    @TypeConverter
    fun toStatusFatura(value: String) = enumValueOf<StatusFatura>(value)
}

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val email: String,
    val perfil: Perfil,
    val especialidade: String
)

@Entity(
    tableName = "clientes",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class ClienteEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val origemLead: String,
    val ativo: Boolean = true
)

@Entity(
    tableName = "projetos",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)
data class ProjetoEntity(
    @PrimaryKey val id: String,
    val clienteId: String,
    val titulo: String,
    val descricao: String,
    val tecnologias: String,
    val prazo: String,
    val status: StatusProjeto,
    val urlEntrega: String? = null,
    val faseFunil: String
)

@Entity(
    tableName = "orcamentos",
    foreignKeys = [
        ForeignKey(
            entity = ProjetoEntity::class,
            parentColumns = ["id"],
            childColumns = ["projetoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projetoId")]
)
data class OrcamentoEntity(
    @PrimaryKey val id: String,
    val projetoId: String,
    val escopo: String,
    val valor: Double,
    val status: StatusOrcamento
)

@Entity(
    tableName = "faturas",
    foreignKeys = [
        ForeignKey(
            entity = ProjetoEntity::class,
            parentColumns = ["id"],
            childColumns = ["projetoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projetoId")]
)
data class FaturaEntity(
    @PrimaryKey val id: String,
    val projetoId: String,
    val valor: Double,
    val vencimento: String,
    val status: StatusFatura
)

@Entity(
    tableName = "historico_comunicacao",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)
data class HistoricoComunicacaoEntity(
    @PrimaryKey val id: String,
    val clienteId: String,
    val data: String,
    val descricao: String
)

